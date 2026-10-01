package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.entity.FuwaEntity;
import com.fuwa.entity.client.FuwaModel;
import com.fuwa.entity.client.FuwaRenderer;
import com.fuwa.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FUWA.get(), FuwaEntity.createAttributes().build());
    }

    @Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(FuwaModel.LAYER_LOCATION, FuwaModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.FUWA.get(), FuwaRenderer::new);
        }
    }
}
