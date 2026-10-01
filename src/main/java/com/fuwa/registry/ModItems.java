package com.fuwa.registry;

import com.fuwa.FuwaMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, FuwaMod.MOD_ID);

    public static final RegistryObject<Item> STELLAR_DONUT = ITEMS.register("stellar_donut",
            () -> new Item(new Item.Properties().food(
                    new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(3) // 1.5 shanks (muslito y medio)
                            .saturationMod(0.4F)
                            .alwaysEat()
                            .build()
            )));

    public static final RegistryObject<Item> FUWA_SPAWN_EGG = ITEMS.register("fuwa_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FUWA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
