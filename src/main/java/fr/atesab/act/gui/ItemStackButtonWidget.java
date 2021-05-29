package fr.atesab.act.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.item.ItemStack;

public class ItemStackButtonWidget extends AbstractButton {
	@FunctionalInterface
	public interface IItemStackPressable {
		void onPress(ItemStackButtonWidget button);
	}

	/**
	 * an wrapper for the {@link Screen#renderTooltip(ItemStack, int, int)} method
	 */
	public interface ITooltipRenderer {
		void renderTooltip(MatrixStack matrixStack, ItemStack stack, int mouseX, int mouseY);
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
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
		GuiUtils.drawItemStack(Minecraft.getInstance().getItemRenderer(), stack, x + 1, y + 1);
		if (isHovered())
			GuiUtils.drawRect(matrixStack, x, y, x + 18, y + 18, 0x55cccccc);
	}

	@Override
	public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
		parent.renderTooltip(matrixStack, getStack(), mouseX, mouseY);
		super.renderToolTip(matrixStack, mouseX, mouseY);
	}
}
