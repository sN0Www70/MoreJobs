package com.snow.morejobs.network.packets;

import com.snow.morejobs.tileentity.BankChestTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class InterestRatePacket {

    private final BlockPos pos;
    private final double interestRate;

    public InterestRatePacket(BlockPos pos, double interestRate) {
        this.pos = pos;
        this.interestRate = interestRate;
    }

    public InterestRatePacket(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.interestRate = buffer.readDouble();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeDouble(interestRate);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                TileEntity tileEntity = context.getSender().level.getBlockEntity(pos);
                if (tileEntity instanceof BankChestTileEntity) {
                    BankChestTileEntity bankTile = (BankChestTileEntity) tileEntity;
                    bankTile.setInterestRate(interestRate);

                    System.out.println("[InterestRatePacket] Setting interest rate to: " + interestRate);

                    // Force la sauvegarde ET la synchronisation client
                    bankTile.setChanged();
                    context.getSender().level.sendBlockUpdated(pos,
                            bankTile.getBlockState(),
                            bankTile.getBlockState(),
                            3);
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}