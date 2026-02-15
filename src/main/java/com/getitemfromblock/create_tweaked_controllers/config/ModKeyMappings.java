package com.getitemfromblock.create_tweaked_controllers.config;

import com.getitemfromblock.create_tweaked_controllers.CreateTweakedControllers;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModKeyMappings
{
    public static KeyMapping KEY_MOUSE_FOCUS;
    public static KeyMapping KEY_MOUSE_RESET;
    public static KeyMapping KEY_CONTROLLER_EXIT;
    public static KeyMapping KEY_CYCLE_DEVICE;

    @SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event)
    {
        KEY_MOUSE_FOCUS = registerKey("mouse_focus", InputConstants.KEY_LALT, event);
        KEY_MOUSE_RESET = registerKey("mouse_reset", InputConstants.KEY_R, event);
        KEY_CONTROLLER_EXIT = registerKey("controller_exit", InputConstants.KEY_TAB, event);
        KEY_CYCLE_DEVICE = registerKey("cycle_device", InputConstants.UNKNOWN.getValue(), event);
    }

    private static KeyMapping registerKey(String name, int keycode, RegisterKeyMappingsEvent event)
    {
        final var key = new KeyMapping(CreateTweakedControllers.ID + ".keybind." + name, keycode, CreateTweakedControllers.NAME);
        event.register(key);
        return key;
    }
}
