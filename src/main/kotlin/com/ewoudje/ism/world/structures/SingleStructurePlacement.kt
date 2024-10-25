package com.ewoudje.ism.world.structures

import com.ewoudje.ism.world.ExtraChunkGeneratorStructureState
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Vec3i
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType
import java.util.*

class SingleStructurePlacement(
    locateOffset: Vec3i,
    frequencyReductionMethod: FrequencyReductionMethod,
    frequency: Float,
    salt: Int,
    exclusionZone: Optional<ExclusionZone>,
    val minDistance: Int,
    val maxDistance: Int,
    private val uuid: UUID
) : StructurePlacement(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone) {
    private val distanceDiff = maxDistance - minDistance


    fun choosePositions(structureState: ChunkGeneratorStructureState): Set<ChunkPos> =
        (0..50).map {
            val seed = structureState.levelSeed + it * 5000 + it * it * 1000 - it * it * it
            val dist = (seed % distanceDiff) + minDistance
            val degrees = (seed / distanceDiff) % 360
            val angle = degrees / 180.0 * Math.PI

            val x = Math.cos(angle) * dist
            val z = Math.sin(angle) * dist

            ChunkPos(x.toInt(), z.toInt())
        }.toSet()

    override fun isPlacementChunk(structureState: ChunkGeneratorStructureState, x: Int, z: Int): Boolean =
        (structureState as ExtraChunkGeneratorStructureState).getPossiblePositions(this).contains(ChunkPos(x, z))

    override fun type(): StructurePlacementType<*> = IsmStructures.SINGLE_PLACEMENT

    override fun equals(other: Any?): Boolean {
        if (other !is SingleStructurePlacement) return false
        return other.uuid == uuid
    }

    override fun hashCode(): Int = uuid.hashCode()

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec {
            placementCodec(it).and(
                it.group(
                    Codec.INT.fieldOf("minDistance").forGetter(SingleStructurePlacement::minDistance),
                    Codec.INT.fieldOf("maxDistance").forGetter(SingleStructurePlacement::maxDistance),
                    Codec.LONG.listOf(2, 2)
                        .xmap({UUID(it.get(0), it.get(1))}, { listOf(it.mostSignificantBits, it.leastSignificantBits) })
                        .fieldOf("uuid")
                        .forGetter(SingleStructurePlacement::uuid)
                )
            ).apply(it, ::SingleStructurePlacement)
        }
    }
}