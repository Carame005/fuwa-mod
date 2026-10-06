package com.fuwa.client.particle;

import com.fuwa.event.StarPunchWaveEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StarWaveParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float baseSize;

    protected StarWaveParticle(ClientLevel level, double x, double y, double z,
                               double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        // Keep the exact velocity from the server so motion matches the damaging projectile.
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        // Cover the full 6-block flight at STEP_PER_TICK.
        this.lifetime = Math.max(2, (int) Math.ceil(StarPunchWaveEvents.MAX_DISTANCE / StarPunchWaveEvents.STEP_PER_TICK));
        this.baseSize = 2.5F;
        this.quadSize = this.baseSize;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // Constant velocity — no friction — for continuous flight.
        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;

        this.setSpriteFromAge(this.sprites);
        float progress = (float) this.age / (float) this.lifetime;
        this.quadSize = this.baseSize * (1.0F - progress * 0.08F);
        this.alpha = progress < 0.85F ? 1.0F : 1.0F - ((progress - 0.85F) / 0.15F);
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
            return new StarWaveParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
