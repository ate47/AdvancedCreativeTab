package fr.atesab.act.gui.selector;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;

public class GuiTypeListSelector extends GuiListSelector<ItemStack> {

	static class TypeListElement extends ListElement {
		private ItemStack itemStack;
		private GuiTypeListSelector parent;

		public TypeListElement(GuiTypeListSelector parent, ItemStack itemStack) {
			super(24, 24);
			this.itemStack = itemStack;
			this.parent = parent;
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawItemStack(mc.getItemRenderer(), parent.zLevel, parent, itemStack, offsetX + 1, offsetY + 1);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public void drawNext(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			if (GuiUtils.isHover(0, 0, 18, 18, mouseX, mouseY)) {
				drawRect(offsetX, offsetY, offsetX + 18, offsetY + 18, 0x55cccccc);
				parent.renderToolTip(itemStack, mouseX + offsetX, mouseY + offsetY);
			}
			super.drawNext(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public boolean match(String search) {
			String s = search.toLowerCase();
			return itemStack.getDisplayName().getUnformattedComponentText().toLowerCase().contains(s)
					|| itemStack.getItem().getRegistryName().toString().toLowerCase().contains(s);
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			if (GuiUtils.isHover(0, 0, 24, 24, mouseX, mouseY) && mouseButton == 0)
				parent.select(itemStack);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public GuiTypeListSelector(GuiScreen parent, Function<ItemStack, GuiScreen> setter) {
		super(parent, new ArrayList<>(), setter, false, new Tuple[0]);
		NonNullList<ItemStack> stacks = NonNullList.create();
		IRegistry.field_212630_s.forEach(i -> // Item.REGISTRY
		stacks.add(new ItemStack(i)));
		stacks.forEach(stack -> elements.add(new TypeListElement(this, stack)));
	}

	public GuiTypeListSelector(GuiScreen parent, Function<ItemStack, GuiScreen> setter, NonNullList<ItemStack> stacks) {
		this(parent, setter, stacks.stream());
	}

	@SuppressWarnings("unchecked")
	public GuiTypeListSelector(GuiScreen parent, Function<ItemStack, GuiScreen> setter, Stream<ItemStack> stacks) {
		super(parent, new ArrayList<>(), setter, false, new Tuple[0]);
		stacks.forEach(stack -> elements.add(new TypeListElement(this, stack)));
	}
}
