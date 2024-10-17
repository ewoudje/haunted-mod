package com.ewoudje.spooky.client

import com.ewoudje.spooky.resource
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.ShaderInstance
import net.neoforged.neoforge.client.event.RegisterShadersEvent

object Shaders {
    lateinit var ROLLING_FOG: ShaderInstance private set

    fun registerShaders(event: RegisterShadersEvent) {
        fun reg(location: String, format: VertexFormat) =
            ShaderInstance(event.resourceProvider, location.resource, format).apply { event.registerShader(this) { } }

        ROLLING_FOG = reg("rolling_fog", DefaultVertexFormat.POSITION_TEX)
    }

}