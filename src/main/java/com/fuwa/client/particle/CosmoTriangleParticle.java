package com.fuwa.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Small colored triangle sparkles that cycle through triangle1..5 textures.
 * Each particle gets a random orientation and gentle spin.
 */
@OnlyIn(Dist.CLIENT)
public class CosmoTriangleParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float spin;

    protected CosmoTriangleParticle(ClientLevel level, double x, double y, double z,
                                    double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.lifetime = 14 + this.random.nextInt(10);
        this.quadSize = 0.08F + this.random.nextFloat() * 0.06F;
        this.gravity = 0.01F;
        this.hasPhysics = false;
        // Random starting angle + light continuous spin (radians).
        this.roll = this.random.nextFloat() * ((float) Math.PI * 2.0F);
        this.oRoll = this.roll;
        this.spin = (this.random.nextFloat() - 0.5F) * 0.35F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.oRoll = this.roll;
        super.tick();
        this.roll += this.spin;
        this.setSpriteFromAge(this.sprites);
        float life = (float) this.age / (float) this.lifetime;
        this.alpha = life < 0.7F ? 1.0F : 1.0F - ((life - 0.7F) / 0.3F);
        this.xd *= 0.94D;
        this.yd *= 0.94D;
        this.zd *= 0.94D;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new CosmoTriangleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
