package fr.atesab.act.gui;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiFactory implements net.minecraftforge.fml.client.IModGuiFactory {
	public GuiFactory() {
	}

	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new ConfigGUI(parentScreen);
	}

	public boolean hasConfigGui() {
		return true;
	}

	public void initialize(Minecraft minecraftInstance) {
	}

	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigGUI.class;
	}

	public Set<net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}
}
