package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.entity.FuwaEntity;
import com.fuwa.entity.PrunceEntity;
import com.fuwa.entity.client.FuwaRenderer;
import com.fuwa.entity.client.PrunceRenderer;
import com.fuwa.item.CompanionCatchItem;
import com.fuwa.registry.ModEntities;
import com.fuwa.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FUWA.get(), FuwaEntity.createAttributes().build());
        event.put(ModEntities.PRUNCE.get(), PrunceEntity.createAttributes().build());
    }

    @Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                registerFilledProperty(ModItems.PRUNCE_CAPSULE.get());
                registerFilledProperty(ModItems.STAR_TWINKLE_BOOK.get());
            });
        }

        private static void registerFilledProperty(net.minecraft.world.item.Item item) {
            ItemProperties.register(item, new ResourceLocation(FuwaMod.MOD_ID, "filled"),
                    (stack, level, entity, seed) -> CompanionCatchItem.isFilled(stack) ? 1.0F : 0.0F);
        }

        @SubscribeEvent
        public static void onItemColors(RegisterColorHandlersEvent.Item event) {
            // Keep custom spawn-egg PNGs untinted (ForgeSpawnEggItem otherwise multiplies egg colors).
            event.register((stack, tintIndex) -> -1,
                    ModItems.PRUNCE_SPAWN_EGG.get(),
                    ModItems.FUWA_SPAWN_EGG.get());
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.FUWA.get(), FuwaRenderer::new);
            event.registerEntityRenderer(ModEntities.PRUNCE.get(), PrunceRenderer::new);
        }
    }
}
