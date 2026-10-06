package com.fuwa.item;

import com.fuwa.FuwaMod;
import com.fuwa.client.MilkyAntennaClientExtensions;
import com.fuwa.registry.ModArmorMaterials;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MilkyAntennaItem extends ArmorItem {
    private static final String ARMOR_TEXTURE = FuwaMod.MOD_ID + ":textures/item/milky_antenna.png";

    public MilkyAntennaItem(Properties properties) {
        super(ModArmorMaterials.MILKY, Type.HELMET, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            consumer.accept(MilkyAntennaClientExtensions.INSTANCE);
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide()) {
            return;
        }
        // Refresh continuously without icon/particles clutter.
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false, true));
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ARMOR_TEXTURE;
    }
}
