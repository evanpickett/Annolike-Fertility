package com.wildestupdate.datagen;

import com.wildestupdate.BlockAdditions;
import com.wildestupdate.WildestUpdate;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class LanguageProviderGen extends LanguageProvider {

    public LanguageProviderGen(DataGenerator gen, String locale) {
        super(gen, WildestUpdate.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        //add("itemGroup." + TAB_NAME, "My Tab");
        add(BlockAdditions.MUD_BLOCK.get(), "Mud");
    }
}
