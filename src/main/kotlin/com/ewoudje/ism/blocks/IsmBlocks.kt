package com.ewoudje.ism.blocks

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import com.ewoudje.ism.IsmMod
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object IsmBlocks {
    val REGISTRY = DeferredRegister.createBlocks(IsmMod.ID)

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

    val CURSED_GRASS by REGISTRY.register("cursed_grass_block") { ->
        CursedGrassBlock(
            Properties.of()
                .mapColor(MapColor.GRASS)
                .randomTicks()
                .strength(0.6F)
                .sound(SoundType.GRASS)
        )
    }

    val SAD_CROP by REGISTRY.register("sad_crop") { ->
        SadCropBlock(
            Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
        )
    }
}
