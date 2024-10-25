package com.ewoudje.ism.blocks

import com.ewoudje.ism.items.IsmItems
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class SadCropBlock(properties: Properties) : CropBlock(properties) {

    override fun codec(): MapCodec<SadCropBlock> = CODEC

    override fun getBaseSeedId(): ItemLike = IsmItems.SAD_SEED

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext?
    ): VoxelShape {
        return SHAPE_BY_AGE[getAge(state)]
    }

    companion object {
        val SHAPE_BY_AGE: Array<VoxelShape> = arrayOf(
            box(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
            box(7.0, 0.0, 7.0, 9.0, 4.0, 9.0),
            box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0),
            box(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),
            box(7.0, 0.0, 7.0, 9.0, 10.0, 9.0),
            box(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
            box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0),
            box(7.0, 0.0, 7.0, 9.0, 15.0, 9.0)
        )

        val CODEC = simpleCodec(::SadCropBlock)
    }

}