package fr.atesab.act.gui;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;

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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
		public void draw(MatrixStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY,
				float partialTicks) {
			GuiUtils.drawItemStack(mc.getItemRenderer(), parent, stack, offsetX + 1, offsetY + 1);
			super.draw(matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public void drawNext(MatrixStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY,
				float partialTicks) {
			if (GuiUtils.isHover(0, 0, 18, 18, mouseX, mouseY)) {
				GuiUtils.drawRect(matrixStack, offsetX, offsetY, offsetX + 18, offsetY + 18, 0x55cccccc);
				parent.renderTooltip(matrixStack, stack, mouseX + offsetX, mouseY + offsetY);
			}
			super.drawNext(matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		@Override
		public boolean match(String search) {
			search = search.toLowerCase();
			return stack.getDisplayName().getString().toLowerCase().contains(search)
					|| stack.getItem().getDefaultInstance().getDisplayName().getString().toLowerCase().contains(search);
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
						mc.setScreen(new GuiGiver(parent, stack, s -> {
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
		getMinecraft().setScreen(
				new GuiTypeListSelector(this, new TranslationTextComponent("gui.act.modifier.attr.type"), is -> {
					GuiGiver giver = new GuiGiver(this, (ItemStack) null, i -> ADD_STACK.accept(i), false);
					if (getMinecraft().screen instanceof GuiTypeListSelector)
						((GuiTypeListSelector) getMinecraft().screen).setParent(giver);
					giver.setPreText(ItemUtils.getCustomTag(is, ACTMod.TEMPLATE_TAG_NAME, ""));
					return null;
				}, ACTMod.getTemplates()));
	};

	@SuppressWarnings("unchecked")
	public GuiMenu(Screen parent) {
		super(parent, new TranslationTextComponent("gui.act.menu"), new ArrayList<>(), o -> {
		}, true, false, new Tuple[0]);
		Tuple<?, ?> btn1 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.get("cmd.act.edit"), new Tuple<>(() -> {
			final int slot = getMinecraft().player.inventory.selected;
			getMinecraft().setScreen(new GuiItemStackModifier(this, getMinecraft().player.getMainHandItem().copy(),
					is -> ItemUtils.give(is, 36 + slot)));
		}, () -> {
		}));
		Tuple<?, ?> btn2 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.get("key.act.giver"),
				new Tuple<>(() -> Minecraft.getInstance().setScreen(new GuiGiver(this)), () -> {
				}));

		Tuple<?, ?> btn3 = new Tuple<String, Tuple<Runnable, Runnable>>(I18n.get("gui.act.config"),
				new Tuple<>(() -> mc.setScreen(new GuiConfig(this)), null));
		buttons = Minecraft.getInstance().player == null ? new Tuple[] { btn2, btn3 }
				: new Tuple[] { btn1, btn2, btn3 };
		addListElement(new ButtonElementList(24, 24, 20, 20,
				new StringTextComponent("+").withStyle(TextFormatting.GREEN), ADD, null));
		ACTMod.getCustomItems().forEach(ADD_STACK::accept);
	}

	@Override
	protected Object get() {
		ACTMod.getCustomItems().clear();
		getElements().stream().filter(MenuListElement.class::isInstance)
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
