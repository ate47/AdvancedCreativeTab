package fr.atesab.act.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiBooleanButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiConfig extends GuiACT {

	public GuiConfig(Screen parent) {
		super(parent, new TranslationTextComponent("gui.act.config"));
	}

	@Override
	protected void init() {
		addButton(new GuiBooleanButton(width / 2 - 100, height / 2 - 24, 200, 20,
				new TranslationTextComponent("gui.act.disableToolTip"), ACTMod::setDoesDisableToolTip,
				ACTMod::doesDisableToolTip));
		addButton(new Button(width / 2 - 100, height / 2, 200, 20, new TranslationTextComponent("gui.done"), b -> {
			ACTMod.saveConfigs();
			mc.setScreen(parent);
		}));
		super.init();
	}

	@Override
	public void render(MatrixStack MatrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(MatrixStack);
		super.render(MatrixStack, mouseX, mouseY, partialTicks);
	}

}
