package fr.atesab.act.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiBooleanButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;

public class GuiConfig extends GuiACT {

	public GuiConfig(Screen parent) {
		super(parent, new TranslatableComponent("gui.act.config"));
	}

	@Override
	protected void init() {
		addWidget(new GuiBooleanButton(width / 2 - 100, height / 2 - 24, 200, 20,
				new TranslatableComponent("gui.act.disableToolTip"), ACTMod::setDoesDisableToolTip,
				ACTMod::doesDisableToolTip));
		addWidget(new Button(width / 2 - 100, height / 2, 200, 20, new TranslatableComponent("gui.done"), b -> {
			ACTMod.saveConfigs();
			mc.setScreen(parent);
		}));
		super.init();
	}

	@Override
	public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(PoseStack);
		super.render(PoseStack, mouseX, mouseY, partialTicks);
	}

}
