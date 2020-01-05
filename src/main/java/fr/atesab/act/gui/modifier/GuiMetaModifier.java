package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiMetaModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;

	public GuiMetaModifier(GuiScreen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, setter);
		this.stack = stack;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GuiUtils.drawItemStack(itemRender, partialTicks, this, stack, width / 2 - 10, height / 2 - 21);
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.color3f(1, 1, 1);
		if (GuiUtils.isHover(width / 2 - 10, height / 2 - 21, 20, 20, mouseX, mouseY))
			renderToolTip(stack, mouseX, mouseY);
	}

	@Override
	public void initGui() {
		int i = 0;
		addButton(new GuiBooleanButton(1, width / 2 - 100, height / 2 - 21 + 21 * (i += 1),
				I18n.format("item.unbreakable").replaceAll(ChatUtils.MODIFIER + "[a-fA-F0-9rRk-oK-O]", ""),
				b -> ItemUtils.setUnbreakable(stack, b), () -> ItemUtils.isUnbreakable(stack)));

		addButton(new GuiButton(2, width / 2 - 100, height / 2 - 21 + 21 * (i += 1),
				I18n.format("gui.act.modifier.tag.editor")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiNBTModifier(GuiMetaModifier.this, tag -> stack.setTag(tag),
						stack.getTag() != null ? stack.getTag() : new NBTTagCompound()));
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(0, width / 2 - 100, height / 2 - 17 + 21 * (i += 1), I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				set(stack);
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		super.initGui();
	}

}