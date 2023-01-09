package com.fertility;

import com.fertility.client.ClientEventHandler;
import com.fertility.networking.BonemealPacket;
import com.fertility.networking.EffectPacket;
import com.fertility.networking.PacketHandler;
import com.fertility.util.Utility;
import com.fertility.util.Vector2;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class CropEvents {

    private final static HashMap<Vector2, Set<String>> cached = new HashMap<>();
    private static int cacheCount = 0;

    private final static HashMap<Block, Block> sameTypeConverter = new HashMap<>();
    static {
        // we treat CHORUS_FLOWERs as CHORUS_PLANTs (to not clutter our types)
        sameTypeConverter.put(Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT);
        sameTypeConverter.put(Blocks.TWISTING_VINES_PLANT, Blocks.TWISTING_VINES);
        sameTypeConverter.put(Blocks.WEEPING_VINES_PLANT, Blocks.WEEPING_VINES);
        sameTypeConverter.put(Blocks.CAVE_VINES_PLANT, Blocks.CAVE_VINES);
        sameTypeConverter.put(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
        sameTypeConverter.put(Blocks.KELP_PLANT, Blocks.KELP);
    }

    public static void addToCache(BlockPos point, Set<String> crops){
        Vector2 v2 = new Vector2(point.getX(), point.getZ());
        if (cacheCount > 50) {
            cached.clear();
            cacheCount = 0;
        }
        cached.put(v2, crops);
        cacheCount++;
    }

    public static Optional<Set<String>> getCache(BlockPos point){
        Vector2 v2 = new Vector2(point.getX(), point.getZ());
        return Optional.ofNullable(cached.get(v2));
    }

    public static Block getRealBlock(Block toConvert){
        return sameTypeConverter.getOrDefault(toConvert, toConvert);
    }
    public static boolean hasCrop(Set<String> set, String checkFor){
        return set.contains(checkFor);
    }

    @SubscribeEvent
    public void onCropGrowEvent(BlockEvent.CropGrowEvent.Pre event) {
        if (event.getWorld().isClientSide())
            return;
        ServerLevel world = (ServerLevel) event.getWorld();
        BlockPos pos = event.getPos();
        Block test = world.getBlockState(pos).getBlock();
        if (world.isEmptyBlock(pos)) {
            Block underneath = world.getBlockState(pos.below()).getBlock();
            if (underneath == Blocks.CACTUS || underneath == Blocks.CHORUS_FLOWER ||
                    underneath == Blocks.WEEPING_VINES || underneath == Blocks.BAMBOO_SAPLING || underneath == Blocks.BAMBOO ||
                    underneath == Blocks.KELP) {
                pos = pos.below();
            } else {
                Block above = world.getBlockState(pos.above()).getBlock();
                if (above == Blocks.WEEPING_VINES || above == Blocks.CAVE_VINES){
                    pos = pos.above();
                } else {
                    Fertility.LOGGER.warn("Tried to grow above({}) OR below({}) from {}", above, underneath, test);
                    return;
                }
            }
        }
        Block b = getRealBlock(world.getBlockState(pos).getBlock());
        //Fertility.LOGGER.warn("Tried to grow crop {} (actually found {})", test.getRegistryName().toString(), b.getRegistryName().toString());
        Set<String> allowed = Utility.getAllowedCropsInLocation(world, pos);
        boolean canGrowCrop = allowed.contains(b.getRegistryName().toString());
        if (!canGrowCrop && Utility.crops.contains(b.getRegistryName().toString())) {
            event.setResult(Event.Result.DENY);
            world.destroyBlock(pos, true);
            PacketHandler.channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(),pos.getY(),pos.getZ(), 64, world.dimension())), new EffectPacket(pos, ParticleTypes.ANGRY_VILLAGER.getRegistryName().toString(), 5));
            //ClientEventHandler.renderParticlesOnBlock(world, pos, ParticleTypes.ANGRY_VILLAGER.getRegistryName().toString(), 5);
        }
    }

    /**
     * When a SaplingGrowTreeEvent is fired. Reject the event on the client
     * and validate on the server. Destroy the sapling if it's unavailable in this location.
     * If the sapling is destroyed, we should tell the client to render particles
     * @param event
     */
    @SubscribeEvent
    public void onSaplingGrowTreeEvent(SaplingGrowTreeEvent event) {
        if (event.getWorld().isClientSide())
            return;
        ServerLevel world = (ServerLevel) event.getWorld();
        BlockPos pos = event.getPos();
        Block b = getRealBlock(world.getBlockState(pos).getBlock());
        Set<String> allowed = Utility.getAllowedCropsInLocation(world, pos);
        //Fertility.LOGGER.warn("Tried to grow tree {}",b.getRegistryName().toString());
        boolean canGrowTree = allowed.contains(b.getRegistryName().toString());
        if (!canGrowTree && Utility.crops.contains(b.getRegistryName().toString())) {
            event.setResult(Event.Result.DENY);
            world.destroyBlock(pos, true);
            PacketHandler.channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(),pos.getY(),pos.getZ(), 64, world.dimension())), new EffectPacket(pos, ParticleTypes.ANGRY_VILLAGER.getRegistryName().toString(), 5));
            //ClientEventHandler.renderParticlesOnBlock(world, pos, ParticleTypes.ANGRY_VILLAGER.getRegistryName().toString(), 5);
        }
    }

    /**
     * When a bonemeal event is fired. On the client, we use a cache to limit the number of
     * requests to the server. On the server, we check if the crop is allowed to grow.
     * If the client is told the crop is not able to grow, we render particles to show that.
     * @param event
     */
    @SubscribeEvent
    public void onBonemeal(BonemealEvent event) {
        Level world = event.getWorld();
        BlockPos pos = event.getPos();
        Block b = getRealBlock(world.getBlockState(pos).getBlock());
        if (!Utility.crops.contains(b.getRegistryName().toString())){
            return;
        }
        Set<String> allowed = null;
        if (event.getPlayer() instanceof LocalPlayer){
            Optional<Set<String>> cacheResult = getCache(pos);
            if (cacheResult.isEmpty())
                PacketHandler.channel.send(PacketDistributor.SERVER.noArg(), new BonemealPacket(pos, b.getRegistryName().toString().length(), b.getRegistryName().toString()));
            else if (!hasCrop(cacheResult.get(), b.getRegistryName().toString()))
                ClientEventHandler.renderParticlesOnBlock(world, pos, ParticleTypes.ANGRY_VILLAGER.getRegistryName().toString(), 5);
        }else{
            allowed = Utility.getAllowedCropsInLocation((ServerLevel) world, pos);
        }
        if (allowed == null) {
            return;
        }
        boolean canBonemeal = allowed.contains(b.getRegistryName().toString());
        if (!canBonemeal) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
        }
    }
}
