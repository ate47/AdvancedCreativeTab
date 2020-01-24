package fr.atesab.act.gui;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiBooleanButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

public class GuiConfig extends GuiACT {

	public GuiConfig(Screen parent) {
		super(parent, "gui.act.config");
	}

	@Override
	protected void init() {
		addButton(new GuiBooleanButton(width / 2 - 100, height / 2 - 24, 200, 20, I18n.format("gui.act.disableToolTip"),
				ACTMod::setDoesDisableToolTip, ACTMod::doesDisableToolTip));
		addButton(new Button(width / 2 - 100, height / 2, 200, 20, I18n.format("gui.done"), b -> {
				ACTMod.saveConfigs();
				mc.displayGuiScreen(parent);
		}));
		super.init();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
	}

}
