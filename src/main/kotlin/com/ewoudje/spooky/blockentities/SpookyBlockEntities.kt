package com.ewoudje.spooky.blockentities

import com.ewoudje.spooky.SpookyMod
import com.ewoudje.spooky.blocks.SpookyBlocks
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyBlockEntities {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SpookyMod.ID)

    val SEAL by REGISTRY.register("seal") { -> BlockEntityType.Builder.of(
        ::SealBlockEntity,
        SpookyBlocks.CENTER_SEAL,
    ).build(null)}

    val SEAL_HACKER by REGISTRY.register("seal_hacker") { -> BlockEntityType.Builder.of(
        ::SealHackerBlockEntity,
        SpookyBlocks.SEAL_HACKER,
    ).build(null)}
}