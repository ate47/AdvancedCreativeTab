package fr.atesab.act.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiItemStackModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class GuiMenu extends GuiListModifier<Object> {
	private static class MenuListElement extends ListElement {
		private GuiMenu parent;
		private ItemStack stack;

		public MenuListElement(GuiMenu parent, ItemStack stack) {
			super(24, 24);
			this.parent = parent;
			this.stack = stack;
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawItemStack(mc.getRenderItem(), parent.zLevel, parent, stack, offsetX + 1, offsetY + 1);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public void drawNext(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			if (GuiUtils.isHover(0, 0, 18, 18, mouseX, mouseY)) {
				drawRect(offsetX, offsetY, offsetX + 18, offsetY + 18, 0x55cccccc);
				parent.renderToolTip(stack, mouseX + offsetX, mouseY + offsetY);
			}
			super.drawNext(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public boolean match(String search) {
			search = search.toLowerCase();
			return stack.getDisplayName().toLowerCase().contains(search)
					|| stack.getItem().getDefaultInstance().getDisplayName().toLowerCase().contains(search);
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			if (GuiUtils.isHover(0, 0, 18, 18, mouseX, mouseY)) {
				playClick();
				if (mouseButton == 0) {
					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
						int i = parent.elements.indexOf(this);
						parent.addListElement(i, new MenuListElement(parent, stack.copy()));
					} else
						mc.displayGuiScreen(new GuiGiver(parent, stack, is -> {
							if (is != null)
								stack = is;
							else
								parent.removeListElement(this);
						}, true));
				} else if (mouseButton == 1)
					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
						parent.removeListElement(this);
					else
						ItemUtils.give(mc, stack);
			}
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	private boolean save = true;

	private Consumer<ItemStack> ADD_STACK = is -> {
		if (is != null)
			addListElement(elements.size() - 1, new MenuListElement(this, is));
	};

	private Runnable ADD = () -> {
		mc.displayGuiScreen(new GuiTypeListSelector(this, is -> {
			GuiGiver giver = new GuiGiver(this, null, ADD_STACK, false);
			if (mc.currentScreen instanceof GuiTypeListSelector)
				((GuiTypeListSelector) mc.currentScreen).setParent(giver);
			giver.setPreText(ItemUtils.getCustomTag(is, ACTMod.TEMPLATE_TAG_NAME, ""));
		}, ACTMod.getTemplates()));
	};

	public GuiMenu(GuiScreen parent) {
		super(parent, new ArrayList<>(), o -> {
		}, true, false);
		Tuple btn1 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("cmd.act.edit"), new Tuple<>(() -> {
			final int slot = mc.player.inventory.currentItem;
			mc.displayGuiScreen(new GuiItemStackModifier(this, mc.player.getHeldItemMainhand().copy(),
					is -> ItemUtils.give(mc, is, 36 + slot)));
		}, () -> {
		}));
		Tuple btn2 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("key.act.giver"),
				new Tuple<>(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiGiver(this)), () -> {
				}));
		buttons = Minecraft.getMinecraft().player == null ? new Tuple[] { btn2 } : new Tuple[] { btn1, btn2 };
		elements.add(new ButtonElementList(24, 24, 20, 20, TextFormatting.GREEN + "+", ADD, null));
		ACTMod.customItems.forEach(data -> ADD_STACK.accept(ItemUtils.getFromGiveCode(data)));
	}

	@Override
	protected Object get() {
		ACTMod.customItems.clear();
		elements.stream().filter(le -> le instanceof MenuListElement)
				.forEach(m -> ACTMod.customItems.add(ItemUtils.getGiveCode(((MenuListElement) m).stack)));
		ACTMod.saveConfig();
		return null;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		save = false;
		super.mouseClicked(mouseX, mouseY, mouseButton);
		save = true;
	}

	@Override
	public void onGuiClosed() {
		if (save)
			get();
		else
			save = true;
		super.onGuiClosed();
	}

}
