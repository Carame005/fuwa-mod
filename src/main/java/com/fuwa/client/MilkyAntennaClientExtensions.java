package com.fuwa.client;

import com.fuwa.client.model.MilkyAntennaModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

@OnlyIn(Dist.CLIENT)
public class MilkyAntennaClientExtensions implements IClientItemExtensions {
    public static final MilkyAntennaClientExtensions INSTANCE = new MilkyAntennaClientExtensions();

    private MilkyAntennaModel model;

    @Override
    @SuppressWarnings("unchecked")
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack,
                                                  EquipmentSlot slot, HumanoidModel<?> defaultModel) {
        if (this.model == null) {
            this.model = new MilkyAntennaModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(MilkyAntennaModel.LAYER_LOCATION));
        }

        // Hide in first person so it never obstructs the camera.
        boolean firstPerson = living == Minecraft.getInstance().player
                && Minecraft.getInstance().options.getCameraType().isFirstPerson();

        HumanoidModel<LivingEntity> source = (HumanoidModel<LivingEntity>) defaultModel;
        source.copyPropertiesTo(this.model);
        this.model.setAllVisible(false);
        this.model.head.visible = !firstPerson;
        this.model.hat.visible = false;
        return this.model;
    }
}
