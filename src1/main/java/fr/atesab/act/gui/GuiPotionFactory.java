package fr.atesab.act.gui;

import fr.atesab.act.superclass.Colors;
import fr.atesab.act.superclass.Effect;
import fr.atesab.act.superclass.EffectType;
import fr.atesab.act.superclass.PotionSkin;
import fr.atesab.act.utils.GuiUtils;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GuiPotionFactory extends GuiScreen {
	private String[] duraList;
	private String[] amplList;
	private GuiButton badd;
	private GuiButton bgive;
	private GuiButton bskin;
	private GuiButton bremove;
	private GuiButton bmaxapl;
	private GuiButton bmaxlv;
	private GuiButton bmax;
	private GuiButton bdone;
	private int iy = 0;
	private int ix = 0;
	public GuiTextField[] tfs_dura;
	public GuiTextField[] tfs_ampl;
	public EffectType[] list;

	public GuiScreen Last;

	public Item item;

	public String skin;

	public String name;

	public GuiPotionFactory(GuiScreen last) {
		Last = last;
		list = Effect.eff_effect;
		duraList = new String[list.length];
		amplList = new String[list.length];
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == bmax) {
			for (int i = 0; i < tfs_ampl.length; i++) {
				tfs_ampl[i].setText("127");
				tfs_dura[i].setText(String.valueOf(Integer.MAX_VALUE));
			}
		}
		if (button == bmaxlv) {
			for (int i = 0; i < tfs_ampl.length; i++) {
				tfs_ampl[i].setText("127");
			}
		}
		if (button == bmaxapl) {
			for (int i = 0; i < tfs_ampl.length; i++) {
				tfs_dura[i].setText(String.valueOf(Integer.MAX_VALUE));
			}
		}
		if (button == bremove) {
			for (int i = 0; i < tfs_ampl.length; i++) {
				tfs_ampl[i].setText("");
				tfs_dura[i].setText("");
			}
		}
		if (button == badd) {
			fr.atesab.act.ModMain.addConfig("AdvancedItem",
					item.getUnlocalizedName().substring(5) + " 1 0 " + getPotion().getTagCompound().toString());
			fr.atesab.act.utils.ChatUtils.show(I18n.format("gui.act.add.msg", new Object[0]));
		}
		if (button == bgive)
			fr.atesab.act.utils.GiveUtils.give(mc, getPotion());
		if (button == bskin)
			mc.displayGuiScreen(new GuiPotionTypeSelector(this, name, skin, item) {
				public void setPotionType(String tName, Item tItem, String tSkin) {
					name = tName;
					item = tItem;
					skin = tSkin;
				}
			});
		if (button == bdone)
			mc.displayGuiScreen(Last);
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
		for (int i = 0; i < amplList.length; i++) {
			duraList[i] = tfs_dura[i].getText();
			amplList[i] = tfs_ampl[i].getText();
		}
		drawDefaultBackground();
		short[] posList = new short[tfs_ampl.length];
		for (int j = 0; j < posList.length; j++) {
			posList[j] = 0;
		}
		for (int i = 0; i < posList.length; i++)
			try {
				if (!tfs_ampl[i].getText().isEmpty()) {
					Integer.valueOf(tfs_ampl[i].getText());
					posList[i] = 1;
				}
			} catch (Exception e) {
				posList[i] = 2;
			}
		for (int i = 0; i < posList.length; i++)
			if (posList[i] != 2)
				try {
					if (!tfs_dura[i].getText().isEmpty()) {
						Integer.valueOf(tfs_dura[i].getText());
						posList[i] = 1;
					}
				} catch (Exception e) {
					posList[i] = 2;
				}
		for (int i = 0; i < tfs_ampl.length; i++) {
			int finalColor = Colors.GRAY;
			switch (posList[i]) {
			case 1:
				finalColor = Colors.WHITE;
				break;
			case 2:
				finalColor = Colors.RED;
			}

			GuiUtils.drawRightString(fontRenderer,
					I18n.format(new StringBuilder().append("effect.").append(list[i].name).toString(), new Object[0])
							+ " : ",
					tfs_ampl[i].x, tfs_ampl[i].x, tfs_ampl[i].y, finalColor);
			tfs_ampl[i].drawTextBox();
			tfs_dura[i].drawTextBox();
		}
		drawItemStack(getPotion(), width / 2 + 205, 5);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (GuiUtils.isHover(width / 2 + 205, 5, 21, 21, mouseX, mouseY))
			renderToolTip(getPotion(), mouseX, mouseY);
		if ((GuiUtils.isHover(width / 2 - 100, 50, 40, iy * 21 + 21, mouseX, mouseY))
				|| (GuiUtils.isHover(width / 2 + 100, 50, 40, iy * 21 + 21, mouseX, mouseY)))
			GuiUtils.drawTextBox(this, mc, fontRenderer,
					new String[] { I18n.format("gui.act.potionfactory.amplifier", new Object[0]) }, mouseX, mouseY,
					Colors.WHITE);
		if ((GuiUtils.isHover(width / 2 - 56, 50, 40, iy * 21 + 21, mouseX, mouseY))
				|| (GuiUtils.isHover(width / 2 - 56 + 200, 50, 40, iy * 21 + 21, mouseX, mouseY)))
			GuiUtils.drawTextBox(this, mc, fontRenderer,
					new String[] { I18n.format("gui.act.potionfactory.duration", new Object[0]) }, mouseX, mouseY,
					Colors.WHITE);
		if (!mc.player.capabilities.isCreativeMode)
			GuiUtils.buttonHoverMessage(this, mc, bgive, mouseX, mouseY, fontRenderer,
					new String[] { I18n.format("gui.act.nocreative", new Object[0]) }, Colors.RED);
	}

	public ItemStack getPotion() {
		String[] strs1 = new String[tfs_ampl.length];
		int j = 0;
		for (int i = 0; i < strs1.length; i++) {
			try {
				if ((!tfs_ampl[i].getText().isEmpty()) || (!tfs_dura[i].getText().isEmpty())) {
					int duration;
					if (!tfs_dura[i].getText().isEmpty()) {
						duration = Integer.valueOf(tfs_dura[i].getText()).intValue();
					} else
						duration = 600;
					int amplifier;
					if (!tfs_ampl[i].getText().isEmpty()) {
						amplifier = Integer.valueOf(tfs_ampl[i].getText()).intValue();
					} else {
						amplifier = 0;
					}
					strs1[j] = new Effect(list[i].id, duration, amplifier).getNBT();
					j++;
				}
			} catch (Exception localException) {
			}
		}
		String[] strs = new String[j];
		for (int i = 0; i < strs.length; i++) {
			strs[i] = strs1[i];
		}
		if (item == null) {
			if ((Effect.itm_type.length > 0) && (Effect.itm_type[0] != null)) {
				item = Effect.itm_type[0];
			} else
				item = net.minecraft.init.Items.POTIONITEM;
		}
		if (skin == null) {
			if ((Effect.psk_skin.length > 0) && (Effect.psk_skin[0] != null)) {
				skin = Effect.psk_skin[0].Name;
			} else
				skin = "water";
		}
		String a = skin;
		String b = "";
		if (name != null)
			b = "display:{Name:\"" + name.replaceAll("&&", "\u00a7") + "\"},";
		String c = Effect.getAllNBT(strs);
		String str = "{Potion:" + a + "," + b + "CustomPotionEffects:" + c + "}";

		return fr.atesab.act.utils.ItemStackGenHelper.getNBT(new ItemStack(item), str);
	}

	public void initGui() {
		ix = 0;
		iy = 0;
		tfs_ampl = new GuiTextField[Effect.eff_effect.length];
		tfs_dura = new GuiTextField[Effect.eff_effect.length];
		for (int i = 0; i < tfs_ampl.length; i++) {
			tfs_ampl[i] = new GuiTextField(i, fontRenderer, width / 2 - 100 + ix * 200, 50 + iy * 21, 40, 20);
			tfs_dura[i] = new GuiTextField(i + Effect.eff_effect.length, fontRenderer, width / 2 - 56 + ix * 200,
					50 + iy * 21, 40, 20);
			if (ix == 1) {
				ix = 0;
				iy += 1;
			} else {
				ix += 1;
			}
		}
		for (int i = 0; i < amplList.length; i++) {
			if (amplList[i] != null)
				tfs_ampl[i].setText(amplList[i]);
			if (duraList[i] != null)
				tfs_dura[i].setText(duraList[i]);
		}
		buttonList.add(this.bmax = new GuiButton(1, width / 2 - 100, 26, 99, 20,
				I18n.format("gui.act.itemfactory.max", new Object[0])));
		buttonList.add(this.bremove = new GuiButton(2, width / 2 - 200, 26, 99, 20,
				I18n.format("gui.act.itemfactory.set0", new Object[0])));

		buttonList.add(this.bmaxlv = new GuiButton(3, width / 2 - 100, 5, 99, 20,
				I18n.format("gui.act.itemfactory.set100", new Object[0]) + " "
						+ I18n.format("gui.act.potionfactory.amplifier", new Object[0]) + " 127"));
		buttonList.add(this.bmaxapl = new GuiButton(4, width / 2 - 200, 5, 99, 20,
				I18n.format("gui.act.itemfactory.set100", new Object[0]) + " "
						+ I18n.format("gui.act.potionfactory.duration", new Object[0])));

		buttonList.add(this.bskin = new GuiButton(5, width / 2, 5, 99, 20,
				I18n.format("gui.act.potionfactory.skin", new Object[0])));
		buttonList.add(
				this.badd = new GuiButton(5, width / 2 + 100, 5, 99, 20, I18n.format("gui.act.add", new Object[0])));

		buttonList
				.add(this.bgive = new GuiButton(5, width / 2, 26, 99, 20, I18n.format("gui.act.give", new Object[0])));
		buttonList.add(
				this.bdone = new GuiButton(6, width / 2 + 100, 26, 99, 20, I18n.format("gui.done", new Object[0])));

		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		for (int i = 0; i < tfs_ampl.length; i++) {
			tfs_ampl[i].textboxKeyTyped(par1, par2);
			tfs_dura[i].textboxKeyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		for (int i = 0; i < tfs_ampl.length; i++) {
			tfs_ampl[i].mouseClicked(x, y, btn);
			tfs_dura[i].mouseClicked(x, y, btn);
		}
		super.mouseClicked(x, y, btn);
	}

	public void updateScreen() {
		for (int i = 0; i < tfs_ampl.length; i++) {
			tfs_ampl[i].updateCursorCounter();
			tfs_dura[i].updateCursorCounter();
		}
		super.updateScreen();
	}
}
