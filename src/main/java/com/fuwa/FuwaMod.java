package com.fuwa;

import com.fuwa.registry.ModCreativeModeTabs;
import com.fuwa.registry.ModEntities;
import com.fuwa.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FuwaMod.MOD_ID)
public class FuwaMod {
    public static final String MOD_ID = "fuwa";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FuwaMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Fuwa mod loaded (GeckoLib animated mob).");
    }
}
