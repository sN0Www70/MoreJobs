package com.snow.morejobs.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class BartenderShopData {

    public static final Item CURRENCY = ForgeRegistries.ITEMS.getValue(new ResourceLocation("cheloucoin", "chelou"));

    public static final Map<Integer, ShopItem> SHOP_ITEMS = new HashMap<>();

    static {
        SHOP_ITEMS.put(0, new ShopItem(new ItemStack(Items.HONEY_BOTTLE), 1.0));
        SHOP_ITEMS.put(1, new ShopItem(new ItemStack(Items.MILK_BUCKET), 2.0));
        SHOP_ITEMS.put(2, new ShopItem(new ItemStack(Items.COOKED_BEEF), 1.0));
        SHOP_ITEMS.put(3, new ShopItem(new ItemStack(Items.COOKIE), 0.8));
        SHOP_ITEMS.put(4, new ShopItem(new ItemStack(Items.APPLE), 0.5));
        SHOP_ITEMS.put(5, new ShopItem(new ItemStack(Items.BREAD), 1.0));
        SHOP_ITEMS.put(6, new ShopItem(new ItemStack(Items.CAKE), 3.0));
        SHOP_ITEMS.put(7, new ShopItem(new ItemStack(Items.PUMPKIN_PIE), 1.5));
    }

    public static class ShopItem {
        public final ItemStack stack;
        public final double price;

        public ShopItem(ItemStack stack, double price) {
            this.stack = stack;
            this.price = price;
        }
    }
}
