package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class GuiStringModifier extends GuiModifier<String> {
	private EditBox field;
	private String value;

	public GuiStringModifier(Screen parent, Component name, String value, Consumer<String> setter) {
		super(parent, name, setter);
		this.value = value;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		field.render(matrixStack, mouseX, mouseY, partialTicks);
		GuiUtils.drawRightString(font, I18n.get("gui.act.text") + " : ", field.x, field.y, Color.ORANGE.getRGB(),
				field.getHeight());
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void init() {
		field = new EditBox(font, width / 2 - 99, height / 2 - 20, 198, 18, new TextComponent(""));
		field.setMaxLength(Integer.MAX_VALUE);
		field.setValue(value.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
		field.setFocus(true);
		field.setCanLoseFocus(false);
		addRenderableWidget(
				new Button(width / 2 - 100, height / 2, 200, 20, new TranslatableComponent("gui.done"), b -> {
					set(value = field.getValue().replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
					getMinecraft().setScreen(parent);
				}));
		addRenderableWidget(new Button(width / 2 - 100, height / 2 + 21, 200, 20,
				new TranslatableComponent("gui.act.cancel"), b -> getMinecraft().setScreen(parent)));
		super.init();
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		return field.charTyped(key, modifiers) || super.charTyped(key, modifiers);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		return field.keyPressed(key, scanCode, modifiers) || super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		field.mouseClicked(mouseX, mouseY, mouseButton);
		if (GuiUtils.isHover(field, (int) mouseX, (int) mouseY) && mouseButton == 1)
			field.setValue("");
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		value = field.getValue();
		field.tick();
		super.tick();
	}
}