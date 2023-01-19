package fr.atesab.act.gui;

import net.minecraft.client.Minecraft;

public class EffectListEntry implements net.minecraft.client.gui.GuiListExtended.IGuiListEntry {
	GuiPotionFactory potionGUI;
	Minecraft mc;

	public EffectListEntry(GuiPotionFactory guiPotionFactoryIn) {
		this.potionGUI = guiPotionFactoryIn;
		this.mc = Minecraft.getMinecraft();
	}

	protected boolean canMoveLeft() {
		return false;
	}

	protected boolean canMoveRight() {
		return false;
	}

	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
			boolean isSelected) {

	}

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
			boolean isSelected, float partialTicks) {

	}

	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {

		return false;
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

	}

	public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {

	}

	@Override
	public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {

	}
}
