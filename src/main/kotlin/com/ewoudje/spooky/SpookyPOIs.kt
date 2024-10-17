package com.ewoudje.spooky

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyPOIs {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, SpookyMod.ID)

    val chest by REGISTRY.register("chest")
        { -> PoiType(Blocks.CHEST.stateDefinition.possibleStates.toSet(), 0, 1)}
}