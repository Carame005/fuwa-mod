package com.fuwa.registry;

import com.fuwa.FuwaMod;
import com.fuwa.block.entity.TwinkleGeneratorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FuwaMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<TwinkleGeneratorBlockEntity>> TWINKLE_GENERATOR =
            BLOCK_ENTITIES.register("twinkle_generator",
                    () -> BlockEntityType.Builder.of(TwinkleGeneratorBlockEntity::new,
                            ModBlocks.TWINKLE_GENERATOR.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
