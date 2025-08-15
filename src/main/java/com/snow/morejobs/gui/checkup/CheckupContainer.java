package com.snow.morejobs.gui.checkup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

public class CheckupContainer extends Container {

    @ObjectHolder("morejobs:checkup")
    public static ContainerType<CheckupContainer> TYPE;

    private final PlayerEntity target;

    // Inventaire temporaire qui contient une copie des items du joueur inspecté
    private final Inventory targetInventoryCopy = new Inventory(36);

    public CheckupContainer(int windowId, PlayerInventory viewerInv, PacketBuffer buf) {
        this(windowId, viewerInv, viewerInv.player.level.getPlayerByUUID(buf.readUUID()));
    }

    public CheckupContainer(int windowId, PlayerInventory viewerInv, PlayerEntity target) {
        super(TYPE, windowId);
        this.target = target;

        // Copier les items du joueur inspecté dans l'inventaire temporaire
        if (target != null) {
            for (int i = 0; i < 36; i++) {
                targetInventoryCopy.setItem(i, target.inventory.getItem(i).copy());
            }
        }

        int x0 = 8, y0 = 18;

        // --- Inventaire du joueur inspecté (lecture seule, via copie) ---
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new ReadonlySlot(targetInventoryCopy, col + row * 9 + 9,
                        x0 + col * 18, y0 + row * 18));
            }
        }
        // Hotbar du joueur inspecté
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new ReadonlySlot(targetInventoryCopy, col,
                    x0 + col * 18, y0 + 58));
        }

        // --- Inventaire du policier (manipulable) ---
        int viewerY = y0 + 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(viewerInv, col + row * 9 + 9,
                        x0 + col * 18, viewerY + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(viewerInv, col,
                    x0 + col * 18, viewerY + 58));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        // Remplacé isRemoved() par isAlive() pour compatibilité
        return target != null && target.isAlive() && player.distanceTo(target) < 16.0f;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        // Pas de shift-click
        return ItemStack.EMPTY;
    }

    private static class ReadonlySlot extends Slot {
        public ReadonlySlot(Inventory inv, int index, int x, int y) {
            super(inv, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(PlayerEntity player) {
            return false;
        }
    }
}
