package com.wildestupdate.datagen;

import com.wildestupdate.BlockAdditions;
import com.wildestupdate.WildestUpdate;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStatesGen extends BlockStateProvider {

    public BlockStatesGen(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, WildestUpdate.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(BlockAdditions.MUD_BLOCK.get());
    }
}
