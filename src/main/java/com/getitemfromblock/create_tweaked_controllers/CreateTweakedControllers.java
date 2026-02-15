package com.getitemfromblock.create_tweaked_controllers;

import com.getitemfromblock.create_tweaked_controllers.block.ModBlocks;
import com.getitemfromblock.create_tweaked_controllers.compat.ComputerCraft.ModComputerCraftProxy;
import com.getitemfromblock.create_tweaked_controllers.config.ModConfigs;
import com.getitemfromblock.create_tweaked_controllers.gui.ModMenuTypes;
import com.getitemfromblock.create_tweaked_controllers.item.ModItems;
import com.getitemfromblock.create_tweaked_controllers.packet.ModPackets;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.createmod.catnip.lang.LangBuilder;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CreateTweakedControllers.ID)
public class CreateTweakedControllers
{
    public static final String ID = "create_tweaked_controllers";
    public static final String NAME = "Create: Tweaked Controllers";

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);

    public CreateTweakedControllers(IEventBus modEventBus, ModContainer modContainer)
    {
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;
        modEventBus.addListener(CreateTweakedControllers::init);
        REGISTRATE.registerEventListeners(modEventBus);
        ModTab.register(modEventBus);
        ModItems.register();
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModMenuTypes.register();
        ModConfigs.register(modContainer);
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ModClientStuff.onConstructor(modEventBus, forgeEventBus);
        }
        ModComputerCraftProxy.register();
    }

    public static void init(final FMLCommonSetupEvent event)
    {
        ModPackets.registerPackets();
    }

    public static CreateRegistrate registrate()
    {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static MutableComponent translateDirect(String key, Object... args)
    {
        return Component.translatable(CreateTweakedControllers.ID + "." + key, LangBuilder.resolveBuilders(args));
    }

    public static MutableComponent translateDirectRaw(String key, Object... args)
    {
        return Component.translatable(key, LangBuilder.resolveBuilders(args));
    }

    public static LangBuilder builder()
    {
        return new LangBuilder(CreateTweakedControllers.ID);
    }

    public static LangBuilder translate(String langKey, Object... args)
    {
        return builder().translate(langKey, args);
    }

    public static void log(String message)
    {
        Create.LOGGER.info(message);
    }

    public static void error(String message)
    {
        Create.LOGGER.error(message);
    }
}
