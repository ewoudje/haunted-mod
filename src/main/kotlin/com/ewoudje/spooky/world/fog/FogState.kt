package com.ewoudje.spooky.world.fog

import com.ewoudje.spooky.SpookyMod.VECTOR3D_CODEC
import com.ewoudje.spooky.SpookyMod.nullable
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.joml.Vector3d
import org.joml.Vector3f

class FogState(
    position: Vector3d? = null,
    direction: Vector3f = Vector3f(),
    velocity: Vector3f = Vector3f(),
    thickness: Double = 192.0,
    height: Double = 300.0,
) {
    private lateinit var setDirty: () -> Unit
    private var networkDirty = true
    var position = position
        set(value) { field = value; setDirty() }
    var direction = direction
        set(value) { field = value; setDirty() }
    var velocity = velocity
        set(value) { field = value; setDirty() }
    var thickness = thickness
        set(value) { field = value; setDirty() }
    var height = height
        set(value) { field = value; setDirty() }

    constructor(tag: CompoundTag) : this(
        if (tag.getBoolean("visible"))
            Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"))
        else null,
        Vector3f(tag.getFloat("dirX"), tag.getFloat("dirY"), tag.getFloat("dirZ")),
        Vector3f(tag.getFloat("velX"), tag.getFloat("velY"), tag.getFloat("velZ")),
        tag.getDouble("thickness"),
        tag.getDouble("height")
    )

    fun save(tag: CompoundTag) {
        val pos = position
        tag.putBoolean("visible", pos != null)
        if (pos != null) {
            tag.putDouble("x", pos.x)
            tag.putDouble("y", pos.y)
            tag.putDouble("z", pos.z)
        }

        tag.putFloat("dirX", direction.x)
        tag.putFloat("dirY", direction.y)
        tag.putFloat("dirZ", direction.z)

        tag.putFloat("velX", velocity.x)
        tag.putFloat("velY", velocity.y)
        tag.putFloat("velZ", velocity.z)

        tag.putDouble("thickness", thickness)
        tag.putDouble("height", height)
    }

    fun consumeNetworkDirty(): Boolean {
        val r = networkDirty
        networkDirty = false
        return r
    }

    fun setSetDirty(setDirty: () -> Unit) {
        this.setDirty = { setDirty(); this.networkDirty = true }
    }

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            VECTOR3D_CODEC.nullable(),
            FogState::position,
            ByteBufCodecs.VECTOR3F,
            FogState::direction,
            ByteBufCodecs.VECTOR3F,
            FogState::velocity,
            ByteBufCodecs.DOUBLE,
            FogState::thickness,
            ByteBufCodecs.DOUBLE,
            FogState::height,
            ::FogState
        )
    }
}