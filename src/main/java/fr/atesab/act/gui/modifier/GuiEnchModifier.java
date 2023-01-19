package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;

public class GuiEnchModifier extends GuiListModifier<List<Tuple<Enchantment, Integer>>> {

	static class EnchListElement extends ListElement {
		private Enchantment enchantment;
		private int level;
		private GuiTextField textField;
		private boolean err = false;

		public EnchListElement(Enchantment enchantment, int level) {
			super(200, 21);
			this.enchantment = enchantment;
			this.level = level;
			this.textField = new GuiTextField(0, fontRenderer, 112, 1, 46, 18);
			textField.setMaxStringLength(6);
			textField.setText(String.valueOf(level == 0 ? "" : level));
			buttonList.add(new GuiButton(0, 160, 0, 40, 20, I18n.format("gui.act.modifier.ench.max")));
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			if (button.id == 0) {
				textField.setText(String.valueOf(enchantment.getMaxLevel()));
			}
			super.actionPerformed(button);
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawRelative(textField, offsetX, offsetY);
			GuiUtils.drawRightString(fontRenderer,
					net.minecraft.util.text.translation.I18n.translateToLocal(enchantment.getName()) + " : ",
					offsetX + textField.x, offsetY + textField.y,
					(err ? Color.RED : level == 0 ? Color.GRAY : Color.WHITE).getRGB(), textField.height);
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		public void init() {
			textField.setFocused(false);
		}

		@Override
		public boolean isFocused() {
			return textField.isFocused();
		}

		@Override
		public void keyTyped(char typedChar, int keyCode) {
			textField.textboxKeyTyped(typedChar, keyCode);
			super.keyTyped(typedChar, keyCode);
		}

		@Override
		public boolean match(String search) {
			return net.minecraft.util.text.translation.I18n.translateToLocal(enchantment.getName()).toLowerCase()
					.contains(search.toLowerCase());
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			textField.mouseClicked(mouseX, mouseY, mouseButton);
			if (mouseButton == 1 && GuiUtils.isHover(textField, mouseX, mouseY))
				textField.setText("");
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override
		public void update() {
			textField.updateCursorCounter();
			try {
				level = textField.getText().isEmpty() ? 0 : Integer.valueOf(textField.getText());
				err = false;
			} catch (NumberFormatException e) {
				err = true;
			}
			super.update();
		}
	}

	public GuiEnchModifier(GuiScreen parent, List<Tuple<Enchantment, Integer>> ench,
			Consumer<List<Tuple<Enchantment, Integer>>> setter) {
		super(parent, new ArrayList<>(), setter);
		buttons = new Tuple[] { new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("gui.act.modifier.ench.max"),
				new Tuple<>(
						() -> elements.stream().map(le -> (EnchListElement) le)
								.forEach(ele -> ele.textField
										.setText(String.valueOf(ele.level = ele.enchantment.getMaxLevel()))),
						() -> elements.stream().map(le -> (EnchListElement) le).forEach(ele -> {
							ele.textField.setText("");
							ele.level = 0;
						}))) };
		ench.forEach(e -> elements.add(new EnchListElement(e.a, e.b)));

	}

	@Override
	protected List<Tuple<Enchantment, Integer>> get() {
		List<Tuple<Enchantment, Integer>> list = new ArrayList<>();
		elements.forEach(le -> {
			EnchListElement ele = ((EnchListElement) le);
			list.add(new Tuple<>(ele.enchantment, ele.level));
		});
		return list;
	}

}
