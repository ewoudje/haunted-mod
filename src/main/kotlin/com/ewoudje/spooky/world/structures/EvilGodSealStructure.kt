package com.ewoudje.spooky.world.structures

import com.ewoudje.spooky.resource
import com.ewoudje.spooky.world.ExtraChunkGeneratorStructureState
import com.ewoudje.spooky.world.structures.processor.IgnoreIfNeededProcessor
import com.ewoudje.spooky.world.structures.processor.ReplacingProcessor
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager
import java.util.*

class EvilGodSealStructure(
    settings: StructureSettings
) : Structure(settings) {
    override fun findGenerationPoint(context: GenerationContext): Optional<GenerationStub> {
        val chunkpos = context.chunkPos()
        val lowestY = getLowestY(context, WIDTH, DEPTH)
        val height = context.chunkGenerator().getFirstOccupiedHeight(
            chunkpos.middleBlockX, chunkpos.middleBlockZ,
            Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor,
            context.randomState
        )

        return if (lowestY < context.chunkGenerator().seaLevel || (height - lowestY) > 6)
            Optional.empty<GenerationStub>()
        else
            onTopOfChunkCenter(
                context, Heightmap.Types.WORLD_SURFACE_WG
            ) { generatePieces(it, lowestY, context) }
    }

    private fun generatePieces(builder: StructurePiecesBuilder, height: Int, context: GenerationContext) {
        builder.addPiece(Piece(context.structureTemplateManager, context.chunkPos().getMiddleBlockPosition(height)))
    }

    override fun type(): StructureType<*> = SpookyStructures.EVIL_GOD_SEAL_TYPE

    class Piece: TemplateStructurePiece {
        constructor(structureTemplateManager: StructureTemplateManager, templatePosition: BlockPos):
                super(SpookyStructurePieces.EVIL_GOD_SEAL,
                    0,
                    structureTemplateManager,
                    STRUCTURE_LOCATION, STRUCTURE_LOCATION.toString(),
                    makeSettings(STRUCTURE_LOCATION),
                    templatePosition)

        constructor(structureTemplateManager: StructureTemplateManager, tag: CompoundTag):
                super(SpookyStructurePieces.EVIL_GOD_SEAL, tag, structureTemplateManager, ::makeSettings)

        override fun handleDataMarker(
            name: String,
            pos: BlockPos,
            level: ServerLevelAccessor,
            random: RandomSource,
            box: BoundingBox
        ) {

        }

        override fun postProcess(
            level: WorldGenLevel,
            structureManager: StructureManager,
            generator: ChunkGenerator,
            random: RandomSource,
            box: BoundingBox,
            chunkPos: ChunkPos,
            pos: BlockPos
        ) {
            super.postProcess(level, structureManager, generator, random, box, chunkPos, pos)
        }

        companion object {
            private fun makeSettings(location: ResourceLocation): StructurePlaceSettings {
                return StructurePlaceSettings()
                    .setMirror(Mirror.NONE)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR)
                    .addProcessor(ReplacingProcessor(mapOf(Blocks.BLACK_WOOL to Blocks.AIR.defaultBlockState())))
                    .setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING)
            }
        }
    }

    companion object {
        val STRUCTURE_LOCATION = "seal".resource
        val CODEC = simpleCodec(::EvilGodSealStructure)
        val WIDTH = 19
        val HEIGHT = 16
        val DEPTH = 32
    }
}