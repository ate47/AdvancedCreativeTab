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
import net.minecraft.client.gui.screen.Screen;
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
			GuiUtils.drawItemStack(mc.getItemRenderer(), parent, stack, offsetX + 1, offsetY + 1);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public void drawNext(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			if (GuiUtils.isHover(0, 0, 18, 18, mouseX, mouseY)) {
				GuiUtils.drawRect(offsetX, offsetY, offsetX + 18, offsetY + 18, 0x55cccccc);
				parent.renderTooltip(stack, mouseX + offsetX, mouseY + offsetY);
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
					if (ACTMod.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
						int i = parent.getElements().indexOf(this);
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
					if (ACTMod.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
						parent.removeListElement(this);
					else
						ItemUtils.give(stack);
			}
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	private Consumer<String> ADD_STACK = i -> {
		ItemStack is = ItemUtils.getFromGiveCode(i.replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
		if (is != null)
			addListElement(getElements().size() - 1, new MenuListElement(this, is));
		else
			ACTMod.LOGGER.warn("Menu - Can't parse : " + i);
	};

	private Runnable ADD = () -> {
		getMinecraft().displayGuiScreen(new GuiTypeListSelector(this, "gui.act.modifier.attr.type", is -> {
			GuiGiver giver = new GuiGiver(this, (ItemStack) null, i -> ADD_STACK.accept(i), false);
			if (getMinecraft().currentScreen instanceof GuiTypeListSelector)
				((GuiTypeListSelector) getMinecraft().currentScreen).setParent(giver);
			giver.setPreText(ItemUtils.getCustomTag(is, ACTMod.TEMPLATE_TAG_NAME, ""));
			return null;
		}, ACTMod.getTemplates()));
	};

	@SuppressWarnings("unchecked")
	public GuiMenu(Screen parent) {
		super(parent, "gui.act.menu", new ArrayList<>(), o -> {
		}, true, false, new Tuple[0]);
		Tuple<?, ?> btn1 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("cmd.act.edit"), new Tuple<>(() -> {
			final int slot = getMinecraft().player.inventory.currentItem;
			getMinecraft().displayGuiScreen(new GuiItemStackModifier(this,
					getMinecraft().player.getHeldItemMainhand().copy(), is -> ItemUtils.give(is, 36 + slot)));
		}, () -> {
		}));
		Tuple<?, ?> btn2 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("key.act.giver"),
				new Tuple<>(() -> Minecraft.getInstance().displayGuiScreen(new GuiGiver(this)), () -> {
				}));
		buttons = Minecraft.getInstance().player == null ? new Tuple[] { btn2 } : new Tuple[] { btn1, btn2 };
		addListElement(new ButtonElementList(24, 24, 20, 20, TextFormatting.GREEN + "+", ADD, null));
		ACTMod.getCustomItems().forEach(ADD_STACK::accept);
	}

	@Override
	protected Object get() {
		ACTMod.getCustomItems().clear();
		getElements().stream().filter(le -> le instanceof MenuListElement)
				.forEach(m -> ACTMod.saveItem(ItemUtils.getGiveCode(((MenuListElement) m).stack)));
		ACTMod.saveConfigs();
		return null;
	}
	
	@Override
	public void onClose() {
		get();
		super.onClose();
	}

}
