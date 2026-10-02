package com.fuwa.entity.client;

import com.fuwa.FuwaMod;
import com.fuwa.entity.PrunceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PrunceModel extends GeoModel<PrunceEntity> {
    private static final ResourceLocation MODEL =
            new ResourceLocation(FuwaMod.MOD_ID, "geo/prunce.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FuwaMod.MOD_ID, "textures/entity/prunce.png");
    private static final ResourceLocation ANIMATIONS =
            new ResourceLocation(FuwaMod.MOD_ID, "animations/prunce.animation.json");

    @Override
    public ResourceLocation getModelResource(PrunceEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PrunceEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PrunceEntity animatable) {
        return ANIMATIONS;
    }
}
