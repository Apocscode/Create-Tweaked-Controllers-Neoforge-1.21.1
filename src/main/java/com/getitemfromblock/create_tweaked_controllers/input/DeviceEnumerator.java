package com.getitemfromblock.create_tweaked_controllers.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

/**
 * Utility class to enumerate all connected joystick and gamepad devices.
 * Used by the device override system to let users select a specific device.
 */
public class DeviceEnumerator
{
    public static class DeviceInfo
    {
        public final int index;
        public final String name;
        public final boolean isGamepad;

        public DeviceInfo(int index, String name, boolean isGamepad)
        {
            this.index = index;
            this.name = name;
            this.isGamepad = isGamepad;
        }

        @Override
        public String toString()
        {
            return index + ": " + name + (isGamepad ? " (Gamepad)" : " (Joystick)");
        }
    }

    /**
     * Get all connected joystick devices (includes gamepads).
     */
    public static List<DeviceInfo> getAllJoysticks()
    {
        List<DeviceInfo> devices = new ArrayList<>();
        for (int i = 0; i < 16; i++)
        {
            if (GLFW.glfwJoystickPresent(i))
            {
                String name = GLFW.glfwGetJoystickName(i);
                if (name == null) name = "Unknown Device";
                boolean isGamepad = GLFW.glfwJoystickIsGamepad(i);
                devices.add(new DeviceInfo(i, name, isGamepad));
            }
        }
        return devices;
    }

    /**
     * Get all connected gamepad devices only.
     */
    public static List<DeviceInfo> getGamepads()
    {
        List<DeviceInfo> devices = new ArrayList<>();
        for (int i = 0; i < 16; i++)
        {
            if (GLFW.glfwJoystickPresent(i) && GLFW.glfwJoystickIsGamepad(i))
            {
                String name = GLFW.glfwGetGamepadName(i);
                if (name == null) name = GLFW.glfwGetJoystickName(i);
                if (name == null) name = "Unknown Gamepad";
                devices.add(new DeviceInfo(i, name, true));
            }
        }
        return devices;
    }

    /**
     * Get all connected joystick-only devices (not recognized as gamepads).
     */
    public static List<DeviceInfo> getJoysticksOnly()
    {
        List<DeviceInfo> devices = new ArrayList<>();
        for (int i = 0; i < 16; i++)
        {
            if (GLFW.glfwJoystickPresent(i) && !GLFW.glfwJoystickIsGamepad(i))
            {
                String name = GLFW.glfwGetJoystickName(i);
                if (name == null) name = "Unknown Joystick";
                devices.add(new DeviceInfo(i, name, false));
            }
        }
        return devices;
    }

    /**
     * Get the name of a joystick device by its GLFW index.
     * Returns null if the device is not present.
     */
    public static String getDeviceName(int index)
    {
        if (index < 0 || index > 15 || !GLFW.glfwJoystickPresent(index))
            return null;
        if (GLFW.glfwJoystickIsGamepad(index))
        {
            String name = GLFW.glfwGetGamepadName(index);
            return name != null ? name : GLFW.glfwGetJoystickName(index);
        }
        return GLFW.glfwGetJoystickName(index);
    }

    /**
     * Get the number of connected joystick devices.
     */
    public static int getDeviceCount()
    {
        int count = 0;
        for (int i = 0; i < 16; i++)
        {
            if (GLFW.glfwJoystickPresent(i)) count++;
        }
        return count;
    }
}
