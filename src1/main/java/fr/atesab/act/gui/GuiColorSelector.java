package fr.atesab.act.gui;

import java.io.IOException;
import java.util.List;

import fr.atesab.act.superclass.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiColorSelector extends GuiScreen {
	private int color = Colors.WHITE;
	private int Index = 0;
	private boolean FadeColor = false;
	private GuiScreen Last;
	private GuiExplosionSelector Editor;

	private int nextGuiId = 15;

	GuiButton done;

	GuiTextField red;

	GuiTextField green;

	GuiTextField blue;

	private String tred;

	private String tgreen;

	private String tblue;
	public GuiColorSelector(GuiExplosionSelector editor, GuiScreen lastGui, boolean fadeColor, int index) {
		if (!fadeColor) {
			if ((editor.getColorSize() > index) && (index >= 0))
				Index = index;
		} else if ((editor.getFadeColorSize() > index) && (index >= 0))
			Index = index;
		color = Colors.WHITE;
		FadeColor = fadeColor;
		Last = lastGui;
		Editor = editor;
	}

	public GuiColorSelector(GuiExplosionSelector editor, GuiScreen lastGui, boolean fadeColor, int index, int value) {
		if (!fadeColor) {
			if ((editor.getColorSize() > index) && (index >= 0))
				Index = index;
		} else if ((editor.getFadeColorSize() > index) && (index >= 0))
			Index = index;
		color = value;
		FadeColor = fadeColor;
		Last = lastGui;
		Editor = editor;
	}

	public void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			color = 1973019;
			break;
		case 1:
			color = 11743532;
			break;
		case 2:
			color = 3887386;
			break;
		case 3:
			color = 5320730;
			break;
		case 4:
			color = 2437522;
			break;
		case 5:
			color = 8073150;
			break;
		case 6:
			color = 2651799;
			break;
		case 7:
			color = 11250603;
			break;
		case 8:
			color = 4408131;
			break;
		case 9:
			color = 14188952;
			break;
		case 10:
			color = 4312372;
			break;
		case 11:
			color = 14602026;
			break;
		case 12:
			color = 6719955;
			break;
		case 13:
			color = 12801229;
			break;
		case 14:
			color = 15435844;
			break;
		case 15:
			color = 15790320;
			break;
		default:
			if (FadeColor) {
				Editor.setFadeColor(Index, color);
			} else {
				Editor.setColor(Index, color);
			}
			mc.displayGuiScreen(Last);
		}

		setTFColor();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		red.drawTextBox();
		green.drawTextBox();
		blue.drawTextBox();
		if (red.getText() != tred) {
			setFTColor();
			tred = red.getText();
		}
		if (green.getText() != tgreen) {
			setFTColor();
			tgreen = green.getText();
		}
		if (blue.getText() != tblue) {
			setFTColor();
			tblue = blue.getText();
		}
		buttonList.remove(done);
		if (color == 0)
			done.packedFGColour = 1;
		else
			done.packedFGColour = color;
		buttonList.add(done);

		tred = red.getText();
		tgreen = green.getText();
		tblue = blue.getText();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	public int getColor() {
		return color;
	}
	public GuiExplosionSelector getEditor() {
		return Editor;
	}
	public GuiScreen getLastGui() {
		return Last;
	}

	private int getNextGuiId() {
		nextGuiId += 1;
		return nextGuiId;
	}

	public void initGui() {
		fontRenderer.drawSplitString(I18n.format("item.fireworksCharge.red", new Object[0]), width / 2 - 150,
				height / 2 - 64 - fontRenderer.FONT_HEIGHT, 100, Colors.WHITE);
		fontRenderer.drawSplitString(I18n.format("item.fireworksCharge.green", new Object[0]), width / 2 - 50,
				height / 2 - 64 - fontRenderer.FONT_HEIGHT, 100, Colors.WHITE);
		fontRenderer.drawSplitString(I18n.format("item.fireworksCharge.blue", new Object[0]), width / 2 + 50,
				height / 2 - 64 - fontRenderer.FONT_HEIGHT, 100, Colors.WHITE);
		red = new GuiTextField(17, fontRenderer, width / 2 - 150, height / 2 - 63, 99, 20);
		green = new GuiTextField(18, fontRenderer, width / 2 - 50, height / 2 - 63, 99, 20);
		blue = new GuiTextField(19, fontRenderer, width / 2 + 50, height / 2 - 63, 99, 20);
		setTFColor();

		GuiButton pre = new GuiButton(0, width / 2 - 150, height / 2 - 42, 70, 20,
				I18n.format("item.fireworksCharge.black", new Object[0]));
		pre.packedFGColour = 1973019;
		buttonList.add(pre);
		pre = new GuiButton(1, width / 2 - 75, height / 2 - 42, 70, 20,
				I18n.format("item.fireworksCharge.red", new Object[0]));
		pre.packedFGColour = 11743532;
		buttonList.add(pre);
		pre = new GuiButton(2, width / 2 + 5, height / 2 - 42, 70, 20,
				I18n.format("item.fireworksCharge.green", new Object[0]));
		pre.packedFGColour = 3887386;
		buttonList.add(pre);
		pre = new GuiButton(3, width / 2 + 80, height / 2 - 42, 70, 20,
				I18n.format("item.fireworksCharge.brown", new Object[0]));
		pre.packedFGColour = 5320730;
		buttonList.add(pre);

		pre = new GuiButton(4, width / 2 - 150, height / 2 - 21, 70, 20,
				I18n.format("item.fireworksCharge.blue", new Object[0]));
		pre.packedFGColour = 2437522;
		buttonList.add(pre);
		pre = new GuiButton(5, width / 2 - 75, height / 2 - 21, 70, 20,
				I18n.format("item.fireworksCharge.purple", new Object[0]));
		pre.packedFGColour = 8073150;
		buttonList.add(pre);
		pre = new GuiButton(6, width / 2 + 5, height / 2 - 21, 70, 20,
				I18n.format("item.fireworksCharge.cyan", new Object[0]));
		pre.packedFGColour = 2651799;
		buttonList.add(pre);
		pre = new GuiButton(7, width / 2 + 80, height / 2 - 21, 70, 20,
				I18n.format("item.fireworksCharge.silver", new Object[0]));
		pre.packedFGColour = 11250603;
		buttonList.add(pre);

		pre = new GuiButton(8, width / 2 - 150, height / 2, 70, 20,
				I18n.format("item.fireworksCharge.gray", new Object[0]));
		pre.packedFGColour = 4408131;
		buttonList.add(pre);
		pre = new GuiButton(9, width / 2 - 75, height / 2, 70, 20,
				I18n.format("item.fireworksCharge.pink", new Object[0]));
		pre.packedFGColour = 14188952;
		buttonList.add(pre);
		pre = new GuiButton(10, width / 2 + 5, height / 2, 70, 20,
				I18n.format("item.fireworksCharge.lime", new Object[0]));
		pre.packedFGColour = 4312372;
		buttonList.add(pre);
		pre = new GuiButton(11, width / 2 + 80, height / 2, 70, 20,
				I18n.format("item.fireworksCharge.yellow", new Object[0]));
		pre.packedFGColour = 14602026;
		buttonList.add(pre);

		pre = new GuiButton(12, width / 2 - 150, height / 2 + 21, 70, 20,
				I18n.format("item.fireworksCharge.lightBlue", new Object[0]));
		pre.packedFGColour = 6719955;
		buttonList.add(pre);
		pre = new GuiButton(13, width / 2 - 75, height / 2 + 21, 70, 20,
				I18n.format("item.fireworksCharge.magenta", new Object[0]));
		pre.packedFGColour = 12801229;
		buttonList.add(pre);
		pre = new GuiButton(14, width / 2 + 5, height / 2 + 21, 70, 20,
				I18n.format("item.fireworksCharge.orange", new Object[0]));
		pre.packedFGColour = 15435844;
		buttonList.add(pre);
		pre = new GuiButton(15, width / 2 + 80, height / 2 + 21, 70, 20,
				I18n.format("item.fireworksCharge.white", new Object[0]));
		pre.packedFGColour = 15790320;
		buttonList.add(pre);

		done = new GuiButton(16, width / 2 - 150, height / 2 + 42, 300, 20, I18n.format("gui.done", new Object[0]));
		done.packedFGColour = color;
		buttonList.add(done);
		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		red.textboxKeyTyped(par1, par2);
		green.textboxKeyTyped(par1, par2);
		blue.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		red.mouseClicked(x, y, btn);
		green.mouseClicked(x, y, btn);
		blue.mouseClicked(x, y, btn);
	}

	public void setFTColor() {
		int var1 = 0;
		int var2 = 0;
		int var3 = 0;
		try {
			var1 = Float.valueOf(red.getText()).intValue();
		} catch (Exception localException) {
		}
		try {
			var2 = Float.valueOf(green.getText()).intValue();
		} catch (Exception localException1) {
		}
		try {
			var3 = Float.valueOf(blue.getText()).intValue();
		} catch (Exception localException2) {
		}
		color = Colors.getColorWithRGB(var1, var2, var3);
		done.packedFGColour = color;
		buttonList.remove(done);
		if (color == 0)
			done.packedFGColour = 1;
		else
			done.packedFGColour = color;
	}

	public void setTFColor() {
		int cred = color >> 16 & 0xFF;
		int cgreen = color >> 8 & 0xFF;
		int cblue = color & 0xFF;
		red.setText(Integer.toString(cred));
		green.setText(Integer.toString(cgreen));
		blue.setText(Integer.toString(cblue));
		color = Colors.getColorWithRGB(cred, cgreen, cblue);
	}

	public void updateScreen() {
		red.updateCursorCounter();
		green.updateCursorCounter();
		blue.updateCursorCounter();
	}
}
