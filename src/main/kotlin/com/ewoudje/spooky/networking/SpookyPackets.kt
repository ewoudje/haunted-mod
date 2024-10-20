package com.ewoudje.spooky.networking

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler
import net.neoforged.neoforge.network.registration.HandlerThread

object SpookyPackets {
    const val PACKET_VERSION = "1"

    fun register(event: RegisterPayloadHandlersEvent) {
        val version = event.registrar(PACKET_VERSION).executesOn(HandlerThread.MAIN)
        version.playToClient(
            FogUpdatePacket.TYPE,
            FogUpdatePacket.STREAM_CODEC,
            IPayloadHandler(FogUpdatePacket::handle)
        )
    }

}