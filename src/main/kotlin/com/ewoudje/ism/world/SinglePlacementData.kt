package com.ewoudje.ism.world

import com.ewoudje.ism.world.structures.SingleStructurePlacement
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.level.ChunkPos

class SinglePlacementData(
    private val locations: MutableMap<SingleStructurePlacement, SinglePlacementLocation> = mutableMapOf(),
) {
    private lateinit var setDirty: () -> Unit

    fun getLocation(placement: SingleStructurePlacement): SinglePlacementLocation? = locations[placement]

    fun getPlacementGuess(placement: SingleStructurePlacement): ChunkPos? =
        locations[placement]?.let { it.pos ?: it.positions?.first() }

    fun foundPlacement(placement: SingleStructurePlacement, chunkPos: ChunkPos) {
        val p = locations[placement] ?: throw NoSuchElementException()
        if (p.pos != null) throw IllegalStateException()

        locations[placement] = SinglePlacementLocation(chunkPos)
        setDirty()
    }

    fun removePlacement(placement: SingleStructurePlacement, chunkPos: ChunkPos) {
        val p = locations[placement] ?: throw NoSuchElementException()
        if (p.pos != null) throw IllegalStateException()

        p.positions!!.remove(chunkPos)
        setDirty()
    }

    fun initialLocations(placement: SingleStructurePlacement, locations: MutableSet<ChunkPos>) {
        if (placement in this.locations) throw IllegalArgumentException("Placement is already in use")

        this.locations[placement] = SinglePlacementLocation(locations)
        setDirty()
    }

    fun getUndecidedLocation(): ChunkPos? = locations
        .filter { it.value.positions != null }
        .firstNotNullOfOrNull { it.value.positions!!.first() }

    constructor(tag: CompoundTag) : this(tag.getList("SinglePlacementLocations", 10).associate { tag ->
        tag as CompoundTag

        val location = SinglePlacementLocation(tag)
        val placement = SingleStructurePlacement.CODEC.decoder().decode(NbtOps.INSTANCE, tag).orThrow.first

        placement to location
    }.toMutableMap())

    fun save(tag: CompoundTag) {
        val locationsTag = ListTag()
        locationsTag.addAll(locations.map { (p, l) ->
            SingleStructurePlacement.CODEC.encoder().encodeStart(NbtOps.INSTANCE, p).orThrow.apply {
                this as CompoundTag

                if (l.pos != null)
                    putLong("location", l.pos.toLong())
                else
                    putLongArray("locations", l.positions!!.map(ChunkPos::toLong))
            }
        })

        tag.put("SinglePlacementLocations", locationsTag)
    }

    fun setSetDirty(setDirty: () -> Unit) {
        this.setDirty = setDirty
    }

    class SinglePlacementLocation private constructor(val pos: ChunkPos?, val positions: MutableSet<ChunkPos>?) {
        constructor(pos: ChunkPos) : this(pos, null)
        constructor(positions: Iterable<ChunkPos>) : this(null, positions.toMutableSet())
        constructor(tag: CompoundTag) : this(
            tag.get("location")?.let { ChunkPos((it as LongTag).asLong) },
            tag.get("locations")?.let { (it as ListTag).map { l -> ChunkPos((l as LongTag).asLong) } }?.toMutableSet()
        )
    }
}