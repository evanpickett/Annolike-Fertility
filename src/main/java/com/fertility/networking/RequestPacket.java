package com.fertility.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class RequestPacket {
    //WARNING: we need to make sure we're requesting chunks that exist! or else this may be exploitable to load
    // unwanted chunks!

    public final BlockPos position;
    public final int length;
    public final CharSequence biome;
    public final int lastMessage;
    public RequestPacket(BlockPos position, CharSequence biome, int lastMessage) {
        this.position = position;
        this.biome = biome;
        this.lastMessage = lastMessage;
        length = biome.length();
    }
    public static void encode(RequestPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.position);
        buf.writeInt(msg.length);
        buf.writeCharSequence(msg.biome, StandardCharsets.UTF_8);
        buf.writeInt(msg.lastMessage);
    }
    public static RequestPacket decode(FriendlyByteBuf buf) {
        BlockPos position = buf.readBlockPos();
        int length = buf.readInt();
        CharSequence sequence = buf.readCharSequence(length, StandardCharsets.UTF_8);
        return new RequestPacket(position, sequence, buf.readInt());
    }
}
