package fr.atesab.act.gui;

import java.awt.Color;
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
import net.minecraft.util.EnumHand;

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
		if ((mc = Minecraft.getInstance()).player != null) {
			ItemStack mainHand = mc.player.getHeldItem(EnumHand.MAIN_HAND);
			this.currentItemStack = mainHand != null ? mainHand : mc.player.getHeldItem(EnumHand.OFF_HAND);
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
	public boolean doesGuiPauseGame() {
		return false || (parent != null && parent.doesGuiPauseGame());
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		code.drawTextField(mouseX, mouseY, partialTicks);
		GuiUtils.drawCenterString(fontRenderer, I18n.format("gui.act.give"), width / 2, code.y - 21,
				Color.ORANGE.getRGB(), 20);
		super.render(mouseX, mouseY, partialTicks);
		if (currentItemStack != null) {
			GuiUtils.drawItemStack(itemRender, zLevel, this, currentItemStack, code.x + code.width + 5, code.y - 2);
			if (GuiUtils.isHover(code.x + code.width + 5, code.y, 20, 20, mouseX, mouseY))
				renderToolTip(currentItemStack, mouseX, mouseY);
		}
	}

	@Override
	public void initGui() {

		code = new GuiTextField(0, fontRenderer, width / 2 - 178, height / 2 + 2, 356, 16);
		code.setMaxStringLength(Integer.MAX_VALUE);
		if (preText != null)
			code.setText(preText.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
		boolean flag2 = deleteButton; // deleteCancel
		int s1 = flag2 ? 120 : 180;
		int s2 = 120;
		addButton(giveButton = new GuiButton(1, width / 2 - 180, height / 2 + 21, s1, 20,
				I18n.format("gui.act.give.give")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				ItemUtils.give(currentItemStack);
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(
				new GuiButton(2, width / 2 + s1 - 178, height / 2 + 21, s1 - 2, 20, I18n.format("gui.act.give.copy")) {
					@Override
					public void onClick(double mouseX, double mouseY) {
						GuiUtils.addToClipboard(code.getText());
						super.onClick(mouseX, mouseY);
					}
				});
		addButton(new GuiButton(3, width / 2 - 180 + (flag2 ? 2 * s1 + 1 : 0), height / 2 + 21 + (flag2 ? 0 : 21),
				(flag2 ? s1 - 1 : s2), 20, I18n.format("gui.act.give.editor")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(
						new GuiItemStackModifier(GuiGiver.this, currentItemStack, itemStack -> setCurrent(itemStack)));
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(doneButton = new GuiButton(0, width / 2 - 179 + 2 * s2, height / 2 + 42, s2 - 1, 20,
				I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				if (setter != null && currentItemStack != null)
					setter.accept(code.getText());
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		if (setter != null)
			addButton(new GuiButton(4, width / 2 - 58, height / 2 + 42, s2 - 2, 20, I18n.format("gui.act.cancel")) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					mc.displayGuiScreen(parent);
					super.onClick(mouseX, mouseY);
				}
			});
		else
			addButton(saveButton = new GuiButton(6, width / 2 - 58, height / 2 + 42, s2 - 2, 20,
					I18n.format("gui.act.save")) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					if (parent instanceof GuiMenu)
						((GuiMenu) parent).get();
					ACTMod.getCustomItems().add(code.getText());
					mc.displayGuiScreen(new GuiMenu(parent));
					super.onClick(mouseX, mouseY);
				}
			});
		if (deleteButton)
			addButton(new GuiButton(5, width / 2 - 180, height / 2 + 42, s2, 20, I18n.format("gui.act.delete")) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					setter.accept(null);
					mc.displayGuiScreen(parent);
					super.onClick(mouseX, mouseY);
				}
			});
		super.initGui();
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		return code.charTyped(key, modifiers);
	}
	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		return code.keyPressed(key, scanCode, modifiers) || super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return code.mouseClicked(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
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
	public void tick() {
		code.tick();
		this.currentItemStack = ItemUtils
				.getFromGiveCode(code.getText().replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
		this.giveButton.enabled = this.currentItemStack != null && mc.player != null && mc.player.isCreative();
		this.doneButton.enabled = (setter != null && this.currentItemStack != null) || setter == null;
		if (saveButton != null)
			saveButton.enabled = this.currentItemStack != null;
		super.tick();
	}
}
