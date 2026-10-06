package com.fuwa.registry;

import com.fuwa.FuwaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FuwaMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FUWA_TAB = CREATIVE_MODE_TABS.register("fuwa_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.fuwa"))
                    .icon(() -> new ItemStack(ModItems.STELLAR_DONUT.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.STELLAR_DONUT.get());
                        output.accept(ModItems.FUWA_SPAWN_EGG.get());
                        output.accept(ModItems.PRUNCE_SPAWN_EGG.get());
                        output.accept(ModItems.PRUNCE_CAPSULE.get());
                        output.accept(ModItems.TWINKLE_BOOK.get());
                        output.accept(ModItems.STAR_PUNCH.get());
                        output.accept(ModItems.MILKY_ANTENNA.get());
                        output.accept(ModItems.SOLEIL_BOOTS.get());
                        output.accept(ModItems.SELENE_BOW.get());
                        output.accept(ModItems.COSMO_SHINING.get());
                        output.accept(ModBlocks.TWINKLE_GENERATOR.get());
                        output.accept(ModItems.TWINKLE_IMAGINATION.get());
                        output.accept(ModItems.SELENE_TWINKLE_IMAGINATION.get());
                        output.accept(ModItems.SOLEIL_TWINKLE_IMAGINATION.get());
                        output.accept(ModItems.MILKY_TWINKLE_IMAGINATION.get());
                        output.accept(ModItems.STAR_TWINKLE_IMAGINATION.get());
                        output.accept(ModItems.COSMO_TWINKLE_IMAGINATION.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
