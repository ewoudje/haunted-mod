package com.ewoudje.ism.world.structures

import com.ewoudje.ism.IsmMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object IsmStructurePieces {
    val REGISTRY = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PIECE, IsmMod.ID)

    val EVIL_GOD_SEAL by REGISTRY.register("evil_god_seal") {->
        StructurePieceType { ctx, tag -> EvilGodSealStructure.Piece(ctx.structureTemplateManager, tag) }
    }
}