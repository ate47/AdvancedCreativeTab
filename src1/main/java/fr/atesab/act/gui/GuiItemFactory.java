package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Attribute;
import fr.atesab.act.superclass.Colors;
import fr.atesab.act.superclass.EnchantmentInfo;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GiveUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemStackGenHelper;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;

public class GuiItemFactory extends GuiScreen {
	public GuiButton give;
	public GuiButton add;
	public GuiScreen Last = null;
	public String StockName = "";
	public String StockMeta = "";
	public String StockItem = "";
	public GuiTextField name;
	public GuiTextField meta;
	public GuiTextField item;
	public GuiButton unbreak;
	public EnchantmentInfo[] enchantments = new EnchantmentInfo[0];
	public String[] lores = new String[0];
	public Attribute[] attributes = new Attribute[0];

	public GuiItemFactory() {
	}

	public GuiItemFactory(GuiScreen last) {
		Last = last;
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		ItemStack is = null;
		int metav = 0;
		switch (button.id) {
		case 0:
			try {
				metav = Integer.valueOf(meta.getText()).intValue();
			} catch (Exception localException) {
			}
			ModMain.addConfig("AdvancedItem", item.getText() + " 1 " + metav + " " + getNbt());
			ChatUtils.show(I18n.format("gui.act.add.msg", new Object[0]));
			break;
		case 1:
			try {
				try {
					metav = Integer.valueOf(meta.getText()).intValue();
				} catch (Exception localException1) {
				}
				is = ItemStackGenHelper.getGive(item.getText() + " 1 " + metav + " " + getNbt());
				GiveUtils.give(mc, is);
			} catch (NumberInvalidException e) {
				ChatUtils.error(I18n.format("gui.act.give.fail2", new Object[0]));
			}

		case 2:
			Minecraft.getMinecraft().displayGuiScreen(Last);
			break;
		case 3:
			Minecraft.getMinecraft().displayGuiScreen(new GuiEnchantSelector(this));
			break;
		case 4:
			Minecraft.getMinecraft().displayGuiScreen(new GuiAttributeSelector(this));
			break;
		case 5:
			Minecraft.getMinecraft().displayGuiScreen(
					new GuiTextListConfig(this, lores, I18n.format("gui.act.itemfactory.lores", new Object[0])) {
						public void onUpdateValue(String[] value) {
							((GuiItemFactory) Last).lores = value;
						}
					});
			break;
		case 6:
			if (unbreak.packedFGColour == GuiAttributeSelector.buttonNOColor) {
				unbreak.packedFGColour = GuiAttributeSelector.buttonYESColor;
			} else {
				unbreak.packedFGColour = GuiAttributeSelector.buttonNOColor;
			}
			break;
		}
		super.actionPerformed(button);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		StockItem = item.getText();
		StockMeta = meta.getText();
		StockName = name.getText();
		drawDefaultBackground();
		meta.drawTextBox();
		name.drawTextBox();
		item.drawTextBox();
		GuiUtils.drawRightString(fontRenderer, " : ", width / 2 + 122, height / 2 - 19, 20, Colors.WHITE);
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.fwf.name", new Object[0]) + " = ", width / 2 - 148,
				height / 2 - 40, 20, Colors.GOLD);
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.itemfactory.itemid", new Object[0]) + " = ",
				width / 2 - 148, height / 2 - 20, 20, Colors.GOLD);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if ((!mc.player.capabilities.isCreativeMode)
				&& (GuiUtils.isHover(width / 2 - 50, height / 2 + 42, 99, 20, mouseX, mouseY)))
			GuiUtils.drawTextBox(this, mc, fontRenderer,
					new String[] { I18n.format("gui.act.nocreative", new Object[0]) }, mouseX, mouseY, Colors.RED);
		give.enabled = (!item.getText().isEmpty());
		add.enabled = (!item.getText().isEmpty());
	}

	public String getAttributeNbt() {
		String str = "[";
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i] != null)
				str = str + "," + attributes[i].getAttriNbt();
		}
		return str.replaceFirst(",", "") + "]";
	}

	public String getEnchNbt() {
		String str = "[";
		for (int i = 0; i < enchantments.length; i++) {
			if (enchantments[i] != null)
				str = str + "," + enchantments[i].getEnchNbt();
		}
		return str.replaceFirst(",", "") + "]";
	}

	public String getLoreNbt() {
		String[] lores2 = lores;
		int j = 0;
		for (int i = 0; i < lores2.length; i++)
			if (!lores2[i].isEmpty()) {
				lores2[j] = lores[i].replaceAll("&&", "\u00a7");
				j++;
			}
		for (int i = j; i < lores2.length; i++) {
			lores2[i] = null;
		}
		String str = "[";
		for (int i = 0; i < lores2.length; i++) {
			if (lores2[i] != null)
				str = str + ",\"" + lores2[i] + "\"";
		}
		return str.replaceFirst(",", "") + "]";
	}

	public String getNbt() {
		String nameout = name.getText().replaceAll("&&", "\u00a7");
		String unb = "";
		if (unbreak.packedFGColour == GuiAttributeSelector.buttonYESColor) {
			unb = "Unbreakable:1,";
		}
		nameout = "";
		if (name.getText().isEmpty())
			nameout = "Name:\"" + name.getText() + "\",";
		return "{" + unb + "display:{" + nameout + "Lore:" + getLoreNbt() + "},AttributeModifiers:" + getAttributeNbt()
				+ ",ench:" + getEnchNbt() + "}";
	}

	public void initGui() {
		name = new GuiTextField(6, fontRenderer, width / 2 - 148, height / 2 - 40, 296, 16);
		item = new GuiTextField(6, fontRenderer, width / 2 - 148, height / 2 - 19,
				266 - fontRenderer.getStringWidth(" : "), 16);
		meta = new GuiTextField(6, fontRenderer, width / 2 + 122, height / 2 - 19, 26, 16);
		name.setText(StockName);
		meta.setText(StockMeta);
		item.setText(StockItem);
		buttonList.add(new GuiButton(5, width / 2 - 150, height / 2, 149, 20,
				I18n.format("gui.act.itemfactory.lores", new Object[0])));
		unbreak = new GuiButton(6, width / 2, height / 2, 150, 20, I18n.format("item.unbreakable", new Object[0]));
		unbreak.packedFGColour = GuiAttributeSelector.buttonNOColor;
		buttonList.add(unbreak);
		buttonList.add(new GuiButton(4, width / 2 - 150, height / 2 + 21, 149, 20,
				I18n.format("gui.act.itemfactory.attributes", new Object[0])));
		buttonList.add(new GuiButton(3, width / 2, height / 2 + 21, 150, 20,
				I18n.format("gui.act.itemfactory.enchants", new Object[0])));
		buttonList.add(
				new GuiButton(2, width / 2 - 150, height / 2 + 42, 99, 20, I18n.format("gui.done", new Object[0])));
		give = new GuiButton(1, width / 2 - 50, height / 2 + 42, 99, 20, I18n.format("gui.act.give", new Object[0]));
		add = new GuiButton(0, width / 2 + 50, height / 2 + 42, 100, 20, I18n.format("gui.act.add", new Object[0]));
		buttonList.add(give);
		buttonList.add(add);
		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		name.textboxKeyTyped(par1, par2);
		meta.textboxKeyTyped(par1, par2);
		item.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		name.mouseClicked(x, y, btn);
		meta.mouseClicked(x, y, btn);
		item.mouseClicked(x, y, btn);
		super.mouseClicked(x, y, btn);
	}

	public void updateScreen() {
		name.updateCursorCounter();
		meta.updateCursorCounter();
		item.updateCursorCounter();
	}
}
