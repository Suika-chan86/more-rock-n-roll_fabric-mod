package com.suikachan86.morerocknroll.block.entity;

import com.suikachan86.morerocknroll.block.MusicDiscShelfLayout;
import com.suikachan86.morerocknroll.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MusicDiscShelfBlockEntity extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(
            MusicDiscShelfLayout.SLOT_COUNT,
            ItemStack.EMPTY
    );

    public MusicDiscShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUSIC_DISC_SHELF, pos, state);
    }

    public boolean insert(int slot, ItemStack source, boolean creative) {
        if (slot < 0 || slot >= size() || source.isEmpty() || !isValidDisc(source) || !getStack(slot).isEmpty()) {
            return false;
        }

        ItemStack stored = source.copy();
        stored.setCount(1);
        setStack(slot, stored);
        if (!creative) {
            source.decrement(1);
        }
        return true;
    }

    public ItemStack take(int slot) {
        if (slot < 0 || slot >= size()) {
            return ItemStack.EMPTY;
        }
        return removeStack(slot);
    }

    private boolean isValidDisc(ItemStack stack) {
        return ModItems.findTrack(stack.getItem()).isPresent();
    }

    private void markInventoryDirty() {
        markDirty();
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removed = items.get(slot).split(amount);
        if (!removed.isEmpty()) {
            markInventoryDirty();
        }
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removed = items.set(slot, ItemStack.EMPTY);
        if (!removed.isEmpty()) {
            markInventoryDirty();
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!stack.isEmpty() && !isValidDisc(stack)) {
            return;
        }

        ItemStack stored = stack.copy();
        stored.setCount(Math.min(stored.getCount(), 1));
        items.set(slot, stored);
        markInventoryDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world == null || world.getBlockEntity(pos) != this) {
            return false;
        }
        return player.squaredDistanceTo(Vec3d.ofCenter(pos)) <= 64.0;
    }

    @Override
    public void clear() {
        for (int slot = 0; slot < size(); slot++) {
            items.set(slot, ItemStack.EMPTY);
        }
        markInventoryDirty();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, items, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, items, registryLookup);
        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
