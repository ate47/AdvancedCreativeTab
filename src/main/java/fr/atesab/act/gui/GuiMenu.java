package fr.atesab.act.gui;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiItemStackModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
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
			GuiUtils.drawItemStack(mc.getItemRenderer(), parent.zLevel, parent, stack, offsetX + 1, offsetY + 1);
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
			return stack.getDisplayName().getUnformattedComponentText().toLowerCase().contains(search)
					|| stack.getItem().getDefaultInstance().getDisplayName().getUnformattedComponentText().toLowerCase()
							.contains(search);
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			if (GuiUtils.isHover(0, 0, 18, 18, mouseX, mouseY)) {
				playClick();
				if (mouseButton == 0) {
					if (InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
						int i = parent.elements.indexOf(this);
						parent.addListElement(i, new MenuListElement(parent, stack.copy()));
					} else
						mc.displayGuiScreen(new GuiGiver(parent, stack, s -> {
							ItemStack is = ItemUtils.getFromGiveCode(s);
							if (is != null)
								stack = is;
							else
								parent.removeListElement(this);
						}, true));
				} else if (mouseButton == 1)
					if (InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
						parent.removeListElement(this);
					else
						ItemUtils.give(stack);
			}
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	private Consumer<ItemStack> ADD_STACK = is -> {
		if (is != null)
			addListElement(elements.size() - 1, new MenuListElement(this, is));
	};

	private Runnable ADD = () -> {
		mc.displayGuiScreen(new GuiTypeListSelector(this, is -> {
			GuiGiver giver = new GuiGiver(this, (ItemStack) null,
					i -> ADD_STACK
							.accept(ItemUtils.getFromGiveCode(i.replaceAll("&", String.valueOf(ChatUtils.MODIFIER)))),
					false);
			if (mc.currentScreen instanceof GuiTypeListSelector)
				((GuiTypeListSelector) mc.currentScreen).setParent(giver);
			giver.setPreText(ItemUtils.getCustomTag(is, ACTMod.TEMPLATE_TAG_NAME, ""));
			return null;
		}, ACTMod.getTemplates()));
	};

	@SuppressWarnings("unchecked")
	public GuiMenu(GuiScreen parent) {
		super(parent, new ArrayList<>(), o -> {
		}, true, false, new Tuple[0]);
		Tuple<?, ?> btn1 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("cmd.act.edit"), new Tuple<>(() -> {
			final int slot = mc.player.inventory.currentItem;
			mc.displayGuiScreen(new GuiItemStackModifier(this, mc.player.getHeldItemMainhand().copy(),
					is -> ItemUtils.give(is, 36 + slot)));
		}, () -> {
		}));
		Tuple<?, ?> btn2 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("key.act.giver"),
				new Tuple<>(() -> Minecraft.getInstance().displayGuiScreen(new GuiGiver(this)), () -> {
				}));
		Tuple<?, ?> btn3 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("gui.act.config"),
				new Tuple<>(() -> mc.displayGuiScreen(new GuiConfig(this)), null));
		buttons = Minecraft.getInstance().player == null ? new Tuple[] { btn2, btn3 }
				: new Tuple[] { btn1, btn2, btn3 };
		elements.add(new ButtonElementList(24, 24, 20, 20, TextFormatting.GREEN + "+", ADD, null));
		ACTMod.getCustomItems().forEach(data -> ADD_STACK
				.accept(ItemUtils.getFromGiveCode(data.replaceAll("&", String.valueOf(ChatUtils.MODIFIER)))));
	}

	@Override
	protected Object get() {
		ACTMod.getCustomItems().clear();
		elements.stream().filter(le -> le instanceof MenuListElement)
				.forEach(m -> ACTMod.getCustomItems().add(ItemUtils.getGiveCode(((MenuListElement) m).stack)));
		ACTMod.saveConfigs();
		return null;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

}
