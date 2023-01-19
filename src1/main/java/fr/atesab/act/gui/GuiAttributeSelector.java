package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Attribute;
import fr.atesab.act.superclass.Colors;
import fr.atesab.act.utils.GuiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiAttributeSelector extends GuiScreen {
	public static int buttonYESColor = Colors.LimeGreen;
	public static int buttonNOColor = Colors.RED;
	private boolean isAdvanced = false;
	private GuiItemFactory Last;
	private GuiButton bdone;
	private GuiButton bcancel;
	private GuiButton bNextPage;
	private GuiButton bLastPage;
	private int preButton = 10;
	private int elementSize = 80;
	private Object[] list = new Object[0];
	private GuiTextField[] amountList = new GuiTextField[0];
	private GuiTextField[] UUIDLeast = new GuiTextField[0];
	private GuiTextField[] UUIDMost = new GuiTextField[0];
	private GuiButton[] operation0List = new GuiButton[0];
	private GuiButton[] operation1List = new GuiButton[0];
	private List<GuiButton> operation0Button = new ArrayList<GuiButton>();
	private List<GuiButton> operation1Button = new ArrayList<GuiButton>();
	private int page = 0;
	private int maxAttribute;

	public GuiAttributeSelector(GuiItemFactory last) {
		isAdvanced = ModMain.AdvancedModActived;
		Last = last;
	}

	public void actionPerformed(GuiButton button) {
		for (int i = 0; i < operation0List.length; i++) {
			if (operation0List[i] == button) {
				operation0List[i].packedFGColour = buttonYESColor;
				operation1List[i].packedFGColour = buttonNOColor;
			}
			if (operation1List[i] == button) {
				operation0List[i].packedFGColour = buttonNOColor;
				operation1List[i].packedFGColour = buttonYESColor;
			}
		}
		if (button == bdone) {
			Attribute[] attriList = (Attribute[]) ModMain.copyOf(new Attribute[0], list.length, Attribute[].class);
			for (int i = 0; i < attriList.length; i++)
				attriList[i] = null;
			int j = 0;
			for (int i = page * maxAttribute; (i < (page + 1) * maxAttribute) && (i < list.length); i++) {
				int Amount = 0;
				if (!amountList[i].getText().isEmpty())
					try {
						Amount = Integer.valueOf(amountList[i].getText()).intValue();
					} catch (Exception localException) {
					}
				if (Amount != 0) {
					int Operation = 0;
					if (operation1List[i].packedFGColour == buttonYESColor) {
						Operation = 1;
					}
					int UUIDLeast = Integer.valueOf((int) (1000000.0D * Math.random())).intValue();
					if (!this.UUIDLeast[i].getText().isEmpty())
						try {
							UUIDLeast = Integer.valueOf(this.UUIDLeast[i].getText()).intValue();
						} catch (Exception localException1) {
						}
					int UUIDMost = Integer.valueOf((int) (1000000.0D * Math.random())).intValue();
					if (!this.UUIDMost[i].getText().isEmpty())
						try {
							UUIDMost = Integer.valueOf(this.UUIDMost[i].getText()).intValue();
						} catch (Exception localException2) {
						}
					String[] strl = (String[]) list[i];
					String str = I18n.format(strl[0], new Object[0]);
					Attribute attri = new Attribute(str, Operation, Amount, UUIDLeast, UUIDMost);
					attriList[j] = attri;
					j++;
				}
			}
			Last.attributes = attriList;
			mc.displayGuiScreen(Last);
		} else if (button == bcancel) {
			mc.displayGuiScreen(Last);
		} else if (button == bNextPage) {
			page += 1;
			defineButton();
		} else if (button == bLastPage) {
			page -= 1;
			defineButton();
		}
		if (page - 1 < 0)
			bLastPage.enabled = false;
		else
			bLastPage.enabled = true;
		if ((page + 1) * maxAttribute > list.length)
			bNextPage.enabled = false;
		else
			bNextPage.enabled = true;
	}

	public void defineButton() {
		buttonList.removeAll(operation0Button);
		buttonList.removeAll(operation1Button);
		for (int i = page * maxAttribute; (i < (page + 1) * maxAttribute) && (i < list.length); i++) {
			buttonList.add(operation0List[i]);
			buttonList.add(operation1List[i]);
		}
		if (page - 1 < 0)
			bLastPage.enabled = false;
		else
			bLastPage.enabled = true;
		if ((page + 1) * maxAttribute - 1 > list.length)
			bNextPage.enabled = false;
		else
			bNextPage.enabled = true;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		list = Attribute.AttributeList;
		int adv = 0;
		if (isAdvanced)
			adv = 1;
		elementSize = (22 + fontRenderer.FONT_HEIGHT + adv * (22 + fontRenderer.FONT_HEIGHT));
		drawDefaultBackground();

		for (int i = page * maxAttribute; (i < (page + 1) * maxAttribute) && (i < list.length); i++) {
			String[] strl = (String[]) list[i];
			String str = I18n.format(strl[1], new Object[0]);
			amountList[i].setTextColor(Colors.WHITE);
			int outColor = Colors.GRAY;
			if (!amountList[i].getText().isEmpty()) {
				try {
					int a = Integer.valueOf(amountList[i].getText()).intValue();
					outColor = Colors.WHITE;
				} catch (Exception e) {
					amountList[i].setTextColor(Colors.RED);
					outColor = Colors.DARK_RED;
				}
			}
			fontRenderer.drawString(str, width / 2 - fontRenderer.getStringWidth(str) / 2,
					25 + i % maxAttribute * elementSize, outColor);
			amountList[i].drawTextBox();
			GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.itemfactory.attribute.amount", new Object[0]),
					width / 2 - 80, 25 + fontRenderer.FONT_HEIGHT + 1 + i % maxAttribute * elementSize, 20,
					Colors.GOLD);
			if (isAdvanced) {
				String str1 = "UUID";
				String str2 = "Least = ";
				String str3 = "Most = ";
				fontRenderer.drawString(str1, width / 2 - fontRenderer.getStringWidth(str1) / 2,
						25 + i * elementSize + fontRenderer.FONT_HEIGHT + 1 + 20, Colors.RED);
				GuiUtils.drawRightString(fontRenderer, str2, width / 2 - 80,
						45 + 2 * (fontRenderer.FONT_HEIGHT + 1) + i % maxAttribute * elementSize, 20, Colors.RED);
				GuiUtils.drawRightString(fontRenderer, str3, width / 2 + 125,
						45 + 2 * (fontRenderer.FONT_HEIGHT + 1) + i % maxAttribute * elementSize, 20, Colors.RED);
				UUIDLeast[i].drawTextBox();
				UUIDMost[i].drawTextBox();
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void initGui() {
		list = Attribute.AttributeList;
		int adv = 0;
		if (isAdvanced)
			adv = 1;
		int tfid = 0;
		elementSize = (22 + fontRenderer.FONT_HEIGHT + adv * (22 + fontRenderer.FONT_HEIGHT));
		maxAttribute = ((height - 23 - 23) / elementSize);
		page = 0;
		amountList = ((GuiTextField[]) ModMain.copyOf(new GuiTextField[0], list.length, GuiTextField[].class));
		for (int i = 0; i < amountList.length; i++) {
			amountList[i] = new GuiTextField(tfid, fontRenderer, width / 2 - 80,
					27 + fontRenderer.FONT_HEIGHT + 1 + i % maxAttribute * elementSize, 75, 16);
			tfid++;
		}
		operation0List = ((GuiButton[]) ModMain.copyOf(new GuiButton[0], list.length, GuiButton[].class));
		for (int i = 0; i < operation0List.length; i++) {
			operation0List[i] = new GuiButton(tfid, width / 2,
					25 + fontRenderer.FONT_HEIGHT + 1 + i % maxAttribute * elementSize, 89, 20, "+- Amount");
			operation0List[i].packedFGColour = buttonYESColor;
			operation0Button.add(operation0List[i]);
			tfid++;
		}
		operation1List = ((GuiButton[]) ModMain.copyOf(new GuiButton[0], list.length, GuiButton[].class));
		for (int i = 0; i < operation1List.length; i++) {
			operation1List[i] = new GuiButton(tfid, width / 2 + 90,
					25 + fontRenderer.FONT_HEIGHT + 1 + i % maxAttribute * elementSize, 120, 20,
					"+- Amount % (additive)");
			operation1List[i].packedFGColour = buttonNOColor;
			operation1Button.add(operation1List[i]);
			tfid++;
		}
		UUIDLeast = ((GuiTextField[]) ModMain.copyOf(new GuiTextField[0], list.length, GuiTextField[].class));
		for (int i = 0; i < amountList.length; i++) {
			UUIDLeast[i] = new GuiTextField(tfid, fontRenderer, width / 2 - 80,
					47 + 2 * (fontRenderer.FONT_HEIGHT + 1) + i % maxAttribute * elementSize, 75, 16);
			UUIDLeast[i].setText(String.valueOf(Integer.valueOf((int) (1000000.0D * Math.random()))));
			tfid++;
		}
		UUIDMost = ((GuiTextField[]) ModMain.copyOf(new GuiTextField[0], list.length, GuiTextField[].class));
		for (int i = 0; i < amountList.length; i++) {
			UUIDMost[i] = new GuiTextField(tfid, fontRenderer, width / 2 + 125,
					47 + 2 * (fontRenderer.FONT_HEIGHT + 1) + i % maxAttribute * elementSize, 75, 16);
			UUIDMost[i].setText(String.valueOf(Integer.valueOf((int) (1000000.0D * Math.random()))));
			tfid++;
		}
		for (int i = 0; i < Last.attributes.length; i++) {
			if (Last.attributes[i] != null) {
				for (int j = 0; j < list.length; j++) {
					String[] strl = (String[]) list[j];
					if (Last.attributes[i].Name.equals(strl[0])) {
						if (Last.attributes[i].Operation == 1) {
							operation0List[j].packedFGColour = buttonNOColor;
							operation1List[j].packedFGColour = buttonYESColor;
						} else {
							operation0List[j].packedFGColour = buttonYESColor;
							operation1List[j].packedFGColour = buttonNOColor;
						}
						amountList[j].setText(String.valueOf((int) Last.attributes[i].Amount));
						UUIDLeast[j].setText(String.valueOf(Last.attributes[i].UUIDLeast));
						UUIDMost[j].setText(String.valueOf(Last.attributes[i].UUIDMost));
					}
				}
			}
		}
		buttonList.add(this.bcancel = new GuiButton(5 + list.length, width / 2 - 150, 2, 149, 20,
				I18n.format("gui.act.cancel", new Object[0])));
		buttonList.add(this.bdone = new GuiButton(6 + list.length, width / 2, 2, 150, 20,
				I18n.format("gui.done", new Object[0])));
		buttonList.add(this.bLastPage = new GuiButton(7 + list.length, width / 2 - 75, height - 22, 74, 20, "<-"));
		buttonList.add(this.bNextPage = new GuiButton(8 + list.length, width / 2, height - 22, 75, 20, "->"));

		defineButton();

		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		for (int i = page * maxAttribute; (i < (page + 1) * maxAttribute) && (i < list.length); i++) {
			amountList[i].textboxKeyTyped(par1, par2);
			if (isAdvanced) {
				UUIDLeast[i].textboxKeyTyped(par1, par2);
				UUIDMost[i].textboxKeyTyped(par1, par2);
			}
		}
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		for (int i = page * maxAttribute; (i < (page + 1) * maxAttribute) && (i < list.length); i++) {
			amountList[i].mouseClicked(x, y, btn);
			if (isAdvanced) {
				UUIDLeast[i].mouseClicked(x, y, btn);
				UUIDMost[i].mouseClicked(x, y, btn);
			}
		}
		super.mouseClicked(x, y, btn);
	}

	public void updateScreen() {
		for (int i = page * maxAttribute; (i < (page + 1) * maxAttribute) && (i < list.length); i++) {
			amountList[i].updateCursorCounter();
			if (isAdvanced) {
				UUIDLeast[i].updateCursorCounter();
				UUIDMost[i].updateCursorCounter();
			}
		}
		super.updateScreen();
	}
}
