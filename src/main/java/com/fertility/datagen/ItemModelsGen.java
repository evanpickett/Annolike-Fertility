package com.fertility.datagen;

import com.fertility.Fertility;
import com.fertility.ItemAdditions;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelsGen extends ItemModelProvider {

    public ItemModelsGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Fertility.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ItemAdditions.MUD_ITEM.get().getRegistryName().getPath(), modLoc("block/mud"));
    }
}
