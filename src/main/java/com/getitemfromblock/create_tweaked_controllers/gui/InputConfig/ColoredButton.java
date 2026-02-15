package com.getitemfromblock.create_tweaked_controllers.gui.InputConfig;

import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ColoredButton extends Button
{
    private static final ResourceLocation WIDGETS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/widgets.png");
    public Vector3f color = new Vector3f(1, 1, 1);

    public ColoredButton(int p_93721_, int p_93722_, int p_93723_, int p_93724_, Component p_93725_, OnPress p_93726_, Vector3f color)
    {
        super(p_93721_, p_93722_, p_93723_, p_93724_, p_93725_, p_93726_, DEFAULT_NARRATION);
        this.color = color;
    }

    private int getTextureYReimplemented()
    {
        int i = 1;
        if (!this.active)
        {
            i = 0;
        }
        else if (this.isHoveredOrFocused())
        {
            i = 2;
        }
        return 46 + i * 20;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int x, int y, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        graphics.setColor(color.x, color.y, color.z, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        int texY = this.getTextureYReimplemented();
        int halfW = this.getWidth() / 2;
        graphics.blit(WIDGETS_LOCATION, this.getX(), this.getY(), 0, texY, halfW, this.getHeight(), 256, 256);
        graphics.blit(WIDGETS_LOCATION, this.getX() + halfW, this.getY(), 200 - halfW, texY, halfW, this.getHeight(), 256, 256);
        graphics.setColor(color.x, color.y, color.z, 1.0F);
        int i = getFGColor();
        this.renderString(graphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }
    
}
