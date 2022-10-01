package com.wildestupdate.datagen;

import com.wildestupdate.ItemAdditions;
import com.wildestupdate.WildestUpdate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemsGen extends ItemModelProvider {

    public ItemsGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, WildestUpdate.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //singleTexture(ItemAdditions.MUD_ITEM.get().getRegistryName().getPath(), new ResourceLocation("item/handheld"),
        //        "layer0", new ResourceLocation(WildestUpdate.MODID, "item/magicblock_item"));
        withExistingParent(ItemAdditions.MUD_ITEM.get().getRegistryName().getPath(), new ResourceLocation(WildestUpdate.MODID, "block/mud"));
    }

}
