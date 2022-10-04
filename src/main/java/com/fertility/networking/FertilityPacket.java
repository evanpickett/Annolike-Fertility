package com.fertility.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class FertilityPacket {
    public final CharSequence validItems;
    public final int length;
    public final boolean isBonemeal;
    public final BlockPos position;
    public final int messageRespondedTo;
    public FertilityPacket(int length, String validItems, boolean isBonemeal, BlockPos position, int messageRespondedTo) {
        this.validItems = validItems;
        this.length = length;
        this.isBonemeal = isBonemeal;
        this.position = position;
        this.messageRespondedTo = messageRespondedTo;
    }
    public static void encode(FertilityPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.length);
        buf.writeCharSequence(msg.validItems, StandardCharsets.UTF_8);
        buf.writeBoolean(msg.isBonemeal);
        buf.writeBlockPos(msg.position);
        buf.writeInt(msg.messageRespondedTo);
    }
    public static FertilityPacket decode(FriendlyByteBuf buf) {
        int length = buf.readInt();
        CharSequence charSequence = buf.readCharSequence(length, StandardCharsets.UTF_8);
        String string = charSequence.toString();
        return new FertilityPacket(string.length(), string, buf.readBoolean(), buf.readBlockPos(), buf.readInt());
    }

}
