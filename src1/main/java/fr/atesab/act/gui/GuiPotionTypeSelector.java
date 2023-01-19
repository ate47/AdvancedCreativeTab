package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Colors;
import fr.atesab.act.superclass.Effect;
import fr.atesab.act.superclass.PotionSkin;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemStackGenHelper;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class GuiPotionTypeSelector extends GuiScreen {
	public GuiScreen parent;
	private GuiButton bdone;
	private GuiButton bcancel;
	private GuiTextField name;
	private String typeName;
	private String skin;
	private Item item;

	public GuiPotionTypeSelector(GuiScreen parent, String typeName, String skin, Item item) {
		this.parent = parent;
		this.item = item;
		this.skin = skin;
		this.typeName = typeName;
	}
	public abstract void setPotionType(String name, Item item, String skin);
	
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == bdone) {
			setPotionType(name.getText(), item, skin);
			mc.displayGuiScreen(parent);
		}
		if (button == bcancel)
			mc.displayGuiScreen(parent);
		super.actionPerformed(button);
	}

	public void drawItemStack(ItemStack stack, int x, int y) {
		GlStateManager.translate(0.0F, 0.0F, 21.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		FontRenderer font = null;
		if (stack != null)
			font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = fontRenderer;
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.potionfactory.potion.type", new Object[0]) + " : ",
				(int) (width / 2 - 22 * Effect.itm_type.length / 2), 26, 20, Colors.GOLD);
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.fwf.name", new Object[0]) + " : ", width / 2 - 98,
				48, 20, Colors.GOLD);
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.potionfactory.potion.model", new Object[0]) + " : ",
				(int) (width / 2 - 99.0F), 69, 20, Colors.GOLD);
		name.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (int i = 0; i < Effect.itm_type.length; i++) {
			if (item == Effect.itm_type[i]){
				int px = (int) (width / 2 - 22 * Effect.itm_type.length / 2 + 22 * i);
				int pz = 26; 
				drawGradientRect(px, pz, px+20, pz+20, Color.WHITE.getRGB()*256+255, Color.WHITE.getRGB()*256+255);
			}
			if (GuiUtils.isHover((int) (width / 2 - 22 * Effect.itm_type.length / 2 + 22 * i), 26, 20, 20, mouseX,
					mouseY))
				renderToolTip(new ItemStack(Effect.itm_type[i]), mouseX, mouseY);
			drawItemStack(new ItemStack(Effect.itm_type[i]),
					(int) (width / 2 - 22 * Effect.itm_type.length / 2 + 22 * i), 26);
		}
		int xo = 0;
		int yo = 0;
		boolean rend = false;
		ItemStack info = new ItemStack(net.minecraft.init.Blocks.BARRIER);
		for (int i = 0; i < Effect.psk_skin.length; i++) {
			if (Effect.psk_skin[i].Name == skin){
				int px = (int) (width / 2 - 99.0F + 22 * xo);
				int pz = 69 + 22 * yo; 
				drawGradientRect(px, pz, px+20, pz+20, Color.WHITE.getRGB()*256+255, Color.WHITE.getRGB()*256+255);
			}
			drawItemStack(ItemStackGenHelper.getNBT(new ItemStack(item), "{Potion:" + Effect.psk_skin[i].Name + "}"),
					(int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo);
			if (GuiUtils.isHover((int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo, 21, 21, mouseX, mouseY)) {
				rend = true;
				info = ItemStackGenHelper.getNBT(new ItemStack(item), "{Potion:" + Effect.psk_skin[i].Name + "}");
			}
			if (xo > 7) {
				yo++;
				xo = 0;
			} else {
				xo++;
			}
			if (Effect.psk_skin[i].isLongable) {
				if (Effect.psk_skin[i].Name == skin) {
					int px = (int) (width / 2 - 99.0F + 22 * xo);
					int pz = 69 + 22 * yo; 
					drawGradientRect(px, pz, px+20, pz+20, Color.WHITE.getRGB()*256+255, Color.WHITE.getRGB()*256+255);
				}
				drawItemStack(
						ItemStackGenHelper.getNBT(new ItemStack(item), "{Potion:long_" + Effect.psk_skin[i].Name + "}"),
						(int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo);
				if (GuiUtils.isHover((int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo, 21, 21, mouseX, mouseY)) {
					rend = true;
					info = ItemStackGenHelper.getNBT(new ItemStack(item),
							"{Potion:long_" + Effect.psk_skin[i].Name + "}");
				}
				if (xo > 7) {
					yo++;
					xo = 0;
				} else {
					xo++;
				}
			}
			if (Effect.psk_skin[i].isStrongable) {
				if (Effect.psk_skin[i].Name == skin) {
					int px = (int) (width / 2 - 99.0F + 22 * xo);
					int pz = 69 + 22 * yo; 
					drawGradientRect(px, pz, px+20, pz+20, Color.WHITE.getRGB()*256+255, Color.WHITE.getRGB()*256+255);
				}
				drawItemStack(
						ItemStackGenHelper.getNBT(new ItemStack(item),
								"{Potion:strong_" + Effect.psk_skin[i].Name + "}"),
						(int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo);
				if (GuiUtils.isHover((int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo, 21, 21, mouseX, mouseY)) {
					rend = true;
					info = ItemStackGenHelper.getNBT(new ItemStack(item),
							"{Potion:strong_" + Effect.psk_skin[i].Name + "}");
				}
				if (xo > 7) {
					yo++;
					xo = 0;
				} else {
					xo++;
				}
			}
		}
		if (rend)
			renderToolTip(info, mouseX, mouseY);
	}

	public void initGui() {
		name = new GuiTextField(0, fontRenderer, width / 2 - 98, 48, 196, 16);
		name.setText(typeName);
		buttonList.add(this.bcancel = new GuiButton(0, width / 2 - 100, 5, 99, 20,
				I18n.format("gui.act.cancel", new Object[0])));
		buttonList.add(this.bdone = new GuiButton(1, width / 2, 5, 100, 20, I18n.format("gui.done", new Object[0])));
		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		name.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int mouseX, int mouseY, int btn) throws IOException {
		name.mouseClicked(mouseX, mouseY, btn);
		super.mouseClicked(mouseX, mouseY, btn);

		for (int i = 0; i < Effect.itm_type.length; i++)
			if (GuiUtils.isHover((int) (width / 2 - 22 * Effect.itm_type.length / 2 + 22 * i), 26, 20, 20, mouseX,
					mouseY))
				item = Effect.itm_type[i];
		int yo = 0;
		int xo = 0;
		for (int i = 0; i < Effect.psk_skin.length; i++) {
			if (GuiUtils.isHover((int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo, 21, 21, mouseX, mouseY))
				skin = Effect.psk_skin[i].Name;
			if (xo > 7) {
				yo++;
				xo = 0;
			} else {
				xo++;
			}
			if (Effect.psk_skin[i].isLongable) {
				if (GuiUtils.isHover((int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo, 21, 21, mouseX, mouseY))
					skin = ("long_" + Effect.psk_skin[i].Name);
				if (xo > 7) {
					yo++;
					xo = 0;
				} else {
					xo++;
				}
			}
			if (Effect.psk_skin[i].isStrongable) {
				if (GuiUtils.isHover((int) (width / 2 - 99.0F + 22 * xo), 69 + 22 * yo, 21, 21, mouseX, mouseY))
					skin = ("strong_" + Effect.psk_skin[i].Name);
				if (xo > 7) {
					yo++;
					xo = 0;
				} else {
					xo++;
				}
			}
		}
	}

	public void updateScreen() {
		name.updateCursorCounter();
		typeName = name.getText();
		super.updateScreen();
	}
}
