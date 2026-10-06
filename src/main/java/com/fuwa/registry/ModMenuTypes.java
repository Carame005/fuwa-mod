package com.fuwa.registry;

import com.fuwa.FuwaMod;
import com.fuwa.menu.TwinkleGeneratorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, FuwaMod.MOD_ID);

    public static final RegistryObject<MenuType<TwinkleGeneratorMenu>> TWINKLE_GENERATOR =
            MENUS.register("twinkle_generator", () -> IForgeMenuType.create(TwinkleGeneratorMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
