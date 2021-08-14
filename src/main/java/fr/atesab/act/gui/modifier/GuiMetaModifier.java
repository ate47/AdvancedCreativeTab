package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;

public class GuiMetaModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;

	public GuiMetaModifier(Screen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, new TranslatableComponent("gui.act.modifier.meta"), setter);
		this.stack = stack;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		GuiUtils.drawItemStack(itemRenderer, this, stack, width / 2 - 10, height / 2 - 21);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		if (GuiUtils.isHover(width / 2 - 10, height / 2 - 21, 20, 20, mouseX, mouseY))
			renderTooltip(matrixStack, stack, mouseX, mouseY);
	}

	@Override
	public void init() {
		int i = 0;
		addRenderableWidget(new GuiBooleanButton(width / 2 - 100, height / 2 - 21 + 21 * ++i,
				new TranslatableComponent("item.unbreakable"), b -> ItemUtils.setUnbreakable(stack, b),
				() -> ItemUtils.isUnbreakable(stack)));

		addRenderableWidget(new Button(width / 2 - 100, height / 2 - 21 + 21 * ++i, 200, 20,
				new TranslatableComponent("gui.act.modifier.tag.editor"), b -> {
					getMinecraft().setScreen(new GuiNBTModifier(GuiMetaModifier.this, tag -> stack.setTag(tag),
							stack.getTag() != null ? stack.getTag() : new CompoundTag()));
				}));
		addRenderableWidget(new Button(width / 2 - 100, height / 2 - 17 + 21 * ++i, 200, 20,
				new TranslatableComponent("gui.done"), b -> {
					set(stack);
					getMinecraft().setScreen(parent);
				}));
		super.init();
	}

}