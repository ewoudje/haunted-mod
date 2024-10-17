package com.ewoudje.spooky.blocks

import com.ewoudje.spooky.SpookyMod
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.minecraft.world.level.material.MapColor
import net.neoforged.neoforge.registries.DeferredRegister

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object SpookyBlocks {
    val REGISTRY = DeferredRegister.createBlocks(SpookyMod.ID)

    val SEAL by REGISTRY.register("seal") { ->
        Block(
            Properties.of()
                .mapColor(MapColor.STONE)
                .strength(-1.0F, 3600000.0F)
                .noLootTable()
                .isValidSpawn(Blocks::never)
        )
    }

    val CENTER_SEAL by REGISTRY.register("seal_center") { ->
        CenterSealBlock(
            Properties.of()
                .mapColor(MapColor.STONE)
                .strength(-1.0F, 3600000.0F)
                .noLootTable()
                .isValidSpawn(Blocks::never)
        )
    }

    val SEAL_HACKER by REGISTRY.register("seal_hacker") { ->
        SealHackerBlock(
            Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL)
        )
    }
}
