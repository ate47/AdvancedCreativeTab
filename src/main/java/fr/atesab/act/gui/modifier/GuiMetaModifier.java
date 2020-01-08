package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;

import com.mojang.blaze3d.platform.GlStateManager;

import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class GuiMetaModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;

	public GuiMetaModifier(Screen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, "gui.act.modifier.meta", setter);
		this.stack = stack;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		GuiUtils.drawItemStack(itemRenderer, this, stack, width / 2 - 10, height / 2 - 21);
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.color3f(1, 1, 1);
		if (GuiUtils.isHover(width / 2 - 10, height / 2 - 21, 20, 20, mouseX, mouseY))
			renderTooltip(stack, mouseX, mouseY);
	}

	@Override
	public void init() {
		int i = 0;
		addButton(new GuiBooleanButton(width / 2 - 100, height / 2 - 21 + 21 * (i += 1),
				I18n.format("item.unbreakable").replaceAll(ChatUtils.MODIFIER + "[a-fA-F0-9rRk-oK-O]", ""),
				b -> ItemUtils.setUnbreakable(stack, b), () -> ItemUtils.isUnbreakable(stack)));

		addButton(new Button(width / 2 - 100, height / 2 - 21 + 21 * (i += 1), 200, 20,
				I18n.format("gui.act.modifier.tag.editor"), b -> {
					getMinecraft().displayGuiScreen(new GuiNBTModifier(GuiMetaModifier.this, tag -> stack.setTag(tag),
							stack.getTag() != null ? stack.getTag() : new CompoundNBT()));
				}));
		addButton(new Button(width / 2 - 100, height / 2 - 17 + 21 * (i += 1), 200, 20, I18n.format("gui.done"), b -> {
			set(stack);
			getMinecraft().displayGuiScreen(parent);
		}));
		super.init();
	}

}