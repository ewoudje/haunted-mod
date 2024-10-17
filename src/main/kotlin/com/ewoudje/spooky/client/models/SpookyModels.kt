package com.ewoudje.spooky.client.models

import com.ewoudje.spooky.resource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.resources.model.ModelResourceLocation
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.ModelEvent
import java.util.function.Supplier

object SpookyModels {
    private val registerModels = mutableListOf<ModelResourceLocation>()
    private val registerLayers = mutableListOf<Pair<ModelLayerLocation, Supplier<LayerDefinition>>>()
    private fun registerModel(location: String) : ModelResourceLocation =
        ModelResourceLocation(location.resource, ModelResourceLocation.STANDALONE_VARIANT)
            .apply { registerModels.add(this) }
    private fun registerLayer(location: String, supplier: Supplier<LayerDefinition>) : ModelLayerLocation =
        ModelLayerLocation(location.resource, "main")
            .apply { registerLayers.add(this to supplier) }

    val SEAL_LEFT = registerModel("block/seal_left")
    val SEAL_RIGHT = registerModel("block/seal_right")

    fun registerModels(event: ModelEvent.RegisterAdditional) {
        for (location in registerModels.toList()) {
            event.register(location)
        }
    }

    fun registerLayers(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        for ((location, supplier) in registerLayers.toList()) {
            event.registerLayerDefinition(location, supplier)
        }
    }
}