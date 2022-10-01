package com.wildestupdate.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class Mud extends Block {

    public static final Properties baseProperties = Properties.of(Material.DIRT).sound(SoundType.SLIME_BLOCK).speedFactor(0.6f).destroyTime(0.5f).color(MaterialColor.COLOR_BROWN);

    public Mud() {
        super(baseProperties);
    }
}
