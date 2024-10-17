package com.ewoudje.spooky.blockentities

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState

class SealHackerBlockEntity(pos: BlockPos, state: BlockState) : SyncedBlockEntity(SpookyBlockEntities.SEAL_HACKER, pos, state) {
    private var isSlotted = false

    override fun readData(tag: CompoundTag) {
        isSlotted = tag.getBoolean("isSlotted")
    }

    override fun writeData(tag: CompoundTag) {
        tag.putBoolean("isSlotted", isSlotted)
    }

    fun isSlotted(): Boolean = isSlotted
    fun slotIn() {
        isSlotted = true
        this.setChanged()
    }

    fun shatter() {
        isSlotted = false
        //TODO play sounds and effects
        this.setChanged()
    }
}