package com.fuwa.registry;

import com.fuwa.FuwaMod;
import com.fuwa.item.CompanionCatchItem;
import com.fuwa.item.CosmoShiningItem;
import com.fuwa.item.MilkyAntennaItem;
import com.fuwa.item.SeleneBowItem;
import com.fuwa.item.SoleilBootsItem;
import com.fuwa.item.StarPunchItem;
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

    public static final RegistryObject<Item> TWINKLE_BOOK = ITEMS.register("twinkle_book",
            () -> new CompanionCatchItem(
                    new Item.Properties().stacksTo(16),
                    ModEntities.FUWA,
                    "item.fuwa.twinkle_book.empty",
                    "item.fuwa.twinkle_book.filled"
            ));

    public static final RegistryObject<Item> STAR_PUNCH = ITEMS.register("star_punch",
            () -> new StarPunchItem(new Item.Properties().stacksTo(1).durability(300)));

    public static final RegistryObject<Item> MILKY_ANTENNA = ITEMS.register("milky_antenna",
            () -> new MilkyAntennaItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SOLEIL_BOOTS = ITEMS.register("soleil_boots",
            () -> new SoleilBootsItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SELENE_BOW = ITEMS.register("selene_bow",
            () -> new SeleneBowItem(new Item.Properties().stacksTo(1).durability(384)));

    public static final RegistryObject<Item> COSMO_SHINING = ITEMS.register("cosmo_shining",
            () -> new CosmoShiningItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TWINKLE_IMAGINATION = ITEMS.register("twinkle_imagination",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SELENE_TWINKLE_IMAGINATION = ITEMS.register("selene_twinkle_imagination",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SOLEIL_TWINKLE_IMAGINATION = ITEMS.register("soleil_twinkle_imagination",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MILKY_TWINKLE_IMAGINATION = ITEMS.register("milky_twinkle_imagination",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> STAR_TWINKLE_IMAGINATION = ITEMS.register("star_twinkle_imagination",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> COSMO_TWINKLE_IMAGINATION = ITEMS.register("cosmo_twinkle_imagination",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
