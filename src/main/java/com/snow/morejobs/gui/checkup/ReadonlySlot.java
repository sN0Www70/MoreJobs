package com.snow.morejobs.gui.checkup;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ReadonlySlot extends Slot {
    public ReadonlySlot(IInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
    }
    @Override public boolean mayPickup(PlayerEntity p) { return false; }
    @Override public boolean mayPlace(ItemStack stack) { return false; }
}
