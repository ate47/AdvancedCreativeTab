package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Colors;
import fr.atesab.act.superclass.EnchantmentInfo;
import fr.atesab.act.superclass.Enchantments;
import fr.atesab.act.utils.GuiUtils;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;

public class GuiEnchantSelector extends GuiScreen {
	private GuiItemFactory Last;
	private GuiButton bdone;
	private GuiButton bcancel;
	private GuiButton ball100;
	private GuiButton ball0;
	private GuiButton bmax;
	private int preButton = 10;
	private Enchantments[] list = new Enchantments[0];
	private GuiTextField[] tfs = new GuiTextField[0];

	public int lgt;

	public GuiEnchantSelector(GuiItemFactory last) {
		Last = last;
	}

	public void actionPerformed(GuiButton button) {
		if (list != null) {
			if (button == bdone) {
				EnchantmentInfo[] ench = (EnchantmentInfo[]) ModMain.copyOf(new EnchantmentInfo[0], lgt,
						EnchantmentInfo[].class);
				for (int i = 0; i < ench.length; i++)
					ench[i] = null;
				int j = 0;
				for (int i = 0; i < lgt; i++) {
					int level = 1;
					boolean hasLevel = false;
					if (!tfs[i].getText().isEmpty())
						try {
							level = Integer.valueOf(tfs[i].getText()).intValue();
							hasLevel = true;
						} catch (Exception localException) {
						}
					if (hasLevel) {
						ench[j] = new EnchantmentInfo(list[i], level);
						j++;
					}
				}
				Last.enchantments = ((EnchantmentInfo[]) ModMain.copyOf(ench, j, EnchantmentInfo[].class));
				mc.displayGuiScreen(Last);
			}
			if (button == bmax) {
				for (int i = 0; i < tfs.length; i++) {
					tfs[i].setText(String.valueOf(list[i].getEnchantment().getMaxLevel()));
				}
			}
			if (button == ball100) {
				for (int i = 0; i < tfs.length; i++) {
					tfs[i].setText(String.valueOf(ModMain.MaxLevelEnch));
				}
			}
			if (button == ball0) {
				for (int i = 0; i < tfs.length; i++) {
					tfs[i].setText("");
				}
			}
		}
		if (button == bcancel)
			mc.displayGuiScreen(Last);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		if (list != null)
			for (int i = 0; i < tfs.length; i++) {
				int isInteger = Colors.GRAY;
				if (!tfs[i].getText().isEmpty())
					try {
						Integer.valueOf(tfs[i].getText());
						isInteger = Colors.azure1;
					} catch (Exception e) {
						isInteger = Colors.RED;
					}
				GuiUtils.drawRightString(fontRenderer, I18n.format(list[i].getEnchantment().getName()) + " : ",
						tfs[i].x, tfs[i].y, tfs[i].height, isInteger);
				tfs[i].drawTextBox();
			}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void initGui() {
		list = Enchantments.getEnchantments();
		lgt = 0;
		if (list != null)
			lgt = list.length;
		tfs = new GuiTextField[lgt];
		int ix = 0;
		int iy = 0;
		for (int i = 0; i < lgt; i++) {
			GuiTextField tf = new GuiTextField(i, fontRenderer, width / 2 - 100 + ix * 200, 50 + iy * 21, 40, 20);
			if (tf != null)
				tfs[i] = tf;
			if (ix == 1) {
				ix = 0;
				iy++;
			} else {
				ix++;
			}
		}
		for (int i = 0; i < lgt; i++) {
			for (int j = 0; j < Last.enchantments.length; j++) {
				if (list[i] == Last.enchantments[j].Enchantment) {
					tfs[i].setText(String.valueOf(Last.enchantments[j].Level));
				}
			}
		}
		bmax = new GuiButton(1 + lgt, width / 2 - 150, 26, 199, 20,
				I18n.format("gui.act.itemfactory.max", new Object[0]));

		ball100 = new GuiButton(3 + lgt, width / 2 - 50, 5, 99, 20,
				I18n.format("gui.act.itemfactory.set100", new Object[0]) + ModMain.MaxLevelEnch);
		ball0 = new GuiButton(4 + lgt, width / 2 - 150, 5, 99, 20,
				I18n.format("gui.act.itemfactory.set0", new Object[0]));

		bdone = new GuiButton(6 + lgt, width / 2 + 50, 26, 99, 20, I18n.format("gui.done", new Object[0]));

		bcancel = new GuiButton(5 + lgt, width / 2 + 50, 5, 99, 20, I18n.format("gui.act.cancel", new Object[0]));
		buttonList.add(bcancel);
		if (list != null) {
			buttonList.add(bdone);
			buttonList.add(ball100);
			buttonList.add(ball0);
			buttonList.add(bmax);
		}
		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		if (list != null) {
			for (int i = 0; i < tfs.length; i++)
				tfs[i].textboxKeyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		if (list != null) {
			for (int i = 0; i < tfs.length; i++)
				tfs[i].mouseClicked(x, y, btn);
		}
		super.mouseClicked(x, y, btn);
	}

	public void updateScreen() {
		if (list != null) {
			for (int i = 0; i < tfs.length; i++)
				tfs[i].updateCursorCounter();
		}
		super.updateScreen();
	}
}
