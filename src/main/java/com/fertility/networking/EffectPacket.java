package com.fertility.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class EffectPacket {

    public final BlockPos position;
    public final CharSequence effect;
    public final int effectLength;
    public final int numEffects;
    public EffectPacket(BlockPos position, String effect, int numEffects) {
        this.position = position;
        this.effect = effect;
        this.effectLength = effect.length();
        this.numEffects = numEffects;
    }
    public static void encode(EffectPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.effectLength).writeInt(msg.numEffects);
        buf.writeCharSequence(msg.effect, StandardCharsets.UTF_8);
        buf.writeBlockPos(msg.position);
    }
    public static EffectPacket decode(FriendlyByteBuf buf) {
        int length = buf.readInt();
        int numEffects = buf.readInt();
        CharSequence charSequence = buf.readCharSequence(length, StandardCharsets.UTF_8);
        String string = charSequence.toString();
        return new EffectPacket(buf.readBlockPos(), string, numEffects);
    }
}
