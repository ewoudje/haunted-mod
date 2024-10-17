package com.ewoudje.spooky.world

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData

class SpookyWorldState(
    isEvilGodFree: Boolean = false,
    val singlePlacementData: SinglePlacementData = SinglePlacementData()
) : SavedData() {
    init {
        singlePlacementData.setSetDirty(::setDirty)
    }

    var isEvilGodFree = isEvilGodFree
        set(value) { field = value; setDirty() }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        tag.putBoolean("isEvilGodFree", isEvilGodFree)
        singlePlacementData.save(tag)
        return tag
    }

    companion object {
        fun create(): SpookyWorldState = SpookyWorldState()
        fun load(tag: CompoundTag, lookupProvider: HolderLookup.Provider): SpookyWorldState = SpookyWorldState(
            tag.getBoolean("isEvilGodFree"),
            SinglePlacementData(tag)
        )

        val ServerLevel.spookyWorldState get() =
            this.server.overworld().dataStorage.computeIfAbsent(Factory(::create, ::load), "haunted_data")
    }
}