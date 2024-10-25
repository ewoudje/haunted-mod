package com.ewoudje.ism.blockentities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class SyncedBlockEntity(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    BlockEntity(type, pos, blockState)
{
    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        writeData(tag)
        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        readData(tag)
        super.loadAdditional(tag, registries)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(registries)
        writeData(tag)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag, lookupProvider: HolderLookup.Provider) {
        readData(tag)
        super.handleUpdateTag(tag, lookupProvider)
    }

    abstract fun readData(tag: CompoundTag)
    abstract fun writeData(tag: CompoundTag)
}