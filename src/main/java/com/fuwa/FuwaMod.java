package com.fuwa;

import com.fuwa.registry.ModBlockEntities;
import com.fuwa.registry.ModBlocks;
import com.fuwa.registry.ModCreativeModeTabs;
import com.fuwa.registry.ModEntities;
import com.fuwa.registry.ModItems;
import com.fuwa.registry.ModMenuTypes;
import com.fuwa.registry.ModParticles;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(FuwaMod.MOD_ID)
public class FuwaMod {
    public static final String MOD_ID = "fuwa";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FuwaMod() {
        GeckoLib.initialize();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModParticles.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Fuwa mod loaded (GeckoLib animated mob).");
    }
}
