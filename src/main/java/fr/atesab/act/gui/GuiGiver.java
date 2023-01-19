package fr.atesab.act.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.function.Consumer;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiItemStackModifier;
import fr.atesab.act.gui.modifier.GuiModifier;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class GuiGiver extends GuiModifier<String> {
	private GuiButton giveButton, saveButton, doneButton;
	private GuiTextField code;
	private String preText;
	private ItemStack currentItemStack;
	private Consumer<String> setter;
	private boolean deleteButton;

	public GuiGiver(GuiScreen parent) {
		super(parent, s -> {
		});
		if ((mc = Minecraft.getMinecraft()).thePlayer != null && mc.thePlayer.inventory.getCurrentItem() != null) {
			this.currentItemStack = mc.thePlayer.getHeldItem();
			this.preText = ItemUtils.getGiveCode(this.currentItemStack);
		}
	}

	public GuiGiver(GuiScreen parent, ItemStack itemStack) {
		this(parent, itemStack, null, false);
	}

	public GuiGiver(GuiScreen parent, ItemStack itemStack, Consumer<String> setter, boolean deleteButton) {
		super(parent, s -> {
		});
		this.preText = itemStack != null ? ItemUtils.getGiveCode(itemStack) : "";
		this.currentItemStack = itemStack;
		this.setter = setter;
		this.deleteButton = deleteButton;
	}

	public GuiGiver(GuiScreen parent, String preText) {
		this(parent, preText, s -> {
		}, false);
	}

	public GuiGiver(GuiScreen parent, String preText, Consumer<String> setter, boolean deleteButton) {
		this(parent, preText != null && !preText.isEmpty() ? ItemUtils.getFromGiveCode(preText) : null);
		this.preText = preText;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) { // done
			if (setter != null && currentItemStack != null)
				setter.accept(code.getText());
			mc.displayGuiScreen(parent);
		} else if (button.id == 1) // give
			ItemUtils.give(mc, currentItemStack);
		else if (button.id == 2) // copy
			GuiUtils.addToClipboard(code.getText());
		else if (button.id == 3) // editor
			mc.displayGuiScreen(new GuiItemStackModifier(this, currentItemStack, itemStack -> setCurrent(itemStack)));
		else if (button.id == 4) { // cancel
			mc.displayGuiScreen(parent);
		} else if (button.id == 5) { // delete
			setter.accept(null);
			mc.displayGuiScreen(parent);
		} else if (button.id == 6) { // save
			if (parent instanceof GuiMenu)
				((GuiMenu) parent).get();
			ACTMod.getCustomItems().add(code.getText());
			mc.displayGuiScreen(new GuiMenu(parent));
		}
		super.actionPerformed(button);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false || (parent != null && parent.doesGuiPauseGame());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		code.drawTextBox();
		GuiUtils.drawCenterString(fontRendererObj, I18n.format("gui.act.give"), width / 2, code.yPosition - 21,
				Color.ORANGE.getRGB(), 20);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (currentItemStack != null) {
			GuiUtils.drawItemStack(itemRender, zLevel, this, currentItemStack, code.xPosition + code.width + 5,
					code.yPosition - 2);
			if (GuiUtils.isHover(code.xPosition + code.width + 5, code.yPosition, 20, 20, mouseX, mouseY))
				renderToolTip(currentItemStack, mouseX, mouseY);
		}
	}

	@Override
	public void initGui() {
		code = new GuiTextField(0, fontRendererObj, width / 2 - 178, height / 2 + 2, 356, 16);
		code.setMaxStringLength(Integer.MAX_VALUE);
		if (preText != null)
			code.setText(preText.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
		boolean flag2 = deleteButton; // deleteCancel
		int s1 = flag2 ? 120 : 180;
		int s2 = 120;
		buttonList.add(giveButton = new GuiButton(1, width / 2 - 180, height / 2 + 21, s1, 20,
				I18n.format("gui.act.give.give")));
		buttonList.add(
				new GuiButton(2, width / 2 + s1 - 178, height / 2 + 21, s1 - 2, 20, I18n.format("gui.act.give.copy")));
		buttonList.add(new GuiButton(3, width / 2 - 180 + (flag2 ? 2 * s1 + 1 : 0), height / 2 + 21 + (flag2 ? 0 : 21),
				(flag2 ? s1 - 1 : s2), 20, I18n.format("gui.act.give.editor")));
		buttonList.add(doneButton = new GuiButton(0, width / 2 - 179 + 2 * s2, height / 2 + 42, s2 - 1, 20,
				I18n.format("gui.done")));
		if (setter != null)
			buttonList
					.add(new GuiButton(4, width / 2 - 58, height / 2 + 42, s2 - 2, 20, I18n.format("gui.act.cancel")));
		else
			buttonList.add(saveButton = new GuiButton(6, width / 2 - 58, height / 2 + 42, s2 - 2, 20,
					I18n.format("gui.act.save")));
		if (deleteButton)
			buttonList.add(new GuiButton(5, width / 2 - 180, height / 2 + 42, s2, 20, I18n.format("gui.act.delete")));
		super.initGui();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		code.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		code.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onResize(Minecraft mcIn, int w, int h) {
		super.onResize(mcIn, w, h);
	}

	private void setCurrent(ItemStack currentItemStack) {
		preText = ItemUtils.getGiveCode(this.currentItemStack = currentItemStack);
	}

	public void setCurrentItemStack(ItemStack currentItemStack) {
		this.currentItemStack = currentItemStack;
		this.preText = ItemUtils.getGiveCode(currentItemStack);
	}

	public void setPreText(String preText) {
		this.preText = preText;
		this.currentItemStack = preText != null && !preText.isEmpty() ? ItemUtils.getFromGiveCode(preText) : null;
	}

	@Override
	public void updateScreen() {
		code.updateCursorCounter();
		this.currentItemStack = ItemUtils
				.getFromGiveCode(code.getText().replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
		this.giveButton.enabled = this.currentItemStack != null && mc.thePlayer != null && mc.thePlayer.capabilities.isCreativeMode;
		this.doneButton.enabled = (setter != null && this.currentItemStack != null) || setter == null;
		if (saveButton != null)
			saveButton.enabled = this.currentItemStack != null;
		super.updateScreen();
	}
}
