package com.fertility.client;

import com.fertility.config.ClientConfigHandler;
import com.fertility.config.CommonConfigHandler;
import com.fertility.networking.PacketHandler;
import com.fertility.networking.RequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    private static final Minecraft mc = Minecraft.getInstance();
    public static final ItemRenderer itemRenderer = mc.getItemRenderer();
    private static int currentX = 0, currentZ = 0;
    private static Biome currentBiome = null;
    public static int lastMessage = 0;
    public static double timer = 0;
    public static ArrayList<ItemStack> renderStackList = new ArrayList<ItemStack>();

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END && mc.player != null && mc.level != null && !mc.options.hideGui && !mc.options.renderDebug && mc.screen == null) {
            final Player player = mc.player;
            final ClientLevel world = mc.level;
            if (timer > 0)
                timer -= event.renderTickTime;
            BlockPos pos = player.getOnPos();
            int fertilityChunkSize = CommonConfigHandler.zoneSizeInChunks.get();
            ChunkAccess access = world.getChunk(pos);
            ChunkPos chunkPos = access.getPos();
            Biome b = world.getBiome(new BlockPos(pos.getX(), 128, pos.getZ())).value();
            if (chunkPos.x/ fertilityChunkSize != currentX ||
                    chunkPos.z/ fertilityChunkSize != currentZ || !b.equals(currentBiome)){
                currentX = chunkPos.x/ fertilityChunkSize;
                currentZ = chunkPos.z/ fertilityChunkSize;
                currentBiome = b;
                //sending our biome doesn't actually matter! Server still checks events!
                lastMessage++;
                lastMessage%=1000;
                PacketHandler.channel.send(PacketDistributor.SERVER.noArg(), new RequestPacket(pos, currentBiome.getRegistryName().toString(), lastMessage));
            }
        }
    }

    private Set<ItemStack> getHolding(){
        Set<ItemStack> result = new HashSet<>();
        if (mc.player != null) {
            result.add(mc.player.getItemInHand(InteractionHand.MAIN_HAND));
            result.add(mc.player.getItemInHand(InteractionHand.OFF_HAND));
        }
        return result;
    }

    @SubscribeEvent
    public void onRenderOverlayTick(RenderGameOverlayEvent.Post event){
        if (mc.player != null && mc.level != null && !mc.options.hideGui && !mc.options.renderDebug && ClientConfigHandler.showOverlay.get()){
            boolean shouldRender = true;
            if (ClientConfigHandler.autoHide.get()){
                if (timer <= 0){
                    Set<ItemStack> heldItems = getHolding();
                    boolean hasRightItem = false;
                    if (ClientConfigHandler.showOnBonemeal.get()){
                        for (ItemStack item : heldItems){
                            if (item.getItem() instanceof BoneMealItem){
                                hasRightItem = true;
                                break;
                            }
                        }
                    }
                    if (!hasRightItem && ClientConfigHandler.showOnHoe.get()){
                        for (ItemStack item : heldItems){
                            if (item.getItem() instanceof HoeItem){
                                hasRightItem = true;
                                break;
                            }
                        }
                    }
                    if (hasRightItem)
                        timer = ClientConfigHandler.autoHideDelay.get()*20;

                    shouldRender = hasRightItem;
                }
            }
            if (shouldRender){
                for (int i = 0; i < renderStackList.size(); i++){
                    itemRenderer.renderAndDecorateItem(renderStackList.get(i), ClientConfigHandler.overlayX.get() + i*16, ClientConfigHandler.overlayY.get());
                }
            }
        }
    }

    public static void renderParticlesOnBlock(LevelAccessor world, BlockPos pos, String effect, int numEffects){
        //problem: right now the world we are getting here is sometimes the ServerLevel (client can't use that)
        SimpleParticleType type = (SimpleParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(effect));
        world = Minecraft.getInstance().level; //this might cause issues in other dimensions and might render particles far from the player!
        //todo fix that^^
        for (int i = 0; i < numEffects; i++) {
            world.addParticle(type, pos.getX()+0.5 + world.getRandom().nextDouble() - 0.5, pos.getY(), pos.getZ()+0.5 + world.getRandom().nextDouble() - 0.5, 0.0D, 0.2, 0.0D);
        }
    }

}
