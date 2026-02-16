package com.getitemfromblock.create_tweaked_controllers;

import com.getitemfromblock.create_tweaked_controllers.compat.Controllable.ControllerHandler;
import com.getitemfromblock.create_tweaked_controllers.input.MouseCursorHandler;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ModClientStuff
{
    private static boolean controllableLoaded = false;

    public static boolean isControllableLoaded()
    {
        return controllableLoaded;
    }
    
    public static void onConstructor(IEventBus modEventBus, IEventBus forgeEventBus)
    {
        modEventBus.addListener(ModClientStuff::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event)
    {
        if(ModList.get().isLoaded("controllable"))
        {
            controllableLoaded = true;
            ControllerHandler.Register();
        }
        MouseCursorHandler.InitValues();
    }
}
