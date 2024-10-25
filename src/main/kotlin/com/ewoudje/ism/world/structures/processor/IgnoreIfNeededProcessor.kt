package com.ewoudje.ism.world.structures.processor

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate

class IgnoreIfNeededProcessor(val blocks: List<Block>) : StructureProcessor() {

    override fun getType(): StructureProcessorType<*> = IsmStructureProcessors.IGNORE_IF_NEEDED

    override fun process(
        serverLevel: LevelReader,
        offset: BlockPos,
        pos: BlockPos,
        blockInfo: StructureTemplate.StructureBlockInfo,
        relativeBlockInfo: StructureTemplate.StructureBlockInfo,
        settings: StructurePlaceSettings,
        template: StructureTemplate?
    ): StructureTemplate.StructureBlockInfo? =
        if (this.blocks.contains(relativeBlockInfo.state().block) &&
                serverLevel.getBlockState(relativeBlockInfo.pos).let { !it.isEmpty && !it.`is`(BlockTags.LEAVES) && !it.`is`(Blocks.BEDROCK)})
            null
        else
            relativeBlockInfo

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec {
            it.group(
                BlockState.CODEC
                    .xmap(
                        BlockState::getBlock,
                        Block::defaultBlockState
                    )
                    .listOf()
                    .fieldOf("blocks")
                    .forGetter(IgnoreIfNeededProcessor::blocks)
            ).apply(it, ::IgnoreIfNeededProcessor)
        }
    }
}