package com.fuwa.entity.client;

import com.fuwa.entity.FuwaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FuwaRenderer extends GeoEntityRenderer<FuwaEntity> {
    public FuwaRenderer(EntityRendererProvider.Context context) {
        super(context, new FuwaModel());
        this.shadowRadius = 0.4F;
    }
}
