package com.ewoudje.ism.client.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType

class UnsealParticleProvider(val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
    override fun createParticle(
        type: SimpleParticleType,
        level: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        xSpeed: Double,
        ySpeed: Double,
        zSpeed: Double
    ): TextureSheetParticle = UnsealParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, text[index++ % text.length])

    class UnsealParticle(
        level: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        val xPos: Double,
        val yPos: Double,
        val zPos: Double,
        sprites: SpriteSet,
        char: Char
    ) : TextureSheetParticle(level, x, y, z, 0.0, 0.0, 0.0) {

        init {
            setSprite(sprites[charToInt(char), 26])
            xo = x
            yo = y
            zo = z
        }

        override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

        override fun tick() {
            /*
            xo = x
            yo = y
            zo = z

            val xDiff = clamp(xPos - x, -MAX_SPEED, MAX_SPEED)
            val yDiff = clamp(yPos - y, -MAX_SPEED, MAX_SPEED)
            val zDiff = clamp(zPos - z, -MAX_SPEED, MAX_SPEED)

            if (xDiff * xDiff + yDiff * yDiff + zDiff * zDiff < 1)
                remove()
            else
                move(xDiff + x, yDiff + y, zDiff + z)*/
            remove()
        }

        companion object {
            val MAX_SPEED = 0.2

            fun charToInt(c: Char): Int = when (c.lowercaseChar()) {
                'a' -> 0
                'b' -> 1
                'c' -> 2
                'd' -> 3
                'e' -> 4
                'f' -> 5
                'g' -> 6
                'h' -> 7
                'i' -> 8
                'j' -> 9
                'k' -> 10
                'l' -> 11
                'm' -> 12
                'n' -> 13
                'o' -> 14
                'p' -> 15
                'q' -> 16
                'r' -> 17
                's' -> 18
                't' -> 19
                'u' -> 20
                'v' -> 21
                'w' -> 22
                'x' -> 23
                'y' -> 24
                'z' -> 25
                else -> 26
            }
        }
    }

    companion object {
        private var text = "_"
        private var index = 0
        fun setString(string: String) {
            text = string
            index = 0
        }
    }
}