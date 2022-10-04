package com.fertility.client;

import com.fertility.Fertility;
import com.fertility.networking.PacketHandler;
import com.fertility.networking.RequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    private static final Minecraft mc = Minecraft.getInstance();
    public static final Font font = mc.font;
    public static final ItemRenderer itemRenderer = mc.getItemRenderer();
    private static int currentX = 0, currentZ = 0;
    private static Biome currentBiome = null;
    public static int lastMessage = 0;
    private static boolean needsCheck = false;
    private static float timer = 0;

    public static ArrayList<ItemStack> renderStackList = new ArrayList<ItemStack>();

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END && mc.player != null && mc.level != null && !mc.options.hideGui && !mc.options.renderDebug /*&& (mc.screen == null || (ConfigHandler.CLIENT.displayWithChatOpen.get() && mc.screen instanceof ChatScreen))*/) {
            final Player player = mc.player;
            final ClientLevel world = mc.level;
            BlockPos pos = player.getOnPos();
            Biome b = world.getBiome(new BlockPos(pos.getX(), 128, pos.getZ())).value();
            /*timer+=event.renderTickTime;
            if (timer > 5){
                timer = 0;
                needsCheck = true;
            }*/
            if (pos.getX()/ Fertility.FERTILITY_CHUNK_SIZE != currentX ||
                    pos.getZ()/ Fertility.FERTILITY_CHUNK_SIZE != currentZ ||
                    needsCheck || !b.equals(currentBiome)){
                needsCheck = false;
                timer = 0;
                currentX = pos.getX()/ Fertility.FERTILITY_CHUNK_SIZE;
                currentZ = pos.getZ()/ Fertility.FERTILITY_CHUNK_SIZE;
                currentBiome = b;
                //Set<String> crops = Utility.getAllowedCropsInLocation(mc.level, pos);
                //renderStackList.clear();
                //for (String c : crops)
                //    renderStackList.add(new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(c))));
                Fertility.LOGGER.info("X: " + currentX + " Z: " + currentZ + " Current biome: " + currentBiome.getRegistryName().toString());
                //sending our biome doesn't actually matter! Server still checks events!
                lastMessage++;
                lastMessage%=1000;
                PacketHandler.channel.send(PacketDistributor.SERVER.noArg(), new RequestPacket(pos, currentBiome.getRegistryName().toString(), lastMessage));
            }
            //PoseStack p = new PoseStack();
            //ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft","seeds")));

            //itemRenderer.renderAndDecorateItem(stack, 16, 16);
            //font.drawShadow(p, validCrops, 2, 2, 0xAAAAAA);
            //RenderSystem.disableDepthTest();
        }
    }
    @SubscribeEvent
    public void onRenderOverlayTick(RenderGameOverlayEvent.Post event){
        if (mc.player != null && mc.level != null && !mc.options.hideGui && !mc.options.renderDebug){
            for (int i = 0; i < renderStackList.size(); i++){
                itemRenderer.renderAndDecorateItem(renderStackList.get(i), 16 + i*16, 0);
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
