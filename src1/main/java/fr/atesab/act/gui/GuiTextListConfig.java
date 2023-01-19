package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Colors;
import fr.atesab.act.utils.GuiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextListConfig extends GuiScreen {
	public GuiScreen Last;
	public List<String> values;
	public GuiTextField[] tfs;
	public GuiButton next;
	public GuiButton last;
	public GuiButton[] btsDel;
	public GuiButton[] btsAdd;
	public String title;
	public int elms;
	public int page = 0;

	public GuiTextListConfig(GuiScreen last, String[] values, String title) {
		Last = last;
		this.values = ModMain.getArray(values);
		this.title = title;
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			mc.displayGuiScreen(Last);
		} else if (button.id == 1) {
			page -= 1;
			button.enabled = (page != 0);
			next.enabled = (page + 1 <= values.size() / elms);
		} else if (button.id == 2) {
			page += 1;
			last.enabled = (page != 0);
			button.enabled = (page + 1 <= values.size() / elms);
		} else if (button.id == 5) {
			int index = Integer.valueOf(String.valueOf(((GuiValueButton) button).getValue())).intValue();
			values.remove(index);
			defineMenu();
		} else if (button.id == 6) {
			values.add(Integer.valueOf(String.valueOf(((GuiValueButton) button).getValue())).intValue(), "");
			defineMenu();
		}
		super.actionPerformed(button);
	}

	public void defineMenu() {
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height - 21,
				net.minecraft.client.resources.I18n.format("gui.done", new Object[0])));
		buttonList.add(this.last = new GuiButton(1, width / 2 - 121, height - 21, 20, 20, "<="));
		buttonList.add(this.next = new GuiButton(2, width / 2 + 101, height - 21, 20, 20, "=>"));
		last.enabled = (page != 0);
		next.enabled = (page + 1 <= values.size() / elms);
		tfs = new GuiTextField[values.size()];
		btsDel = new GuiButton[values.size()];
		btsAdd = new GuiButton[values.size() + 1];
		int i;
		for (i = 0; i < values.size(); i++) {
			tfs[i] = new GuiTextField(0, fontRenderer, width / 2 - 198, 21 + 21 * i % (elms * 21) + 2, 396, 16);
			tfs[i].setMaxStringLength(Integer.MAX_VALUE);
			tfs[i].setText(values.get(i));
			buttonList.add(btsDel[i] = new GuiValueButton<Integer>(5, width / 2 + 205, 21 + 21 * i % (elms * 21), 20, 20,
					"\u00a7c-", Integer.valueOf(i)));
			buttonList.add(btsAdd[i] = new GuiValueButton<Integer>(6, width / 2 + 226, 21 + 21 * i % (elms * 21), 20, 20,
					"\u00a7a+", Integer.valueOf(i)));
		}
		buttonList.add(btsAdd[i] = new GuiValueButton<Integer>(6, width / 2 - 100, 21 + 21 * i % (elms * 21), 200, 20,
				"\u00a7a+", Integer.valueOf(i)));
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GuiUtils.drawCenterString(fontRenderer, title, width / 2, 1, 20, Colors.GOLD);
		for (int i = page * elms; (i < (page + 1) * elms) && (i < tfs.length); i++) {
			GuiUtils.drawRightString(fontRenderer, i + " : ", tfs[i].x, tfs[i].y, tfs[i].height, Colors.WHITE);
			tfs[i].drawTextBox();
		}
	}

	public void initGui() {
		elms = ((height - 42) / 21);
		defineMenu();
		super.initGui();
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (int i = page * elms; (i < (page + 1) * elms) && (i < values.size()); i++) {
			tfs[i].textboxKeyTyped(typedChar, keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (int i = page * elms; (i < (page + 1) * elms) && (i < values.size()); i++) {
			tfs[i].mouseClicked(mouseX, mouseY, mouseButton);
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void onUpdateValue(String[] value) {
	}

	public void updateScreen() {
		for (int i = page * elms; (i < (page + 1) * elms) && (i < values.size()); i++) {
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
		onUpdateValue(ModMain.getStringList(values));
		super.updateScreen();
	}
}
