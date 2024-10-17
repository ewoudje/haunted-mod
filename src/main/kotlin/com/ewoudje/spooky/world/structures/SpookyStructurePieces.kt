package com.ewoudje.spooky.world.structures

import com.ewoudje.spooky.SpookyMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyStructurePieces {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PIECE, SpookyMod.ID)

    val EVIL_GOD_SEAL by REGISTRY.register("evil_god_seal") {->
        StructurePieceType { ctx, tag -> EvilGodSealStructure.Piece(ctx.structureTemplateManager, tag) }
    }
}