package com.fuwa.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Intentionally empty: Blockbench first-person display is authored against the vanilla
 * bow-hold reference, so we must not override hand transforms or the pose will not match.
 */
@OnlyIn(Dist.CLIENT)
public class SeleneBowClientExtensions implements IClientItemExtensions {
    public static final SeleneBowClientExtensions INSTANCE = new SeleneBowClientExtensions();
}
