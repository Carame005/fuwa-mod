package com.fuwa.registry;

import com.fuwa.FuwaMod;
import com.fuwa.entity.FuwaEntity;
import com.fuwa.entity.PrunceEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FuwaMod.MOD_ID);

    public static final RegistryObject<EntityType<FuwaEntity>> FUWA =
            ENTITY_TYPES.register("fuwa",
                    () -> EntityType.Builder.of(FuwaEntity::new, MobCategory.CREATURE)
                            .sized(0.8F, 1.4F)
                            .clientTrackingRange(8)
                            .build(new ResourceLocation(FuwaMod.MOD_ID, "fuwa").toString()));

    public static final RegistryObject<EntityType<PrunceEntity>> PRUNCE =
            ENTITY_TYPES.register("prunce",
                    () -> EntityType.Builder.of(PrunceEntity::new, MobCategory.CREATURE)
                            .sized(0.8F, 1.0F)
                            .clientTrackingRange(8)
                            .build(new ResourceLocation(FuwaMod.MOD_ID, "prunce").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
