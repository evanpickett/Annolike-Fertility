package com.fertility.datagen;

import com.fertility.Fertility;
import com.fertility.BlockAdditions;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class LanguageProviderGen extends LanguageProvider {

    public LanguageProviderGen(DataGenerator gen, String locale) {
        super(gen, Fertility.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        //add("itemGroup." + TAB_NAME, "My Tab");
        add(BlockAdditions.MUD_BLOCK.get(), "Mud");
    }
}
