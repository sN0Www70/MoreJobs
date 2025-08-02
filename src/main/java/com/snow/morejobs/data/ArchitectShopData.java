package com.snow.morejobs.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ArchitectShopData {

    public static final Item CURRENCY = ForgeRegistries.ITEMS.getValue(new ResourceLocation("cheloucoin", "chelou"));

    public static final Map<Integer, ShopItem> SHOP_ITEMS = new HashMap<>();

    static {
        SHOP_ITEMS.put(0, new ShopItem(new ItemStack(Items.STONE), 1.0));
        SHOP_ITEMS.put(1, new ShopItem(new ItemStack(Items.BRICKS), 2.0));
        SHOP_ITEMS.put(2, new ShopItem(new ItemStack(Items.OAK_PLANKS), 1.0));
        SHOP_ITEMS.put(3, new ShopItem(new ItemStack(Items.SPRUCE_PLANKS), 1.0));
        SHOP_ITEMS.put(4, new ShopItem(new ItemStack(Items.BIRCH_PLANKS), 1.0));
        SHOP_ITEMS.put(5, new ShopItem(new ItemStack(Items.GLASS), 1.0));
        SHOP_ITEMS.put(6, new ShopItem(new ItemStack(Items.WHITE_CONCRETE), 1.2));
        SHOP_ITEMS.put(7, new ShopItem(new ItemStack(Items.GRAY_CONCRETE), 1.2));
        SHOP_ITEMS.put(8, new ShopItem(new ItemStack(Items.COBBLESTONE), 1.0));
        SHOP_ITEMS.put(9, new ShopItem(new ItemStack(Items.STONE_BRICKS), 1.5));
        SHOP_ITEMS.put(10, new ShopItem(new ItemStack(Items.QUARTZ_BLOCK), 3.0));
        SHOP_ITEMS.put(11, new ShopItem(new ItemStack(Items.SMOOTH_STONE), 1.0));
        SHOP_ITEMS.put(12, new ShopItem(new ItemStack(Items.SMOOTH_QUARTZ), 3.5));
        SHOP_ITEMS.put(13, new ShopItem(new ItemStack(Items.DARK_OAK_PLANKS), 1.0));
        SHOP_ITEMS.put(14, new ShopItem(new ItemStack(Items.STRIPPED_OAK_LOG), 1.5));
        SHOP_ITEMS.put(15, new ShopItem(new ItemStack(Items.STRIPPED_SPRUCE_LOG), 1.5));
        SHOP_ITEMS.put(16, new ShopItem(new ItemStack(Items.GLOWSTONE), 2.0));
        SHOP_ITEMS.put(17, new ShopItem(new ItemStack(Items.SEA_LANTERN), 2.5));
        SHOP_ITEMS.put(18, new ShopItem(new ItemStack(Items.LANTERN), 1.5));
        SHOP_ITEMS.put(19, new ShopItem(new ItemStack(Items.REDSTONE_LAMP), 1.5));
        SHOP_ITEMS.put(20, new ShopItem(new ItemStack(Items.BOOKSHELF), 2.0));
        SHOP_ITEMS.put(21, new ShopItem(new ItemStack(Items.WHITE_WOOL), 1.0));
        SHOP_ITEMS.put(22, new ShopItem(new ItemStack(Items.BROWN_WOOL), 1.0));
        SHOP_ITEMS.put(23, new ShopItem(new ItemStack(Items.CYAN_WOOL), 1.0));
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
