package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.function.Consumer;

import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

public class GuiStringModifier extends GuiModifier<String> {
	private TextFieldWidget field;
	private String name;

	public GuiStringModifier(Screen parent, String name, Consumer<String> setter) {
		super(parent, setter);
		this.name = name;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		field.render(mouseX, mouseY, partialTicks);
		GuiUtils.drawRightString(font, I18n.format("gui.act.text") + " : ", field.x, field.y, Color.ORANGE.getRGB(),
				field.getHeight());
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public void init() {
		field = new TextFieldWidget(font, width / 2 - 99, height / 2 - 20, 198, 18, "");
		field.setMaxStringLength(Integer.MAX_VALUE);
		field.setText(name.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
		field.setFocused2(true);
		field.setCanLoseFocus(false);
		addButton(new Button(width / 2 - 100, height / 2, 200, 20, I18n.format("gui.done"), b -> {
			set(name = field.getText().replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
			getMinecraft().displayGuiScreen(parent);
		}));
		addButton(new Button(width / 2 - 100, height / 2 + 21, 200, 20, I18n.format("gui.act.cancel"),
				b -> getMinecraft().displayGuiScreen(parent)));
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
			field.setText("");
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		name = field.getText();
		field.tick();
		super.tick();
	}
}