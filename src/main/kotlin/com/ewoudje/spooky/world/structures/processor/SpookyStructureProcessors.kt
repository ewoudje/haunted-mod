package com.ewoudje.spooky.world.structures.processor

import com.ewoudje.spooky.SpookyMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyStructureProcessors {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR, SpookyMod.ID)

    val IGNORE_IF_NEEDED by REGISTRY.register("ignore_if_need")
        { -> StructureProcessorType { IgnoreIfNeededProcessor.CODEC } }
    val REPLACING by REGISTRY.register("replacing")
        { -> StructureProcessorType { ReplacingProcessor.CODEC } }
}