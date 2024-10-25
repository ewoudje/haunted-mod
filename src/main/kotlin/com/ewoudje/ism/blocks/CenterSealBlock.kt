package com.ewoudje.ism.blocks

import com.ewoudje.ism.blockentities.SealBlockEntity
import com.ewoudje.ism.blockentities.IsmBlockEntities
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class CenterSealBlock(properties: Properties) : BaseEntityBlock(properties) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SealBlockEntity(pos, state)

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val self = level.getBlockEntity(pos)
        return if (self is SealBlockEntity) {
            if (self.open())
                InteractionResult.SUCCESS
            else
                InteractionResult.FAIL
        } else InteractionResult.FAIL
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? = createTickerHelper(
        type, IsmBlockEntities.SEAL,
        SealBlockEntity::tick
    )

    override fun codec(): MapCodec<out BaseEntityBlock> = CODEC

    companion object {
        val CODEC = simpleCodec(::CenterSealBlock)
    }
}