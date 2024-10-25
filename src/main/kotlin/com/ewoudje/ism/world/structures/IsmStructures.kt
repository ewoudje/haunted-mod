package com.ewoudje.ism.world.structures

import com.ewoudje.ism.IsmMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object IsmStructures {
    val TYPE_REGISTRY = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, IsmMod.ID)
    val PLACEMENT_REGISTRY = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PLACEMENT, IsmMod.ID)

    val SINGLE_PLACEMENT by PLACEMENT_REGISTRY.register("single_placement") { -> StructurePlacementType { SingleStructurePlacement.CODEC }}

    val EVIL_GOD_SEAL_TYPE by TYPE_REGISTRY.register("evil_god_seal") {-> StructureType { EvilGodSealStructure.CODEC }}
}