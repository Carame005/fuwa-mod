package com.fuwa.registry;

import com.fuwa.FuwaMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, FuwaMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> STAR_PARTICLE =
            PARTICLE_TYPES.register("star_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> STAR_WAVE =
            PARTICLE_TYPES.register("star_wave", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> COSMO_TRIANGLE =
            PARTICLE_TYPES.register("cosmo_triangle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> COSMO_BLUE_MIST =
            PARTICLE_TYPES.register("cosmo_blue_mist", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
