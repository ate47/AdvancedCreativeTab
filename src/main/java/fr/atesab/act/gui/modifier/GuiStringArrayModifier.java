package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import fr.atesab.act.gui.GuiValueButton;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiStringArrayModifier extends GuiModifier<String[]> {
	private ArrayList<String> values;
	private GuiTextField[] tfs;
	private GuiButton next, last;
	private GuiValueButton<Integer>[] btsDel, btsAdd;
	private int elms;
	private int page = 0;

	public GuiStringArrayModifier(GuiScreen parent, String[] values, Consumer<String[]> setter) {
		super(parent, setter);
		this.values = new ArrayList<String>();
		for (String v : values)
			this.values.add(v.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			String[] result = new String[values.size()];
			for (int i = 0; i < result.length; i++)
				result[i] = values.get(i).replaceAll("&", String.valueOf(ChatUtils.MODIFIER));
			set(result);
			mc.displayGuiScreen(parent);
		} else if (button.id == 7)
			mc.displayGuiScreen(parent);
		else if (button.id == 1) {
			page--;
			button.enabled = page != 0;
			next.enabled = page + 1 <= values.size() / elms;
		} else if (button.id == 2) {
			page++;
			last.enabled = page != 0;
			button.enabled = page + 1 <= values.size() / elms;
		} else if (button.id == 5) {
			values.remove(((GuiValueButton<Integer>) button).getValue().intValue());
			defineMenu();
		} else if (button.id == 6) {
			values.add(((GuiValueButton<Integer>) button).getValue().intValue(), "");
			defineMenu();
		}
		super.actionPerformed(button);
	}

	private void defineMenu() {
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height - 21, 100, 20, I18n.format("gui.done")));
		buttonList.add(new GuiButton(7, width / 2 + 1, height - 21, 99, 20, I18n.format("gui.act.cancel")));
		buttonList.add(last = new GuiButton(1, width / 2 - 121, height - 21, 20, 20, "<-"));
		buttonList.add(next = new GuiButton(2, width / 2 + 101, height - 21, 20, 20, "->"));
		last.enabled = page != 0;
		next.enabled = page + 1 <= values.size() / elms;
		tfs = new GuiTextField[values.size()];
		btsDel = new GuiValueButton[values.size()];
		btsAdd = new GuiValueButton[values.size() + 1];
		int i;
		for (i = 0; i < values.size(); i++) {
			tfs[i] = new GuiTextField(0, fontRenderer, width / 2 - 178, 21 + 21 * i % (elms * 21) + 2, 340, 16);
			tfs[i].setMaxStringLength(Integer.MAX_VALUE);
			tfs[i].setText(values.get(i));
			buttonList.add(btsDel[i] = new GuiValueButton<Integer>(5, width / 2 + 165, 21 + 21 * i % (elms * 21), 20,
					20, TextFormatting.RED + "-", i));
			buttonList.add(btsAdd[i] = new GuiValueButton<Integer>(6, width / 2 + 187, 21 + 21 * i % (elms * 21), 20,
					20, TextFormatting.GREEN + "+", i));
		}
		buttonList.add(btsAdd[i] = new GuiValueButton<Integer>(6, width / 2 - 100, 21 + 21 * i % (elms * 21), 200, 20,
				TextFormatting.GREEN + "+", i));

	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		for (int i = page * elms; i < (page + 1) * elms && i < tfs.length; i++) {
			GuiTextField tf = tfs[i];
			GuiUtils.drawRightString(fontRenderer, i + " : ", tf.x, tf.y, Color.WHITE.getRGB(), tf.height);
			tf.drawTextBox();
		}
	}

	public void initGui() {
		elms = (height - 42) / 21;
		defineMenu();
		super.initGui();
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
			tfs[i].textboxKeyTyped(typedChar, keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
			tfs[i].mouseClicked(mouseX, mouseY, mouseButton);
			if (mouseButton == 1 && GuiUtils.isHover(tfs[i], mouseX, mouseY))
				tfs[i].setText("");
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void updateScreen() {
		for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
			tfs[i].updateCursorCounter();
			values.set(i, tfs[i].getText());
		}
		for (int i = 0; i < btsAdd.length; i++)
			if (i < (page + 1) * elms && i >= page * elms) {
				if (btsAdd[i] != null)
					btsAdd[i].visible = true;
				if (i < btsDel.length && btsDel[i] != null)
					btsDel[i].visible = true;
			} else {
				if (btsAdd[i] != null)
					btsAdd[i].visible = false;
				if (i < btsDel.length && btsDel[i] != null)
					btsDel[i].visible = false;
			}
		super.updateScreen();
	}
}
