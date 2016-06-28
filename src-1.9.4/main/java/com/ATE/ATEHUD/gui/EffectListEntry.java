package com.ATE.ATEHUD.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

public class EffectListEntry implements GuiListExtended.IGuiListEntry {
	GuiPotionFactory potionGUI;
	Minecraft mc;
	public EffectListEntry(GuiPotionFactory guiPotionFactoryIn){
		this.potionGUI = guiPotionFactoryIn;
        this.mc = Minecraft.getMinecraft();
	}
	public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
		
		
	}

	public void drawEntry(int slotIndex, int x, int y, int listWidth,
			int slotHeight, int mouseX, int mouseY, boolean isSelected) {
		
	}

	public boolean mousePressed(int slotIndex, int mouseX, int mouseY,
			int mouseEvent, int relativeX, int relativeY) {
		
		return false;
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent,
			int relativeX, int relativeY) {
		
		
	}
    protected boolean canMoveRight()
    {
        return false;
    }

    protected boolean canMoveLeft()
    {
        return false;
    }

}
