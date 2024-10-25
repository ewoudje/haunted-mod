package com.ewoudje.ism.networking

import com.ewoudje.ism.client.ClientFogHandler
import com.ewoudje.ism.resource
import com.ewoudje.ism.world.fog.FogState
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

data class FogUpdatePacket(
    val fogState: FogState
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<FogUpdatePacket> = TYPE

    fun handle(ctx: IPayloadContext) {
        ClientFogHandler.updateFog(fogState)
    }

    companion object {
        val TYPE = CustomPacketPayload.Type<FogUpdatePacket>("fog_update".resource)
        val STREAM_CODEC = FogState.STREAM_CODEC.map(::FogUpdatePacket, FogUpdatePacket::fogState)
    }
}