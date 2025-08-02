package com.snow.morejobs.config;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Config {

    // Monnaie utilisée pour tous les jobs (modifiable facilement ici)
    public static final Item MONEY_ITEM = ForgeRegistries.ITEMS.getValue(new ResourceLocation("cheloucoin", "chelou"));

}
