package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.io.IOException;
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

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0)
			mc.displayGuiScreen(parent);
		else if (button.id == 1) {
			set(name = field.getText().replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
			mc.displayGuiScreen(parent);
		}
		super.actionPerformed(button);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		field.drawTextBox();
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.text") + " : ", field.x, field.y,
				Color.ORANGE.getRGB(), field.height);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void initGui() {
		field = new GuiTextField(0, fontRenderer, width / 2 - 99, height / 2 - 20, 198, 18);
		field.setMaxStringLength(Integer.MAX_VALUE);
		field.setText(name.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
		field.setFocused(true);
		field.setCanLoseFocus(false);
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 2, I18n.format("gui.done")));
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + 21, I18n.format("gui.act.cancel")));
		super.initGui();
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		field.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		field.mouseClicked(mouseX, mouseY, mouseButton);
		if(GuiUtils.isHover(field, mouseX, mouseY) && mouseButton == 1)
			field.setText("");
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void updateScreen() {
		name = field.getText();
		field.updateCursorCounter();
		super.updateScreen();
	}
}