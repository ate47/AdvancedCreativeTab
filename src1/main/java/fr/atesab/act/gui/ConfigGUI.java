package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGUI extends GuiConfig {
	//TODO
	public ConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(ModMain.configFile.getCategory("general")).getChildElements(), ModMain.MOD_ID, false,
				false, I18n.format("gui.act.guifactorytitle", new Object[0]));
	}
}
