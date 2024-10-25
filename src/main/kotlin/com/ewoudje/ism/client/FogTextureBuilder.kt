package com.ewoudje.ism.client

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

object FogTextureBuilder {

    fun build2DTexture(textureSize: Int, sample: (Int, Int) -> Float): FloatBuffer {
        val buffer = MemoryUtil.memAllocFloat(textureSize * textureSize)
        buffer.clear()
        for (x in 0 until textureSize) {
            for (y in 0 until textureSize) {
                buffer.put(sample(x, y))
            }
        }
        buffer.flip()
        return buffer
    }
}