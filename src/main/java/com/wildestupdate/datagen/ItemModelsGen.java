package com.wildestupdate.datagen;

import com.wildestupdate.ItemAdditions;
import com.wildestupdate.WildestUpdate;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelsGen extends ItemModelProvider {

    public ItemModelsGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, WildestUpdate.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ItemAdditions.MUD_ITEM.get().getRegistryName().getPath(), modLoc("block/mud"));
    }
}
