package com.ewoudje.spooky.client

import com.ewoudje.spooky.client.FogTextureBuilder.sampler
import net.minecraft.world.level.levelgen.DensityFunction
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

object FogTextureBuilder {
    fun FloatBuffer.sampler(size: Int): (Int, Int, Int) -> Float = {x, y, z -> this[y + z * size + x * size * size]}
    fun FloatBuffer.samplerD(size: Int, divider: Int): (Int, Int, Int) -> Float {
        val size2 = size * size;

        return { x, y, z ->
            val dX = x / divider
            val dY = y / divider
            val dZ = z / divider

            val s = this[dY + dZ * size + dX * size2]

            val rX = x % divider
            val rY = y % divider
            val rZ = z % divider
            val xAvg = if (rX == 0 || dX + 1 == size) s else
                (s * rX + ((divider - rX) * this[dY + dZ * size + (dX + 1) * size2])) / divider

            val yAvg = if (rY == 0 || dY + 1 == size) s else
                (s * rY + ((divider - rY) * this[(dY + 1) + dZ * size + dX * size2])) / divider

            val zAvg = if (rZ == 0 || dZ + 1 == size) s else
                (s * rZ + ((divider - rZ) * this[dY + (dZ + 1) * size + dX * size2])) / divider

            xAvg + yAvg + zAvg / 3f
        }
    }

    fun build3DTexture(textureSize: Int, sample: (Int, Int, Int) -> Float): FloatBuffer {
        val buffer = MemoryUtil.memAllocFloat(textureSize*textureSize*textureSize*4)
        for (x in 0 until textureSize) {
            for (z in 0 until textureSize) {
                for (y in 0 until textureSize) {
                    buffer.put(sample(x,y,z))
                }
            }
        }
        buffer.flip()
        return buffer
    }

    fun buildShadow3DTexture(textureSize: Int, angle: Float, sample: (Int, Int, Int) -> Float): FloatBuffer {
        val buffer = MemoryUtil.memAllocFloat(textureSize*textureSize*textureSize*4)
        for (x in 0 until textureSize) {
            for (z in 0 until textureSize) {
                var farthestOut = 0f
                for (y in 0 until textureSize) {
                    val s = sample(x, y, z)
                    farthestOut -= 0.07f

                    if (s > farthestOut) {
                        farthestOut = s
                        buffer.put(0f)
                    } else {
                        buffer.put(farthestOut)
                    }
                }
            }
        }
        buffer.flip()
        return buffer
    }
}