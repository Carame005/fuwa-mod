package com.fuwa.entity.client;

import com.fuwa.FuwaMod;
import com.fuwa.entity.FuwaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FuwaModel extends GeoModel<FuwaEntity> {
    private static final ResourceLocation MODEL =
            new ResourceLocation(FuwaMod.MOD_ID, "geo/fuwa2.0.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FuwaMod.MOD_ID, "textures/entity/fuwa.png");
    private static final ResourceLocation ANIMATIONS =
            new ResourceLocation(FuwaMod.MOD_ID, "animations/fuwa.animation.json");

    @Override
    public ResourceLocation getModelResource(FuwaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FuwaEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FuwaEntity animatable) {
        return ANIMATIONS;
    }
}
