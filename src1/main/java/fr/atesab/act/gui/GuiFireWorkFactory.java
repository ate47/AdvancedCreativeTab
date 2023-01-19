package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Colors;
import fr.atesab.act.superclass.Explosion;
import fr.atesab.act.superclass.Firework;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GiveUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemStackGenHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiFireWorkFactory extends GuiScreen {
	private GuiScreen Last = null;
	private GuiButton buttonDone;
	private GuiButton buttonAdd;
	private GuiButton buttonAddExp;
	private GuiButton buttonCopy;
	private GuiButton buttonGiveItem;
	private GuiButton buttonNextPage;
	private GuiButton buttonLastPage;
	private GuiValueButton[] explosionsBtn;
	private GuiValueButton[] delExplosionsBtn;
	private GuiValueButton[] addExplosionsBtn;
	private GuiTextField name = null;
	private GuiTextField flightduration = null;
	private int page = 0;
	public ArrayList<Explosion> explosions;
	public int elms;

	private int nextGuiId = 0;

	private int CursorY = -1;

	public GuiFireWorkFactory(GuiScreen last) {
		Last = last;
		explosions = new ArrayList<Explosion>();
		explosions.add(new Explosion(0, 1, 1, new int[] { Colors.RANDOM(), Colors.RANDOM() },
				new int[] { Colors.RANDOM(), Colors.RANDOM() }));
		explosions.add(new Explosion());
	}

	public void actionPerformed(GuiButton button) throws IOException {
		if (button == buttonDone)
			mc.displayGuiScreen(Last);
		if (button == buttonAdd) {
			ModMain.addConfig("AdvancedItem", getConfigData());
			ChatUtils.show(I18n.format("gui.act.add.msg", new Object[0]));
			mc.displayGuiScreen(Last);
		}
		if (button == buttonCopy) {
			GuiNbtCode.setClipboardString(getConfigData());
			ChatUtils.show(I18n.format("gui.act.clipload.msg", new Object[0]));
		}
		if (button == buttonGiveItem) {
			if (mc.player.capabilities.isCreativeMode) {
				int flight = 1;
				try {
					flight = Integer.valueOf(flightduration.getText()).intValue();
				} catch (Exception localException) {
				}
				int amount = 1;
				try {
					ItemStack stack = ItemStackGenHelper.getGive(getConfigData());
					GiveUtils.give(mc, stack);
				} catch (NumberInvalidException e) {
					ChatUtils.error(e.getMessage());
				}
			} else {
				ChatUtils.error(I18n.format("gui.act.nocreative", new Object[0]));
			}
		}
		if (button.id == 7) {
			explosions.remove(Integer.valueOf(String.valueOf(((GuiValueButton) button).getValue())).intValue());
			defineButton();
		} else if (button.id == 6) {
			GuiValueButton b = (GuiValueButton) button;
			mc.displayGuiScreen(new GuiExplosionSelector(this, ((Integer) b.getValue()).intValue(),
					explosions.get(((Integer) b.getValue()).intValue())) {
				public void saveExplosion(Explosion exp) {
					explosions.set(ExpIndex, exp);
				}
			});
		} else if (button.id == 8) {
			page -= 1;
			defineButton();
		} else if (button.id == 10) {
			page += 1;
			defineButton();
		} else if (button.id == 11) {
			explosions.add(Integer.valueOf(String.valueOf(((GuiValueButton) button).getValue())).intValue(),
					new Explosion());
			defineButton();
		}
		super.actionPerformed(button);
	}

	public void defineButton() {
		buttonList.clear();
		buttonList.add(this.buttonLastPage = new GuiButton(8, width / 2 + 20, height - 56, 31, 20, "<-"));
		buttonList.add(this.buttonNextPage = new GuiButton(10, width / 2 + 20 + 65, height - 56, 31, 20, "->"));
		buttonList.add(this.buttonDone = new GuiButton(2, width / 2 + 75, height - 35, 75, 20,
				I18n.format("gui.done", new Object[0])));
		buttonList.add(this.buttonAdd = new GuiButton(4, width / 2 - 75, height - 35, 75, 20,
				I18n.format("gui.act.add", new Object[0])));
		buttonList.add(this.buttonCopy = new GuiButton(3, width / 2 - 150, height - 35, 75, 20,
				I18n.format("gui.act.clipload", new Object[0])));
		buttonList.add(this.buttonGiveItem = new GuiButton(999, width / 2 - 0, height - 35, 75, 20,
				I18n.format("gui.act.give", new Object[0])));

		buttonLastPage.enabled = (page != 0);
		buttonNextPage.enabled = (page + 1 <= explosions.size() / elms);

		explosionsBtn = new GuiValueButton[explosions.size()];
		delExplosionsBtn = new GuiValueButton[explosions.size()];
		addExplosionsBtn = new GuiValueButton[explosions.size() + 1];
		int i;
		for (i = 0; i < explosions.size(); i++) {
			buttonList.add(explosionsBtn[i] = new GuiValueButton<Integer>(6, width / 2 + 20, 41 + 21 * (i % elms), 75, 20,
					I18n.format("gui.act.explosion", new Object[0]) + Integer.valueOf(i + 1), Integer.valueOf(i)));
			buttonList.add(delExplosionsBtn[i] = new GuiValueButton<Integer>(7, width / 2 + 20 + 76, 41 + 21 * (i % elms), 20,
					20, "\u00a7c-", Integer.valueOf(i)));
			buttonList.add(addExplosionsBtn[i] = new GuiValueButton<Integer>(11, width / 2 + 20 + 97, 41 + 21 * (i % elms), 20,
					20, "\u00a7a+", Integer.valueOf(i)));
		}
		buttonList.add(addExplosionsBtn[i] = new GuiValueButton<Integer>(11, width / 2 + 20, 41 + 21 * (i % elms), 75, 20,
				"\u00a7a+", Integer.valueOf(i)));
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		int info = -1;
		for (int i = page * elms; (i < (page + 1) * elms) && (i < explosions.size()); i++) {
			if (GuiUtils.isHover(explosionsBtn[i].x, explosionsBtn[i].y, 117, 20, mouseX, mouseY)) {
				info = i;
			}
		}

		GuiUtils.drawCenterString(fontRenderer, I18n.format("gui.act.fwf.exp", new Object[0]), width / 2 + 20, 21, 20,
				Colors.GOLD);
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.fwf.name", new Object[0]) + " : ", width / 2 - 100,
				height / 2 - 74, 18, Colors.WHITE);
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.fwf.flightduration", new Object[0]) + " : ",
				width / 2 - 100, height / 2 - 22, 18, Colors.WHITE);
		name.drawTextBox();
		flightduration.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (info >= 0) {
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/backgrounds.png"));
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			drawTexturedModalRect(mouseX + 5, mouseY + 5, 0, 0, 160,
					(fontRenderer.FONT_HEIGHT + 1) * (6 + explosions.get(info).getColors().length
							+ explosions.get(info).getFadeColors().length) + 10);
			CursorY = -1;
			fontRenderer.drawString(I18n.format("gui.act.explosion", new Object[0]) + Integer.valueOf(info + 1),
					mouseX + 10, mouseY + 10 + getCursorY(), Colors.AQUA);
			int colortrail = Colors.DARK_GREEN;
			String stringtrail = I18n.format("gui.act.yes", new Object[0]);
			if (explosions.get(info).getTrail() == 0) {
				colortrail = Colors.DARK_RED;
				stringtrail = I18n.format("gui.act.no", new Object[0]);
			}
			int colorflicker = Colors.DARK_GREEN;
			String stringflicker = I18n.format("gui.act.yes", new Object[0]);
			if (explosions.get(info).getFlicker() == 0) {
				colorflicker = Colors.DARK_RED;
				stringflicker = I18n.format("gui.act.no", new Object[0]);
			}
			int a = getCursorY();
			fontRenderer.drawString(I18n.format("item.fireworksCharge.trail", new Object[0]) + " : ", mouseX + 10,
					mouseY + 10 + a, Colors.DARK_AQUA);
			fontRenderer.drawString(stringtrail,
					mouseX + 10
							+ fontRenderer
									.getStringWidth(I18n.format("item.fireworksCharge.trail", new Object[0]) + " : "),
					mouseY + 10 + a, colortrail);
			a = getCursorY();
			fontRenderer.drawString(I18n.format("item.fireworksCharge.flicker", new Object[0]) + " : ", mouseX + 10,
					mouseY + 10 + a, Colors.DARK_AQUA);
			fontRenderer.drawString(stringflicker,
					mouseX + 10
							+ fontRenderer
									.getStringWidth(I18n.format("item.fireworksCharge.flicker", new Object[0]) + " : "),
					mouseY + 10 + a, colorflicker);
			a = getCursorY();
			fontRenderer.drawString(I18n.format("gui.act.type", new Object[0]) + " : "
					+ I18n.format(new StringBuilder().append("item.fireworksCharge.type.")
							.append(explosions.get(info).getType()).toString(), new Object[0]),
					mouseX + 10, mouseY + 10 + a, Colors.DARK_AQUA);
			fontRenderer.drawString(getChargeType(explosions.get(info).getType()),
					mouseX + 10 + fontRenderer.getStringWidth(I18n.format("gui.act.type", new Object[0]) + " : "),
					mouseY + 10 + a, Colors.DARK_AQUA);
			fontRenderer.drawString(I18n.format("gui.act.colors", new Object[0]) + " : ", mouseX + 10,
					mouseY + 10 + getCursorY(), Colors.DARK_AQUA);
			for (int i = 0; i < explosions.get(info).getColors().length; i++) {
				fontRenderer.drawString("=========", mouseX + 10, mouseY + 10 + getCursorY(),
						explosions.get(info).getColors()[i]);
			}

			fontRenderer.drawString(I18n.format("gui.act.fadecolor", new Object[0]) + " : ", mouseX + 10,
					mouseY + 10 + getCursorY(), Colors.DARK_AQUA);
			for (int i = 0; i < explosions.get(info).getFadeColors().length; i++) {
				fontRenderer.drawString("=========", mouseX + 10, mouseY + 10 + getCursorY(),
						explosions.get(info).getFadeColors()[i]);
			}
		}
		if (!mc.player.capabilities.isCreativeMode)
			GuiUtils.buttonHoverMessage(this, mc, buttonGiveItem, mouseX, mouseY, fontRenderer,
					I18n.format("gui.act.nocreative", new Object[0]).split("::"), Colors.RED);
	}

	public String getChargeType(int type) {
		switch (type) {
		case 0:
			return I18n.format("item.fireworksCharge.type.0", new Object[0]);
		case 1:
			return I18n.format("item.fireworksCharge.type.1", new Object[0]);
		case 2:
			return I18n.format("item.fireworksCharge.type.2", new Object[0]);
		case 3:
			return I18n.format("item.fireworksCharge.type.3", new Object[0]);
		case 4:
			return I18n.format("item.fireworksCharge.type.4", new Object[0]);
		}
		return I18n.format("item.fireworksCharge.type", new Object[0]);
	}

	public String getConfigData() {
		Firework fw = new Firework(explosions, getFlight());
		String str = name.getText();
		if (str != "")
			fw.setName(str);
		return "minecraft:fireworks 1 0 " + fw.getNBTFirework();
	}

	private int getCursorY() {
		CursorY += 1;
		return CursorY * (fontRenderer.FONT_HEIGHT + 1);
	}

	public int getFlight() {
		int flight = 1;
		try {
			flight = Integer.valueOf(flightduration.getText()).intValue();
		} catch (Exception localException) {
		}
		return flight;
	}

	public void initGui() {
		if (name == null)
			name = new GuiTextField(0, fontRenderer, width / 2 - 99, height / 2 - 74, 90, 20);
		fontRenderer.drawString(I18n.format("gui.act.fwf.colorinfo", new Object[0]), width / 2 - 99,
				height - 53 - fontRenderer.FONT_HEIGHT / 2, Colors.GRAY);
		if (flightduration == null) {
			flightduration = new GuiTextField(1, fontRenderer, width / 2 - 99, height / 2 - 22, 90, 20);
		}

		elms = ((height - 57 - 41) / 21);
		defineButton();

		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		name.textboxKeyTyped(par1, par2);
		flightduration.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		name.mouseClicked(x, y, btn);
		flightduration.mouseClicked(x, y, btn);
	}

	public void updateScreen() {
		name.updateCursorCounter();
		flightduration.updateCursorCounter();
		for (int i = 0; i < addExplosionsBtn.length; i++)
			if (i < (page + 1) * elms && i >= page * elms) {
				if (addExplosionsBtn[i] != null)
					addExplosionsBtn[i].visible = true;
				if (i < delExplosionsBtn.length && delExplosionsBtn[i] != null)
					delExplosionsBtn[i].visible = true;
				if (i < explosionsBtn.length && explosionsBtn[i] != null)
					explosionsBtn[i].visible = true;
			} else {
				if (addExplosionsBtn[i] != null)
					addExplosionsBtn[i].visible = false;
				if (i < delExplosionsBtn.length && delExplosionsBtn[i] != null)
					delExplosionsBtn[i].visible = false;
				if (i < explosionsBtn.length && explosionsBtn[i] != null)
					explosionsBtn[i].visible = false;
			}
	}
}
