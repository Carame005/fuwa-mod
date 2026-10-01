package com.fuwa.entity.client;

import com.fuwa.FuwaMod;
import com.fuwa.entity.FuwaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FuwaRenderer extends MobRenderer<FuwaEntity, FuwaModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FuwaMod.MOD_ID, "textures/entity/fuwa.png");

    public FuwaRenderer(EntityRendererProvider.Context context) {
        super(context, new FuwaModel(context.bakeLayer(FuwaModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(FuwaEntity entity) {
        return TEXTURE;
    }
}
