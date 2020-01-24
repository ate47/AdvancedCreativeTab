package fr.atesab.act.gui;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiBooleanButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiConfig extends GuiACT {

	public GuiConfig(GuiScreen parent) {
		super(parent);
	}

	@Override
	protected void initGui() {
		addButton(new GuiBooleanButton(0, width / 2 - 100, height / 2 - 24, 200, 20, I18n.format("gui.act.disableToolTip"),
				ACTMod::setDoesDisableToolTip, ACTMod::doesDisableToolTip));
		addButton(new GuiButton(0, width / 2 - 100, height / 2, 200, 20, I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				ACTMod.saveConfigs();
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		super.initGui();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
	}

}
