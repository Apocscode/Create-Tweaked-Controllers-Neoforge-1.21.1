package com.getitemfromblock.create_tweaked_controllers.controller;

import com.getitemfromblock.create_tweaked_controllers.config.ModClientConfig;
import com.getitemfromblock.create_tweaked_controllers.config.ModKeyMappings;
import com.getitemfromblock.create_tweaked_controllers.input.DeviceEnumerator;
import com.getitemfromblock.create_tweaked_controllers.input.GamepadInputs;
import com.getitemfromblock.create_tweaked_controllers.input.JoystickInputs;
import com.getitemfromblock.create_tweaked_controllers.input.MouseCursorHandler;
import com.simibubi.create.foundation.utility.ControlsUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TweakedControlsUtil
{
    public static ControlProfile profile = new ControlProfile();
    public static ControllerRedstoneOutput output = new ControllerRedstoneOutput();
    private static boolean lastFocusKeyState = false;
    private static boolean isFocusActive = false;
    private static boolean wasFocusActive = false;
    private static ControlType controlType = ControlType.KEYBOARD_MOUSE;
    private static boolean lastCycleKeyState = false;

    public static ControlType GetActiveProfileType()
    {
        return controlType;
    }

    public static void FreeFocus()
    {
        isFocusActive = false;
        lastFocusKeyState = false;
        wasFocusActive = false;
    }

    public static void SelectProfileType(ControlType type)
    {
        profile.Load(type);
        controlType = type;
    }

    private static void BackgroundUpdate()
    {
        //if (!ModClientConfig.AUTO_DETECT_INPUT_TYPE.get() || controlType.IsAdapted()) return;
        if (JoystickInputs.HasJoystick())
        {
            controlType = ControlType.JOYSTICK;
        }
        else
        {
            controlType = ControlType.KEYBOARD_MOUSE;
        }
        profile.Load(controlType);
    }

    public static void GuiUpdate()
    {
        MouseCursorHandler.Update();
        if (ControlsUtil.isActuallyPressed(ModKeyMappings.KEY_MOUSE_RESET))
        {
            ModKeyMappings.KEY_MOUSE_RESET.setDown(false);
            MouseCursorHandler.ResetCenter();
        }
        JoystickInputs.GetControls();
        if (ModClientConfig.USE_CUSTOM_MAPPINGS.get())
        {
            FillInputs(false);
        }
        else
        {
            GamepadInputs.GetControls();
            FillGamepadInputs(false);
        }
    }

    private static void HandleMouseKeyBinds()
    {
        if (ModClientConfig.TOGGLE_MOUSE_FOCUS.get())
        {
            if (ControlsUtil.isActuallyPressed(ModKeyMappings.KEY_MOUSE_FOCUS))
            {
                if (!lastFocusKeyState)
                {
                    lastFocusKeyState = true;
                    isFocusActive = !isFocusActive;
                }
            }
            else
            {
                lastFocusKeyState = false;
            }
        }
        else
        {
            isFocusActive = ControlsUtil.isActuallyPressed(ModKeyMappings.KEY_MOUSE_FOCUS);
        }
        if (isFocusActive)
        {
            if (!wasFocusActive)
            {
                MouseCursorHandler.ActivateMouseLock();
            }
            else
            {
                MouseCursorHandler.Update();
            }
            wasFocusActive = true;
        }
        else if (wasFocusActive)
        {
            MouseCursorHandler.DeactivateMouseLock();
            wasFocusActive = false;
        }
        if (ControlsUtil.isActuallyPressed(ModKeyMappings.KEY_MOUSE_RESET))
        {
            ModKeyMappings.KEY_MOUSE_RESET.setDown(false);
            MouseCursorHandler.ResetCenter();
        }
    }

    public static void Update()
    {
        Update(false);
    }

    public static void Update(boolean useFullPrec)
    {
        HandleCycleDevice();
        if (ModClientConfig.USE_CUSTOM_MAPPINGS.get())
        {
            //BackgroundUpdate();
            HandleMouseKeyBinds();
            if (profile.hasJoystickInput)
            {
                JoystickInputs.GetControls();
            }
            FillInputs(useFullPrec);
        }
        else
        {
            GamepadInputs.GetControls();
            FillGamepadInputs(useFullPrec);
        }
        
    }

    private static void HandleCycleDevice()
    {
        if (ModKeyMappings.KEY_CYCLE_DEVICE == null) return;
        boolean pressed = ControlsUtil.isActuallyPressed(ModKeyMappings.KEY_CYCLE_DEVICE);
        if (pressed && !lastCycleKeyState)
        {
            lastCycleKeyState = true;
            if (ModClientConfig.USE_CUSTOM_MAPPINGS.get())
            {
                // Cycle joystick device
                int newIdx = JoystickInputs.CycleJoystick();
                String name = newIdx >= 0 ? DeviceEnumerator.getDeviceName(newIdx) : null;
                if (name == null) name = "None";
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null)
                {
                    mc.player.displayClientMessage(
                        Component.literal("[Tweaked Controllers] Joystick: " + newIdx + " - " + name), true);
                }
            }
            else
            {
                // Cycle gamepad device
                int newIdx = GamepadInputs.CycleGamepad();
                String name = newIdx >= 0 ? DeviceEnumerator.getDeviceName(newIdx) : null;
                if (name == null) name = "None";
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null)
                {
                    mc.player.displayClientMessage(
                        Component.literal("[Tweaked Controllers] Gamepad: " + newIdx + " - " + name), true);
                }
            }
        }
        else if (!pressed)
        {
            lastCycleKeyState = false;
        }
    }

    public static void FillInputs(boolean useFullPrec)
    {
        profile.duplicatedKeys.forEach(key ->
        {
            key.setDown(false);
            while (key.consumeClick()) {};
        });
        for (int i = 0; i < GamepadInputs.buttons.length; i++)
        {
            GamepadInputs.buttons[i] = profile.GetButton(i);
        }
        for (int i = 0; i < GamepadInputs.axis.length; i++)
        {
            GamepadInputs.axis[i] = profile.GetAxis(i);
        }
        FillGamepadInputs(useFullPrec);
    }

    public static void FillGamepadInputs(boolean useFullPrec)
    {
        for (int i = 0; i < output.buttons.length; i++)
        {
            output.buttons[i] = GamepadInputs.GetButton(i);
        }
        for (int i = 0; i < output.axis.length; i++)
        {
            if (i >= 4) // triggers
            {
                float v = (GamepadInputs.GetAxis(i) + 1) / 2;
                if (v < 0) v = 0;
                if (v > 1) v = 1;
                if (useFullPrec)
                {
                    output.fullAxis[i] = v;
                }
                output.axis[i] = (byte)(Math.round(v * 15));
            }
            else // joystick axis
            {
                float v = GamepadInputs.GetAxis(i);
                boolean negative = v < 0;
                if (negative) v = -v;
                if (v < 0) v = 0;
                if (v > 1) v = 1;
                if (useFullPrec)
                {
                    output.fullAxis[i] = negative ? -v : v;
                }
                output.axis[i] = (byte)Math.round(v * 15);
                if (negative && output.axis[i] > 0) output.axis[i] = (byte)(output.axis[i] + 16);
            }
        }
    }
}