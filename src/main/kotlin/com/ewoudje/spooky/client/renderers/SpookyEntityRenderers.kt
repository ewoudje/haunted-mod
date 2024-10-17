package com.ewoudje.spooky.client.renderers

import com.ewoudje.spooky.blockentities.SpookyBlockEntities
import com.ewoudje.spooky.client.renderers.blockentity.SealBlockEntityRenderer
import com.ewoudje.spooky.client.renderers.blockentity.SealHackerBlockEntityRenderer
import net.neoforged.neoforge.client.event.EntityRenderersEvent

object SpookyEntityRenderers {

    fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(SpookyBlockEntities.SEAL, ::SealBlockEntityRenderer)
        event.registerBlockEntityRenderer(SpookyBlockEntities.SEAL_HACKER, ::SealHackerBlockEntityRenderer)
    }

}