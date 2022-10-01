package com.wildestupdate.datagen;

import com.wildestupdate.BlockAdditions;
import com.wildestupdate.WildestUpdate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class BlockTagsGen extends BlockTagsProvider {

    public BlockTagsGen(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, WildestUpdate.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(){
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(BlockAdditions.MUD_BLOCK.get());
    }
}
