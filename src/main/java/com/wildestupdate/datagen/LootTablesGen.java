package com.wildestupdate.datagen;

import com.wildestupdate.BlockAdditions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class LootTablesGen extends BaseLootTableProvider {

    public LootTablesGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void addTables(){
        LootPool.Builder builder = LootPool.lootPool()
                .name("mud")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(BlockAdditions.MUD_BLOCK.get()));
        lootTables.put(BlockAdditions.MUD_BLOCK.get(), LootTable.lootTable().withPool(builder));
    }

}
