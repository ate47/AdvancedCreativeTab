package fr.atesab.act.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.world.item.ItemStack;

public class ItemStackButtonWidget extends AbstractButton {
	@FunctionalInterface
	public interface IItemStackPressable {
		void onPress(ItemStackButtonWidget button);
	}

	/**
	 * an wrapper for the render Screen item tool tip method
	 */
	public interface ITooltipRenderer {
		void renderTooltip1(PoseStack matrixStack, ItemStack stack, int mouseX, int mouseY);
	}

	private ItemStack stack;
	private ITooltipRenderer parent;
	private IItemStackPressable pressable;

	public ItemStackButtonWidget(ITooltipRenderer parent, int x, int y, ItemStack stack,
			IItemStackPressable pressable) {
		super(x, y, 18, 18, stack.getDisplayName());
		this.stack = stack;
		this.parent = parent;
		this.pressable = pressable;
	}

	public ItemStack getStack() {
		return stack;
	}

	@Override
	public void onPress() {
		pressable.onPress(this);
	}

	@Override
	public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTick) {
		GuiUtils.drawItemStack(Minecraft.getInstance().getItemRenderer(), stack, x + 1, y + 1);
		if (isHoveredOrFocused())
			GuiUtils.drawRect(matrixStack, x, y, x + 18, y + 18, 0x55cccccc);
	}

	@Override
	public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
		parent.renderTooltip1(matrixStack, getStack(), mouseX, mouseY);
		super.renderToolTip(matrixStack, mouseX, mouseY);
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}
}
