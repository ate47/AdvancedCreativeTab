package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiStringModifier extends GuiModifier<String> {
	private TextFieldWidget field;
	private String value;

	public GuiStringModifier(Screen parent, ITextComponent name, String value, Consumer<String> setter) {
		super(parent, name, setter);
		this.value = value;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		field.render(matrixStack, mouseX, mouseY, partialTicks);
		GuiUtils.drawRightString(font, I18n.get("gui.act.text") + " : ", field.x, field.y, Color.ORANGE.getRGB(),
				field.getHeight());
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void init() {
		field = new TextFieldWidget(font, width / 2 - 99, height / 2 - 20, 198, 18, new StringTextComponent(""));
		field.setMaxLength(Integer.MAX_VALUE);
		field.setValue(value.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
		field.setFocus(true);
		field.setCanLoseFocus(false);
		addButton(new Button(width / 2 - 100, height / 2, 200, 20, new TranslationTextComponent("gui.done"), b -> {
			set(value = field.getValue().replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
			getMinecraft().setScreen(parent);
		}));
		addButton(new Button(width / 2 - 100, height / 2 + 21, 200, 20, new TranslationTextComponent("gui.act.cancel"),
				b -> getMinecraft().setScreen(parent)));
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