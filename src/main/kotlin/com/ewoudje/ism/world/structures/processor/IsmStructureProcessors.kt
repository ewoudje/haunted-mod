package com.ewoudje.ism.world.structures.processor

import com.ewoudje.ism.IsmMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object IsmStructureProcessors {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR, IsmMod.ID)

    val IGNORE_IF_NEEDED by REGISTRY.register("ignore_if_need")
        { -> StructureProcessorType { IgnoreIfNeededProcessor.CODEC } }
    val REPLACING by REGISTRY.register("replacing")
        { -> StructureProcessorType { ReplacingProcessor.CODEC } }
}