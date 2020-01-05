package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.function.Consumer;

import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiStringModifier extends GuiModifier<String> {
	private GuiTextField field;
	private String name;

	public GuiStringModifier(GuiScreen parent, String name, Consumer<String> setter) {
		super(parent, setter);
		this.name = name;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		field.drawTextField(mouseX, mouseY, partialTicks);
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.text") + " : ", field.x, field.y,
				Color.ORANGE.getRGB(), field.height);
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui() {
		field = new GuiTextField(0, fontRenderer, width / 2 - 99, height / 2 - 20, 198, 18);
		field.setMaxStringLength(Integer.MAX_VALUE);
		field.setText(name.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
		field.setFocused(true);
		field.setCanLoseFocus(false);
		addButton(new GuiButton(1, width / 2 - 100, height / 2, I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				set(name = field.getText().replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(0, width / 2 - 100, height / 2 + 21, I18n.format("gui.act.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		super.initGui();
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