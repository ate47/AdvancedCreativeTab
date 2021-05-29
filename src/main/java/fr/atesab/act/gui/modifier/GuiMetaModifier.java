package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiMetaModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;

	public GuiMetaModifier(Screen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, new TranslationTextComponent("gui.act.modifier.meta"), setter);
		this.stack = stack;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		GuiUtils.drawItemStack(itemRenderer, this, stack, width / 2 - 10, height / 2 - 21);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		GuiUtils.color3f(1, 1, 1);
		if (GuiUtils.isHover(width / 2 - 10, height / 2 - 21, 20, 20, mouseX, mouseY))
			renderTooltip(matrixStack, stack, mouseX, mouseY);
	}

	@Override
	public void init() {
		int i = 0;
		addButton(new GuiBooleanButton(width / 2 - 100, height / 2 - 21 + 21 * ++i,
				new TranslationTextComponent("item.unbreakable"), b -> ItemUtils.setUnbreakable(stack, b),
				() -> ItemUtils.isUnbreakable(stack)));

		addButton(new Button(width / 2 - 100, height / 2 - 21 + 21 * ++i, 200, 20,
				new TranslationTextComponent("gui.act.modifier.tag.editor"), b -> {
					getMinecraft().setScreen(new GuiNBTModifier(GuiMetaModifier.this, tag -> stack.setTag(tag),
							stack.getTag() != null ? stack.getTag() : new CompoundNBT()));
				}));
		addButton(new Button(width / 2 - 100, height / 2 - 17 + 21 * ++i, 200, 20,
				new TranslationTextComponent("gui.done"), b -> {
					set(stack);
					getMinecraft().setScreen(parent);
				}));
		super.init();
	}

}