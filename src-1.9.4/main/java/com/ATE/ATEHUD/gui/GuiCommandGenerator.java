package com.ATE.ATEHUD.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;

public class GuiCommandGenerator extends GuiScreen {
	public GuiScreen Last;
	public GuiCommandGenerator(GuiScreen last){
		Last=last;
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		World wld=mc.theWorld;
		EntityVillager villager=new EntityVillager(wld);
		GuiInventory.drawEntityOnScreen(width/2-25, height/2-25, 50, width/2, height/2, villager);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
