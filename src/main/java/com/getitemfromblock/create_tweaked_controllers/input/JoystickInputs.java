package com.getitemfromblock.create_tweaked_controllers.input;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Vector;

import org.lwjgl.glfw.GLFW;

import com.getitemfromblock.create_tweaked_controllers.config.ModClientConfig;

public class JoystickInputs
{
    private static Vector<Boolean> buttons = new Vector<>(0);
    private static Vector<Float> axis = new Vector<>(0);
    private static Vector<Boolean> storedButtons = new Vector<>(0);
    private static Vector<Float> storedAxis = new Vector<>(0);

    protected static int selectedJoystick = -1;
    private static int forcedJoystickIndex = -1;

    /**
     * Force the joystick selection to a specific GLFW index.
     * Set to -1 to return to auto-detection.
     */
    public static void ForceSelectJoystick(int index)
    {
        forcedJoystickIndex = index;
        if (index >= 0 && index < 16 && GLFW.glfwJoystickPresent(index))
        {
            if (selectedJoystick != index)
            {
                selectedJoystick = -1; // Reset so SelectJoystick re-initializes
                SelectJoystick(index);
            }
        }
        else if (index < 0)
        {
            // Return to auto-detection - reset current selection
            selectedJoystick = -1;
        }
    }

    /**
     * Get the currently forced joystick index, or -1 if auto-detecting.
     */
    public static int GetForcedIndex()
    {
        return forcedJoystickIndex;
    }

    /**
     * Cycle to the next available joystick device.
     * Returns the new device index, or -1 if no devices found.
     */
    public static int CycleJoystick()
    {
        int start = selectedJoystick >= 0 ? selectedJoystick + 1 : 0;
        for (int i = 0; i < 16; i++)
        {
            int idx = (start + i) % 16;
            if (GLFW.glfwJoystickPresent(idx))
            {
                ForceSelectJoystick(idx);
                return idx;
            }
        }
        return -1;
    }

    public static void GetControls()
    {
        // Check if config has a forced index
        try
        {
            int configForced = ModClientConfig.FORCE_JOYSTICK_INDEX.get();
            if (configForced != forcedJoystickIndex)
            {
                ForceSelectJoystick(configForced);
            }
        }
        catch (Exception e)
        {
            // Config not yet loaded, ignore
        }

        if (selectedJoystick < 0)
        {
            // If a forced index is set, only try that device
            if (forcedJoystickIndex >= 0)
            {
                if (GLFW.glfwJoystickPresent(forcedJoystickIndex))
                {
                    SelectJoystick(forcedJoystickIndex);
                }
            }
            else
            {
                // Original auto-detection logic
                int uniqueJoystickID = -1;
                for (int i = 0; i < 16 && selectedJoystick < 0; i++)
                {
                    if (!GLFW.glfwJoystickPresent(i)) continue;
                    if (uniqueJoystickID == -1)
                    {
                        uniqueJoystickID = i;
                    }
                    else if (uniqueJoystickID >= 0)
                    {
                        uniqueJoystickID = -2;
                    }
                    ByteBuffer res = GLFW.glfwGetJoystickButtons(i);
                    if (res == null) continue;
                    for (int b = 0; b < res.limit(); b++)
                    {
                        if (res.get(b) != GLFW.GLFW_PRESS) continue;
                        SelectJoystick(i);
                        break;
                    }
                }
                if (selectedJoystick < 0 && uniqueJoystickID >= 0)
                {
                    SelectJoystick(uniqueJoystickID);
                }
            }
        }
        if (selectedJoystick >= 0 && GLFW.glfwJoystickPresent(selectedJoystick))
        {
            ByteBuffer b = GLFW.glfwGetJoystickButtons(selectedJoystick);
            FloatBuffer a = GLFW.glfwGetJoystickAxes(selectedJoystick);
            if (b == null || buttons.size() != b.limit()
                || a == null || axis.size() != a.limit())
            {
                Empty();
                selectedJoystick = -1;
            }
            else
            {
                Fill(b, a);
            }
        }
        else
        {
            Empty();
            selectedJoystick = -1;
        }
    }

    private static void SelectJoystick(int id)
    {
        selectedJoystick = id;
        ByteBuffer b = GLFW.glfwGetJoystickButtons(selectedJoystick);
        FloatBuffer a = GLFW.glfwGetJoystickAxes(selectedJoystick);
        if (b == null || a == null)
        {
            Empty();
            selectedJoystick = -1;
            return;
        }
        buttons = new Vector<>(b.limit());
        storedButtons = new Vector<>(b.limit());
        for (int i = 0; i < b.limit(); i++)
        {
            buttons.add(false);
            storedButtons.add(false);
        }
        axis = new Vector<>(a.limit());
        storedAxis = new Vector<>(a.limit());
        for (int i = 0; i < a.limit(); i++)
        {
            axis.add(0.0f);
            storedAxis.add(0.0f);
        }
    }

    public static int GetButtonCount()
    {
        return HasJoystick() ? buttons.size() : 0;
    }

    public static int GetAxisCount()
    {
        return HasJoystick() ? axis.size() : 0;
    }

    public static int GetJoystickIndex()
    {
        return selectedJoystick;
    }

    public static boolean HasJoystick()
    {
        return selectedJoystick >= 0;
    }

    public static void SearchGamepad()
    {
        selectedJoystick = -1;
    }

    public static boolean GetButton(int button)
    {
        return button >= GetButtonCount() ? false : JoystickInputs.buttons.get(button);
    }

    public static float GetAxis(int axis)
    {
        return axis >= GetAxisCount() ? 0.0f : JoystickInputs.axis.get(axis);
    }

    public static void Empty()
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            buttons.set(i, false);
        }
        for (int i = 0; i < axis.size(); i++)
        {
            axis.set(i, 0.0f);
        }
    }

    public static void Fill(ByteBuffer b, FloatBuffer a)
    {
        for (int i = 0; i < b.limit(); i++)
        {
            buttons.set(i, b.get(i) == GLFW.GLFW_PRESS);
        }
        for (int i = 0; i < a.limit(); i++)
        {
            axis.set(i, a.get(i));
        }
    }

    public static void StoreAxisValues()
    {
        for (int i = 0; i < axis.size(); i++)
        {
            storedAxis.set(i, axis.get(i));
        }
    }

    public static void StoreButtonsValues()
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            storedButtons.set(i, buttons.get(i));
        }
    }

    public static int GetFirstButton()
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            if (buttons.get(i) != storedButtons.get(i)) return i;
        }
        return -1;
    }

    public static int GetFirstAxis()
    {
        for (int i = 0; i < axis.size(); i++)
        {
            if (Math.abs(axis.get(i) - storedAxis.get(i)) > 0.75f) return i;
        }
        return -1;
    }

    public static float GetStoredAxis(int index)
    {
        return storedAxis.get(index);
    }

    public static boolean GetStoredButton(int index)
    {
        return storedButtons.get(index);
    }
}
