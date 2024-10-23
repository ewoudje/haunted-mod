package com.ewoudje.spooky.client.particles

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.world.phys.Vec3

class VisionParticleProvider(val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
    override fun createParticle(
        type: SimpleParticleType,
        level: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        xSpeed: Double,
        ySpeed: Double,
        zSpeed: Double
    ): VisionParticle = VisionParticle(level, x, y, z, xSpeed, ySpeed, zSpeed)

    inner class VisionParticle(
        level: ClientLevel,
        val x: Double,
        val y: Double,
        val z: Double,
        xSpeed: Double,
        ySpeed: Double,
        zSpeed: Double,
    ) : TextureSheetParticle(level, x, y, z, 0.0, 0.0, 0.0) {
        init {
            setSprite(sprites[random])
            val size = random.nextFloat() * 3f + 5f
            setSize(size * 0.1f, size * 0.1f)
            scale(size)
            setLifetime(random.nextInt(120, 200))
        }

        override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

        override fun tick() {
            super.tick()
            setPos(
                x + random.nextDouble() * 0.2 - 0.1,
                y + random.nextDouble() * 0.2 - 0.1,
                z + random.nextDouble() * 0.2 - 0.1
            )

            val player = Minecraft.getInstance().player ?: return
            val d = player.lookAngle.dot(Vec3(this.x - player.x, this.y - player.eyeY, this.z - player.z).normalize())
            if (d > 0.9) remove()
        }
    }
}