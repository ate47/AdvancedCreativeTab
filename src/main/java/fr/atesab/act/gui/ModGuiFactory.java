package fr.atesab.act.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class ModGuiFactory implements IModGuiFactory {
	public ModGuiFactory() {
	}
	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiMenu.class;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
}