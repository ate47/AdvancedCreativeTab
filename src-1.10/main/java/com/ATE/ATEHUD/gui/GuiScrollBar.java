package com.ATE.ATEHUD.gui;

import com.ATE.ATEHUD.ModMain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiScrollBar extends Gui{
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	public int ValueMin;
	public int ValueMax;
	public int Value;
	public int height;
	public int width;
	public int xPosition;
	public int yPosition;
	public boolean visible;
    public int id;
    public boolean enabled;
    protected boolean hovered;
    public boolean dragging;
    public GuiScrollBar(int id,int xPosition,int yPosition,int width,int height){
    	this(id, xPosition, yPosition, width, height, 0, 100,0);
    }
    public GuiScrollBar(int id,int xPosition,int yPosition,int width,int height,int value){
    	this(id, xPosition, yPosition, width, height, 0, 100,value);
    }
    public GuiScrollBar(int id,int xPosition,int yPosition,int width,int height,int min,int max){
    	this(id, xPosition, yPosition, width, height, min, max, min);
    }
    public GuiScrollBar(int id,int xPosition,int yPosition,int width,int height,int min,int max,int value){
		this.id=id;
		this.xPosition=xPosition;
		this.yPosition=yPosition;
		this.height=height;
		this.width=width;
		this.ValueMin=min;
		this.ValueMax=max;
	}
    public void mouseReleased(int mouseX, int mouseY)
    {
        this.dragging = false;
    }
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height)
        {
            this.Value = (int)(mouseX - (this.xPosition + 4)) / (int)(this.width - 8);
            this.Value = MathHelper.clamp_int(this.Value, ValueMin, ValueMax);
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
        
    }
    public void drawScrollBar(Minecraft mc, int mouseX, int mouseY){
        if (this.visible) {
            if (this.dragging)
            {
                this.Value = (int)(mouseX - (this.xPosition + 4)) / (int)(this.width - 8);
                this.Value = MathHelper.clamp_int(this.Value, ValueMin, ValueMax);
            }
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.Value * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.Value * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        	
        }    	
    }
}
