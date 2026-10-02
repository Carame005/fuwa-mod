package com.fuwa.registry;

import com.fuwa.FuwaMod;
import com.fuwa.item.CompanionCatchItem;
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
                            .nutrition(3)
                            .saturationMod(0.4F)
                            .alwaysEat()
                            .build()
            )));

    public static final RegistryObject<Item> FUWA_SPAWN_EGG = ITEMS.register("fuwa_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FUWA, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static final RegistryObject<Item> PRUNCE_SPAWN_EGG = ITEMS.register("prunce_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PRUNCE, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static final RegistryObject<Item> PRUNCE_CAPSULE = ITEMS.register("prunce_capsule",
            () -> new CompanionCatchItem(
                    new Item.Properties().stacksTo(16),
                    ModEntities.PRUNCE,
                    "item.fuwa.prunce_capsule.empty",
                    "item.fuwa.prunce_capsule.filled"
            ));

    public static final RegistryObject<Item> STAR_TWINKLE_BOOK = ITEMS.register("star_twinkle_book",
            () -> new CompanionCatchItem(
                    new Item.Properties().stacksTo(16),
                    ModEntities.FUWA,
                    "item.fuwa.star_twinkle_book.empty",
                    "item.fuwa.star_twinkle_book.filled"
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
