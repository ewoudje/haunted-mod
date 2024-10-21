package com.ewoudje.spooky

import com.ewoudje.spooky.blocks.SpookyBlocks
import com.ewoudje.spooky.blockentities.SpookyBlockEntities
import com.ewoudje.spooky.capabilities.ShizoCapability
import com.ewoudje.spooky.capabilities.SpookyCapabilities
import com.ewoudje.spooky.client.ClientSpookyMod
import com.ewoudje.spooky.items.SpookyItems
import com.ewoudje.spooky.networking.SpookyPackets
import com.ewoudje.spooky.world.SpookyWorldHandler
import com.ewoudje.spooky.world.SpookyWorldState
import com.ewoudje.spooky.world.SpookyWorldState.Companion.spookyWorldState
import com.ewoudje.spooky.world.fog.FogHandler
import com.ewoudje.spooky.world.fog.FogState
import com.ewoudje.spooky.world.structures.SpookyStructurePieces
import com.ewoudje.spooky.world.structures.SpookyStructures
import com.ewoudje.spooky.world.structures.processor.SpookyStructureProcessors
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.RegisterCommandsEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

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
        MOD_BUS.addListener(SpookyPackets::register)

        FORGE_BUS.addListener(ShizoCapability::tickPlayer)
        FORGE_BUS.addListener(FogHandler::tick)
        FORGE_BUS.addListener(::registerCommands)

        SpookyWorldHandler.register()

        LOGGER.log(Level.INFO, "Spooky mod active!")
    }

    fun registerCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(LiteralArgumentBuilder.literal<CommandSourceStack>(ID).then(
            LiteralArgumentBuilder.literal<CommandSourceStack?>("fog").executes {
                val fog = it.source.level.spookyWorldState.fogState
                fog.position = it.source.anchor.apply(it.source).toVector3d()
                fog.position!!.y = 192.0
                fog.thickness = 100.0
                fog.velocity = Vector3f(0.1f, 0f, 0f)
                fog.direction = Vector3f(0.5f, 1f, 0f).normalize()
                fog.height = (it.source.level.height + 100).toDouble()

                1
            }))
    }

    val VECTOR3D_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE,
        Vector3dc::x,
        ByteBufCodecs.DOUBLE,
        Vector3dc::y,
        ByteBufCodecs.DOUBLE,
        Vector3dc::z,
        ::Vector3d
    )

    fun <T> StreamCodec<ByteBuf, T>.nullable() = object : StreamCodec<ByteBuf, T?> {
        override fun decode(buffer: ByteBuf): T? {
            val notNull = buffer.readBoolean()
            return if (notNull)
                this@nullable.decode(buffer)
            else
                null
        }

        override fun encode(buffer: ByteBuf, value: T?) {
            buffer.writeBoolean(value != null)
            if (value != null) this@nullable.encode(buffer, value)
        }
    }

}

val String.resource get() = ResourceLocation.fromNamespaceAndPath(SpookyMod.ID, this)
val String.component get() = Component.translatable(this)
