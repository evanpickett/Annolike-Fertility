package com.fertility.datagen;

import com.fertility.BlockAdditions;
import com.fertility.Fertility;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStatesGen extends BlockStateProvider {

    public BlockStatesGen(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Fertility.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(BlockAdditions.MUD_BLOCK.get());
    }
}
