package com.snow.morejobs.tileentity;

import com.snow.morejobs.MoreJobsMod;
import com.snow.morejobs.container.BankChestContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BankChestTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private double interestRate = 0.0;
    private int tickCounter = 0;
    private static final int INTEREST_INTERVAL = 36000; // 30 minutes
    private static final String CHELOU_ITEM_ID = "cheloucoin:chelou";

    private double pendingAmount = 0.0; // Pour garder les intérêts décimaux en attente

    public BankChestTileEntity() {
        super(MoreJobsMod.BANK_CHEST_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            tickCounter++;
            if (tickCounter >= INTEREST_INTERVAL) {
                applyInterest();
                tickCounter = 0;
                setChanged();
            }
        }
    }

    private void applyInterest() {
        if (interestRate <= 0) return;

        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return;

        String itemId = stack.getItem().getRegistryName().toString();
        if (!CHELOU_ITEM_ID.equals(itemId)) return;

        int currentCount = stack.getCount();
        double exactTotal = currentCount + pendingAmount;

        double interest = exactTotal * (interestRate / 100.0);
        pendingAmount += interest;

        int wholeToAdd = (int) pendingAmount;
        if (wholeToAdd > 0) {
            int newCount = Math.min(currentCount + wholeToAdd, 512);
            int actuallyAdded = newCount - currentCount;

            pendingAmount -= actuallyAdded;

            stack.setCount(newCount);
            setChanged();

            if (level != null) {
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 3);
            }
        }
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                String itemId = stack.getItem().getRegistryName().toString();
                return CHELOU_ITEM_ID.equals(itemId);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 512;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        interestRate = nbt.getDouble("interestRate");
        tickCounter = nbt.getInt("tickCounter");
        pendingAmount = nbt.getDouble("pendingAmount");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putDouble("interestRate", interestRate);
        nbt.putInt("tickCounter", tickCounter);
        nbt.putDouble("pendingAmount", pendingAmount);
        return nbt;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    public void dropContents(World world, BlockPos pos) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                net.minecraft.inventory.InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double rate) {
        this.interestRate = Math.max(0, Math.min(rate, 100));
        setChanged();
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Bank Chest");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BankChestContainer(windowId, playerInventory, this);
    }
}
