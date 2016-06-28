package com.ATE.ATEHUD.gui;

import com.ATE.ATEHUD.ModMain;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGUI extends GuiConfig {
  public ConfigGUI(GuiScreen parent) {
    super(parent,
        new ConfigElement(ModMain.configFile.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
        ModMain.ModId, false, false, I18n.format("gui.act.guifactorytitle"));
  }
}