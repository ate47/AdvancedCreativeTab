package com.ATE.ATEHUD.gui;

import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;

public class GuiTest extends GuiScreen {
	GuiScrollBar sb;
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		sb.drawScrollBar(mc, mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	public void initGui() {
		sb=new GuiScrollBar(0, 0, 0, 200, 20);
		super.initGui();
	}
	public GuiTest(){}
}
