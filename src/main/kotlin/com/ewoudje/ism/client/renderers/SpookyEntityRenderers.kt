package com.ewoudje.ism.client.renderers

import com.ewoudje.ism.blockentities.IsmBlockEntities
import com.ewoudje.ism.client.renderers.blockentity.SealBlockEntityRenderer
import com.ewoudje.ism.client.renderers.blockentity.SealHackerBlockEntityRenderer
import net.neoforged.neoforge.client.event.EntityRenderersEvent

object SpookyEntityRenderers {

    fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(IsmBlockEntities.SEAL, ::SealBlockEntityRenderer)
        event.registerBlockEntityRenderer(IsmBlockEntities.SEAL_HACKER, ::SealHackerBlockEntityRenderer)
    }

}