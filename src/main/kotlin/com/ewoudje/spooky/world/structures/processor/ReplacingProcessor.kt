package com.ewoudje.spooky.world.structures.processor

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate

class ReplacingProcessor(val blocks: Map<Block, BlockState>) : StructureProcessor() {

    override fun getType(): StructureProcessorType<*> = SpookyStructureProcessors.REPLACING

    override fun process(
        serverLevel: LevelReader,
        offset: BlockPos,
        pos: BlockPos,
        blockInfo: StructureTemplate.StructureBlockInfo,
        relativeBlockInfo: StructureTemplate.StructureBlockInfo,
        settings: StructurePlaceSettings,
        template: StructureTemplate?
    ): StructureTemplate.StructureBlockInfo =
        this.blocks[relativeBlockInfo.state().block]?.let { StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos, it, null) } ?: relativeBlockInfo

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.unboundedMap(
                    BlockState.CODEC
                        .xmap(
                            BlockState::getBlock,
                            Block::defaultBlockState
                        ),
                    BlockState.CODEC,
                ).fieldOf("blocks").forGetter(ReplacingProcessor::blocks)
            ).apply(it, ::ReplacingProcessor)
        }
    }
}