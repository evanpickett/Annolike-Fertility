package com.fertility.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class BonemealPacket {
    //WARNING: we need to make sure we're requesting chunks that exist! or else this may be exploitable to load
    // unwanted chunks!

    public final BlockPos position;
    public final CharSequence checkFor;
    public final int length;
    public BonemealPacket(BlockPos position, int length, String checkFor) {
        this.position = position;
        this.checkFor = checkFor;
        this.length = length;
    }
    public static void encode(BonemealPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.length);
        buf.writeBlockPos(msg.position);
        buf.writeCharSequence(msg.checkFor, StandardCharsets.UTF_8);
    }
    public static BonemealPacket decode(FriendlyByteBuf buf) {
        int length = buf.readInt();
        BlockPos position = buf.readBlockPos();
        CharSequence charSequence = buf.readCharSequence(length, StandardCharsets.UTF_8);
        String string = charSequence.toString();
        return new BonemealPacket(position, length, string);
    }
}
