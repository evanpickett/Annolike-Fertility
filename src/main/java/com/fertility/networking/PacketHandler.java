package com.fertility.networking;

import com.fertility.Fertility;
import com.fertility.CropEvents;
import com.fertility.client.ClientEventHandler;
import com.fertility.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel channel;

    public static void register(){
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Fertility.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        channel.registerMessage(0, FertilityPacket.class, FertilityPacket::encode, FertilityPacket::decode, Client::handleFertilityPacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        channel.registerMessage(1, RequestPacket.class, RequestPacket::encode, RequestPacket::decode, PacketHandler::handleRequestPacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        channel.registerMessage(2, BonemealPacket.class, BonemealPacket::encode, BonemealPacket::decode, PacketHandler::handleBonemealPacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        channel.registerMessage(3, EffectPacket.class, EffectPacket::encode, EffectPacket::decode, Client::handleEffectPacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    //Server-sided responses
    public static void handleRequestPacket(RequestPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().setPacketHandled(true);
        BlockPos position = msg.position;
        String biome = msg.biome.toString();
        Set<String> out = Utility.getAllowedCropsInLocation(ctx.get().getSender().getLevel(), position, biome);
        StringBuilder result = new StringBuilder();
        for (String e : out){
            result.append(e + "&");
        }
        Fertility.LOGGER.debug("Server Received {}", msg.position);
        String answer = new String(result);
        Fertility.LOGGER.debug("Server Sent {}", answer);
        channel.reply(new FertilityPacket(answer.length(), answer, false, position, msg.lastMessage), ctx.get());
    }

    public static void handleBonemealPacket(BonemealPacket msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().setPacketHandled(true);
        BlockPos position = msg.position;
        Set<String> out = Utility.getAllowedCropsInLocation(ctx.get().getSender().getLevel(), position);
        if (!out.contains(msg.checkFor.toString())){
            StringBuilder result = new StringBuilder();
            for (String e : out){
                result.append(e + "&");
            }
            String answer = new String(result);
            channel.reply(new FertilityPacket(answer.length(), answer, true, position, -1), ctx.get());
        }

            //channel.reply(new EffectPacket(position, ParticleTypes.ANGRY_VILLAGER.getRegistryName().toString().length(), ParticleTypes.ANGRY_VILLAGER.getRegistryName().toString(), 5), ctx.get());
    }

    private static class Client{
        // Client-sided responses
        public static void handleFertilityPacket(FertilityPacket msg, Supplier<NetworkEvent.Context> ctx){
            ctx.get().setPacketHandled(true);
            LocalPlayer player = Minecraft.getInstance().player;
            if(player==null) return;
            ArrayList<ItemStack> list = new ArrayList<>();
            Set<String> set = new HashSet<>();
            String [] split = msg.validItems.toString().split("&");
            for (String s : split){
                if (s.length() > 0){
                    list.add(new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s))));
                    set.add(s);
                }
            }
            if (msg.isBonemeal){
                CropEvents.addToCache(msg.position, set);
                ClientEventHandler.renderParticlesOnBlock(player.level, msg.position, ParticleTypes.ANGRY_VILLAGER.getRegistryName().toString(), 5);
            }else{
                if (msg.messageRespondedTo == ClientEventHandler.lastMessage) {
                    ClientEventHandler.renderStackList = list;
                }
            }
        }

        public static void handleEffectPacket(EffectPacket msg, Supplier<NetworkEvent.Context> ctx){
            ctx.get().setPacketHandled(true);
            LocalPlayer player = Minecraft.getInstance().player;
            LevelAccessor world = Minecraft.getInstance().level;
            if(player==null || world==null) return;
            BlockPos pos = msg.position;
            String effectName = msg.effect.toString();
            int numberEffects = msg.numEffects;
            ClientEventHandler.renderParticlesOnBlock(world, pos, effectName, numberEffects);
        }
    }

}


