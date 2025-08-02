package com.snow.morejobs.gui.bartender;

import com.snow.morejobs.data.BartenderShopData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

public class BartenderShopContainer extends Container {

    public static ContainerType<BartenderShopContainer> TYPE;
    private final PlayerEntity player;

    public BartenderShopContainer(int id, PlayerEntity player) {
        super(TYPE, id);
        this.player = player;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    public void handleClick(int slotId, boolean rightClick) {
        if (!(player instanceof ServerPlayerEntity)) return;

        Map<Integer, BartenderShopData.ShopItem> items = BartenderShopData.SHOP_ITEMS;
        if (!items.containsKey(slotId)) return;

        BartenderShopData.ShopItem item = items.get(slotId);
        int amount = rightClick ? 16 : 1;
        int cost = (int) Math.ceil(item.price * amount);

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        Item currency = BartenderShopData.CURRENCY;

        if (countItem(serverPlayer, currency) < cost) {
            serverPlayer.sendMessage(new StringTextComponent("❌ Pas assez de monnaie !"), serverPlayer.getUUID());
            return;
        }

        ItemStack stack = item.stack.copy();
        stack.setCount(amount);

        boolean success = serverPlayer.inventory.add(stack);
        if (success) {
            removeItems(serverPlayer, currency, cost);
            serverPlayer.sendMessage(new StringTextComponent("✅ Achat de " + amount + " " + item.stack.getDisplayName().getString()), serverPlayer.getUUID());
        } else {
            serverPlayer.sendMessage(new StringTextComponent("❌ Pas de place dans l'inventaire !"), serverPlayer.getUUID());
        }
    }

    private int countItem(PlayerEntity player, Item item) {
        int count = 0;
        for (ItemStack stack : player.inventory.items) {
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void removeItems(PlayerEntity player, Item item, int amount) {
        for (int i = 0; i < player.inventory.items.size(); i++) {
            ItemStack stack = player.inventory.items.get(i);
            if (stack.getItem() == item) {
                int remove = Math.min(stack.getCount(), amount);
                stack.shrink(remove);
                amount -= remove;
                if (amount <= 0) break;
            }
        }
    }
}
