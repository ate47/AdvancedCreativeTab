package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Colors;
import fr.atesab.act.utils.GuiUtils;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiSkullGiver extends GuiScreen {
	private GuiScreen Last;
	private GuiButton done;
	private GuiButton give;
	private GuiButton add;
	private GuiButton myhead;
	private GuiButton mylink;
	public GuiTextField name;
	public GuiTextField adv_link;

	public GuiSkullGiver() {
		Last = null;
	}

	public GuiSkullGiver(GuiScreen last) {
		Last = last;
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == give) {
			net.minecraft.item.ItemStack is = new fr.atesab.act.superclass.Head(name.getText()).getHead();
			if ((ModMain.AdvancedModActived) && (!adv_link.getText().isEmpty())) {
				is = fr.atesab.act.utils.ItemStackGenHelper.getCustomSkull(adv_link.getText(), name.getText());
			}
			fr.atesab.act.utils.GiveUtils.give(mc, is);
		}
		if (button == done)
			Minecraft.getMinecraft().displayGuiScreen(Last);
		if (button == myhead) {
			name.setText(Minecraft.getMinecraft().getSession().getUsername());
		}
		if (button == add) {
			ModMain.addConfig("HeadNames", name.getText());
			fr.atesab.act.utils.ChatUtils.show(I18n.format("gui.act.add.msg", new Object[0]));
		}
		if (button == mylink)
			mc.displayGuiScreen(new net.minecraft.client.gui.GuiConfirmOpenLink(this,
					"skins.minecraft.net/MinecraftSkins/" + name.getText() + ".png", 31102009, false));
		super.actionPerformed(button);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		fontRenderer.drawString(I18n.format("gui.act.skullfactory", new Object[0]),
				width / 2 - fontRenderer.getStringWidth(I18n.format("gui.act.skullfactory", new Object[0])) / 2,
				height / 2 - 1 - fontRenderer.FONT_HEIGHT, Colors.GOLD);
		int a = 0;
		if (ModMain.AdvancedModActived)
			a = 1;
		GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.fwf.name", new Object[0]) + " : ", width / 2 - 102,
				height / 2 - 22 - 22 * a, 20, Colors.GOLD);
		if (ModMain.AdvancedModActived)
			GuiUtils.drawRightString(fontRenderer, I18n.format("gui.act.skullfactory.link", new Object[0]) + " : ",
					width / 2 - 102, height / 2 - 22, 20, Colors.GOLD);
		name.drawTextBox();
		if (ModMain.AdvancedModActived)
			adv_link.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (!mc.player.capabilities.isCreativeMode)
			GuiUtils.buttonHoverMessage(this, mc, give, mouseX, mouseY, fontRenderer,
					new String[] { I18n.format("gui.act.nocreative", new Object[0]) }, Colors.RED);
		if ((ModMain.AdvancedModActived)
				&& (GuiUtils.isHover(width / 2 - 102, height / 2 - 22, 200, 20, mouseX, mouseY)))
			GuiUtils.drawTextBox(this, mc, fontRenderer,
					I18n.format("gui.act.skullfactory.help", new Object[0]).split("::"), mouseX + 5, mouseY + 5,
					Colors.GREEN);
		give.enabled = (!name.getText().isEmpty());
		add.enabled = (!name.getText().isEmpty());
		if (ModMain.AdvancedModActived)
			mylink.enabled = (!name.getText().isEmpty());
	}

	public void initGui() {
		buttonList.add(this.done = new GuiButton(2, width / 2 - 100, height / 2 + 42, 200, 20,
				I18n.format("gui.done", new Object[0])));
		buttonList.add(this.give = new GuiButton(4, width / 2, height / 2 + 21, 100, 20,
				I18n.format("gui.act.give", new Object[0])));
		int adv = 0;
		if (ModMain.AdvancedModActived) {
			adv = 1;
			adv_link = new GuiTextField(2, fontRenderer, width / 2 - 100, height / 2 - 22, 200, 20);
			adv_link.setMaxStringLength(200);
			buttonList.add(this.mylink = new GuiButton(6, width / 2, height / 2, 100, 20,
					I18n.format("gui.act.give.mylink", new Object[0])));
		}
		buttonList.add(this.myhead = new GuiButton(1, width / 2 - 100, height / 2, 200 - 100 * adv, 20,
				I18n.format("gui.act.give.myhead", new Object[0])));
		buttonList.add(this.add = new GuiButton(3, width / 2 - 100, height / 2 + 21, 99, 20,
				I18n.format("gui.act.add", new Object[0])));
		int a = 0;
		if (ModMain.AdvancedModActived)
			a = 1;
		name = new GuiTextField(1, fontRenderer, width / 2 - 100, height / 2 - 22 - 22 * a, 200, 20);
		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		name.textboxKeyTyped(par1, par2);
		if (ModMain.AdvancedModActived)
			adv_link.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		name.mouseClicked(x, y, btn);
		if (ModMain.AdvancedModActived)
			adv_link.mouseClicked(x, y, btn);
		super.mouseClicked(x, y, btn);
	}

	public void updateScreen() {
		if (ModMain.AdvancedModActived)
			adv_link.updateCursorCounter();
		name.updateCursorCounter();
		super.updateScreen();
	}
}
