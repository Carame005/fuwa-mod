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
 * Soft blue perfume mist that drifts and fades like vanilla cloud.
 */
@OnlyIn(Dist.CLIENT)
public class CosmoBlueMistParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected CosmoBlueMistParticle(ClientLevel level, double x, double y, double z,
                                    double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.xd = xSpeed + (this.random.nextDouble() - 0.5D) * 0.02D;
        this.yd = ySpeed + this.random.nextDouble() * 0.02D;
        this.zd = zSpeed + (this.random.nextDouble() - 0.5D) * 0.02D;
        this.lifetime = 30 + this.random.nextInt(20);
        this.quadSize = 0.55F + this.random.nextFloat() * 0.35F;
        this.gravity = -0.005F;
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
        this.setAlpha(0.85F);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        float life = (float) this.age / (float) this.lifetime;
        this.quadSize = this.quadSize * (1.0F + 0.012F);
        this.alpha = life < 0.55F ? 0.85F : 0.85F * (1.0F - ((life - 0.55F) / 0.45F));
        this.xd *= 0.96D;
        this.yd *= 0.96D;
        this.zd *= 0.96D;
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
            return new CosmoBlueMistParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
