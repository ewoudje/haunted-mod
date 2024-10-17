package com.ewoudje.spooky

import com.ewoudje.spooky.blocks.SpookyBlocks
import com.ewoudje.spooky.blockentities.SpookyBlockEntities
import com.ewoudje.spooky.capabilities.ShizoCapability
import com.ewoudje.spooky.capabilities.SpookyCapabilities
import com.ewoudje.spooky.client.ClientSpookyMod
import com.ewoudje.spooky.items.SpookyItems
import com.ewoudje.spooky.world.SpookyWorldHandler
import com.ewoudje.spooky.world.SpookyWorldState
import com.ewoudje.spooky.world.structures.SpookyStructurePieces
import com.ewoudje.spooky.world.structures.SpookyStructures
import com.ewoudje.spooky.world.structures.processor.SpookyStructureProcessors
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(SpookyMod.ID)
object SpookyMod {
    const val ID = "haunted"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        SpookyBlocks.REGISTRY.register(MOD_BUS)
        SpookyPOIs.REGISTRY.register(MOD_BUS)
        SpookyBlockEntities.REGISTRY.register(MOD_BUS)
        SpookyAttributes.REGISTRY.register(MOD_BUS)
        SpookyItems.REGISTRY.register(MOD_BUS)
        SpookyStructures.PLACEMENT_REGISTRY.register(MOD_BUS)
        SpookyStructures.TYPE_REGISTRY.register(MOD_BUS)
        SpookyStructurePieces.REGISTRY.register(MOD_BUS)
        SpookyStructureProcessors.REGISTRY.register(MOD_BUS)

        MOD_BUS.addListener(SpookyCapabilities::registerCapabilities)
        MOD_BUS.addListener(SpookyAttributes::registerAttributes)

        FORGE_BUS.addListener(ShizoCapability::tickPlayer)

        SpookyWorldHandler.register()

        LOGGER.log(Level.INFO, "Spooky mod active!")
    }
}

val String.resource get() = ResourceLocation.fromNamespaceAndPath(SpookyMod.ID, this)
val String.component get() = Component.translatable(this)
