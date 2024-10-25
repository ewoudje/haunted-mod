package com.ewoudje.ism.blocks

import com.ewoudje.ism.blockentities.SealHackerBlockEntity
import com.ewoudje.ism.items.IsmItems
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class SealHackerBlock(properties: Properties) : BaseEntityBlock(properties) {

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (!stack.`is`(IsmItems.REDSTONE_ENGRAVED_DIAMOND)) return ItemInteractionResult.FAIL
        val be = level.getBlockEntity(pos) as? SealHackerBlockEntity ?: return ItemInteractionResult.FAIL
        if (be.isSlotted()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        stack.shrink(1)
        be.slotIn()

        return ItemInteractionResult.CONSUME
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (state.`is`(newState.block)) return
        val be = level.getBlockEntity(pos) as? SealHackerBlockEntity ?: return
        if (be.isSlotted())
            Containers.dropItemStack(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), ItemStack(IsmItems.REDSTONE_ENGRAVED_DIAMOND))

        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        val be = level.getBlockEntity(pos) as? SealHackerBlockEntity ?: return
        if (!be.isSlotted()) return

        val d0 = pos.x.toDouble() + 0.5 + (random.nextDouble() - 0.5) * 0.2
        val d1 = pos.y.toDouble() + 0.55 + (random.nextDouble() - 0.5) * 0.2
        val d2 = pos.z.toDouble() + 0.5 + (random.nextDouble() - 0.5) * 0.2
        level.addParticle(DustParticleOptions.REDSTONE, d0, d1, d2, 0.0, 10.0, 0.0)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SealHackerBlockEntity(pos, state)
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = SHAPE
    override fun codec(): MapCodec<out BaseEntityBlock> = CODEC

    companion object {
        val CODEC = simpleCodec(::SealHackerBlock)
        val SHAPE = box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    }
}