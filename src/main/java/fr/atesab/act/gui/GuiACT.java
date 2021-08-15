package fr.atesab.act.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import org.lwjgl.glfw.GLFW;

import fr.atesab.act.gui.ItemStackButtonWidget.ITooltipRenderer;
import fr.atesab.act.gui.act.ACTDevInfo;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class GuiACT extends Screen implements ITooltipRenderer {
	private static boolean devMode = false;

	public static void playClick() {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	protected static boolean isDevModeEnabled() {
		return devMode;
	}

	protected Screen parent;
	protected Minecraft mc;

	public GuiACT(Screen parent, Component name) {
		super(name);
		this.parent = parent;
		mc = Minecraft.getInstance();
	}

	/**
	 * @return the formatted title
	 */
	public String getStringTitle() {
		return super.title.getString();
	}

	public Screen getParent() {
		return parent;
	}

	public void setParent(Screen parent) {
		this.parent = parent;
	}

	public float getZLevel() {
		return getBlitOffset();
	}

	@Override
	public Minecraft getMinecraft() {
		return mc;
	}

	@Override
	public boolean keyPressed(int key, int mouseX, int mouseY) {
		if (key == GLFW.GLFW_KEY_K && hasControlDown() && hasShiftDown()) {
			devMode ^= true; // toggle dev mode
		}
		return super.keyPressed(key, mouseX, mouseY);
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
		if (devMode) {
			var entries = new ArrayList<ACTDevInfo>();
			entries.add(new ACTDevInfo(ChatFormatting.BOLD + "ACT Dev")); // header
			entries.add(new ACTDevInfo("Menu", this.getClass().getSimpleName())); // menu name
			generateDev(entries, mouseX, mouseY); // fetch from the menu

			// render
			var lines = entries.stream().mapToInt(dev -> 1 + dev.getElements().length).sum();
			var w = entries.stream().mapToInt(dev -> Math.max(font.width(dev.getTitle()),
					Arrays.stream(dev.getElements()).mapToInt(font::width).max().orElse(0))).max().getAsInt();
			var x = width - w - 4;
			var y = 4;
			GuiUtils.drawGradientRect(stack, x - 4, 0, width, y + lines * (font.lineHeight + 2) + 4, 0x44000000,
					0x44000000, getZLevel());
			for (var dev : entries) {
				GuiUtils.drawCenterString(font, dev.getTitle(), x + w / 2, y, ChatFormatting.RED.getColor());
				y += font.lineHeight + 2;
				for (var element : dev.getElements()) {
					GuiUtils.drawString(font, element, x, y, 0xFFFFFFFF, font.lineHeight);
					y += font.lineHeight + 2;
				}
			}
		}
		super.render(stack, mouseX, mouseY, delta);
	}

	@Override
	public void renderTooltip1(PoseStack matrixStack, ItemStack stack, int mouseX, int mouseY) {
		super.renderTooltip(matrixStack, stack, mouseX, mouseY);
	}

	/**
	 * generate the dev info list
	 * 
	 * @param entries the entries added to the dev mode
	 * @param mouseX  the mouseX
	 * @param mouseY  the mouseY
	 */
	protected void generateDev(List<ACTDevInfo> entries, int mouseX, int mouseY) {
		// fill by implementation
	}
}
