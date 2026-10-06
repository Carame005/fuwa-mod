package com.fuwa.client;

import com.fuwa.client.model.SoleilBootsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

@OnlyIn(Dist.CLIENT)
public class SoleilBootsClientExtensions implements IClientItemExtensions {
    public static final SoleilBootsClientExtensions INSTANCE = new SoleilBootsClientExtensions();

    private SoleilBootsModel model;

    @Override
    @SuppressWarnings("unchecked")
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack,
                                                  EquipmentSlot slot, HumanoidModel<?> defaultModel) {
        if (this.model == null) {
            this.model = new SoleilBootsModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(SoleilBootsModel.LAYER_LOCATION));
        }

        HumanoidModel<LivingEntity> source = (HumanoidModel<LivingEntity>) defaultModel;
        source.copyPropertiesTo(this.model);
        this.model.setAllVisible(false);
        this.model.rightLeg.visible = true;
        this.model.leftLeg.visible = true;
        return this.model;
    }
}
