package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.client.model.MeteoriteModel;
import com.fuwa.client.model.MilkyAntennaModel;
import com.fuwa.client.model.SoleilBootsModel;
import com.fuwa.client.particle.CosmoBlueMistParticle;
import com.fuwa.client.particle.CosmoTriangleParticle;
import com.fuwa.client.particle.StarTwinkleParticle;
import com.fuwa.client.particle.StarWaveParticle;
import com.fuwa.client.screen.TwinkleGeneratorScreen;
import com.fuwa.entity.FuwaEntity;
import com.fuwa.entity.PrunceEntity;
import com.fuwa.entity.client.FuwaRenderer;
import com.fuwa.entity.client.MeteoriteRenderer;
import com.fuwa.entity.client.PrunceRenderer;
import com.fuwa.entity.client.SeleneArrowRenderer;
import com.fuwa.item.CompanionCatchItem;
import com.fuwa.item.SeleneBowItem;
import com.fuwa.registry.ModEntities;
import com.fuwa.registry.ModItems;
import com.fuwa.registry.ModMenuTypes;
import com.fuwa.registry.ModParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
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
                registerFilledProperty(ModItems.TWINKLE_BOOK.get());
                registerSeleneBowProperties();
                MenuScreens.register(ModMenuTypes.TWINKLE_GENERATOR.get(), TwinkleGeneratorScreen::new);
            });
        }

        private static void registerFilledProperty(net.minecraft.world.item.Item item) {
            ItemProperties.register(item, new ResourceLocation(FuwaMod.MOD_ID, "filled"),
                    (stack, level, entity, seed) -> CompanionCatchItem.isFilled(stack) ? 1.0F : 0.0F);
        }

        private static void registerSeleneBowProperties() {
            ItemProperties.register(ModItems.SELENE_BOW.get(), new ResourceLocation(FuwaMod.MOD_ID, "pulling"),
                    (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

            ItemProperties.register(ModItems.SELENE_BOW.get(), new ResourceLocation(FuwaMod.MOD_ID, "pull"),
                    (stack, level, entity, seed) -> {
                        if (entity == null || !(stack.getItem() instanceof SeleneBowItem)
                                || entity.getUseItem() != stack) {
                            return 0.0F;
                        }
                        int charged = stack.getItem().getUseDuration(stack) - entity.getUseItemRemainingTicks();
                        return SeleneBowItem.getPowerForTime(charged);
                    });
        }

        @SubscribeEvent
        public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.STAR_PARTICLE.get(), StarTwinkleParticle.Provider::new);
            event.registerSpriteSet(ModParticles.STAR_WAVE.get(), StarWaveParticle.Provider::new);
            event.registerSpriteSet(ModParticles.COSMO_TRIANGLE.get(), CosmoTriangleParticle.Provider::new);
            event.registerSpriteSet(ModParticles.COSMO_BLUE_MIST.get(), CosmoBlueMistParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onItemColors(RegisterColorHandlersEvent.Item event) {
            // Keep custom spawn-egg PNGs untinted (ForgeSpawnEggItem otherwise multiplies egg colors).
            event.register((stack, tintIndex) -> -1,
                    ModItems.PRUNCE_SPAWN_EGG.get(),
                    ModItems.FUWA_SPAWN_EGG.get());
        }

        @SubscribeEvent
        public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(MilkyAntennaModel.LAYER_LOCATION, MilkyAntennaModel::createBodyLayer);
            event.registerLayerDefinition(SoleilBootsModel.LAYER_LOCATION, SoleilBootsModel::createBodyLayer);
            event.registerLayerDefinition(MeteoriteModel.LAYER_LOCATION, MeteoriteModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.FUWA.get(), FuwaRenderer::new);
            event.registerEntityRenderer(ModEntities.PRUNCE.get(), PrunceRenderer::new);
            event.registerEntityRenderer(ModEntities.SELENE_ARROW.get(), SeleneArrowRenderer::new);
            event.registerEntityRenderer(ModEntities.METEORITE.get(), MeteoriteRenderer::new);
        }
    }
}
