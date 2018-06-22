package fr.atesab.act.gui.selector;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

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
			GuiUtils.drawItemStack(mc.getRenderItem(), parent.zLevel, parent, itemStack, offsetX, offsetY);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public void drawNext(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			if (GuiUtils.isHover(0, 0, 24, 24, mouseX, mouseY)) {
				drawRect(offsetX, offsetY, offsetX + 18, offsetY + 18, 0x55cccccc);
				parent.renderToolTip(itemStack, mouseX + offsetX, mouseY + offsetY);
			}
			super.drawNext(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public boolean match(String search) {
			return itemStack.getDisplayName().toLowerCase().contains(search.toLowerCase());
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			if (GuiUtils.isHover(0, 0, 24, 24, mouseX, mouseY) && mouseButton == 0)
				parent.select(itemStack);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	public GuiTypeListSelector(GuiScreen parent, Consumer<ItemStack> setter) {
		super(parent, new ArrayList<>(), setter, false);
		NonNullList<ItemStack> stacks = NonNullList.create();
		Item.REGISTRY.forEach(i -> {
			NonNullList<ItemStack> subStack = NonNullList.create();
			i.getSubItems(i.getCreativeTab() == null ? CreativeTabs.SEARCH : i.getCreativeTab(), subStack);
			boolean f = true;
			for (ItemStack sub : subStack)
				if (sub.getItem().equals(i) && sub.getCount() == 1 && sub.getMetadata() == 0
						&& (sub.getTagCompound() == null || sub.getTagCompound().hasNoTags())) {
					f = false;
					break;
				}
			if (f)
				stacks.add(new ItemStack(i));
			stacks.addAll(subStack);
		});
		stacks.forEach(stack -> elements.add(new TypeListElement(this, stack)));
	}

	public GuiTypeListSelector(GuiScreen parent, Consumer<ItemStack> setter, NonNullList<ItemStack> stacks) {
		this(parent, setter, stacks.stream());
	}

	public GuiTypeListSelector(GuiScreen parent, Consumer<ItemStack> setter, Stream<ItemStack> stacks) {
		super(parent, new ArrayList<>(), setter, false);
		stacks.forEach(stack -> elements.add(new TypeListElement(this, stack)));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
