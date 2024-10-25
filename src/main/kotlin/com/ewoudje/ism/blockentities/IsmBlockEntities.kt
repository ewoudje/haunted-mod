package com.ewoudje.ism.blockentities

import com.ewoudje.ism.IsmMod
import com.ewoudje.ism.blocks.IsmBlocks
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object IsmBlockEntities {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, IsmMod.ID)

    val SEAL by REGISTRY.register("seal") { -> BlockEntityType.Builder.of(
        ::SealBlockEntity,
        IsmBlocks.CENTER_SEAL,
    ).build(null)}

    val SEAL_HACKER by REGISTRY.register("seal_hacker") { -> BlockEntityType.Builder.of(
        ::SealHackerBlockEntity,
        IsmBlocks.SEAL_HACKER,
    ).build(null)}
}