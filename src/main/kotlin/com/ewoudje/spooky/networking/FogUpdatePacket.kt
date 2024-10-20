package com.ewoudje.spooky.networking

import com.ewoudje.spooky.client.ClientSpookyMod
import com.ewoudje.spooky.client.renderers.RollingFogRenderer
import com.ewoudje.spooky.resource
import com.ewoudje.spooky.world.fog.FogState
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

data class FogUpdatePacket(
    val fogState: FogState
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<FogUpdatePacket> = TYPE

    fun handle(ctx: IPayloadContext) {
        RollingFogRenderer.setClientFog(fogState, ctx.player().position().toVector3d())
    }

    companion object {
        val TYPE = CustomPacketPayload.Type<FogUpdatePacket>("fog_update".resource)
        val STREAM_CODEC = FogState.STREAM_CODEC.map(::FogUpdatePacket, FogUpdatePacket::fogState)
    }
}