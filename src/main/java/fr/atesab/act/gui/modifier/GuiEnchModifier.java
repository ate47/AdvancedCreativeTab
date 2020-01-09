package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;

public class GuiEnchModifier extends GuiListModifier<List<Tuple<Enchantment, Integer>>> {

	static class EnchListElement extends ListElement {
		private Enchantment enchantment;
		private int level;
		private TextFieldWidget textField;
		private boolean err = false;

		public EnchListElement(Enchantment enchantment, int level) {
			super(200, 21);
			this.enchantment = enchantment;
			this.level = level;
			this.textField = new TextFieldWidget(font, 112, 1, 46, 18, "");
			textField.setMaxStringLength(6);
			textField.setText(String.valueOf(level == 0 ? "" : level));
			buttonList.add(new Button(160, 0, 40, 20, I18n.format("gui.act.modifier.ench.max"), b -> {
				textField.setText(String.valueOf(enchantment.getMaxLevel()));
			}));
		}

		@Override
		public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
			GuiUtils.drawRelative(textField, offsetX, offsetY, mouseY, mouseY, partialTicks);
			GuiUtils.drawRightString(font, I18n.format(enchantment.getName()) + " : ", offsetX + textField.x,
					offsetY + textField.y, (err ? Color.RED : level == 0 ? Color.GRAY : Color.WHITE).getRGB(),
					textField.getHeight());
			super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
		}

		public void init() {
			textField.setFocused2(false);
		}

		@Override
		public boolean isFocused() {
			return textField.isFocused();
		}

		@Override
		public boolean charTyped(char key, int modifiers) {
			return textField.charTyped(key, modifiers) || super.charTyped(key, modifiers);
		}

		@Override
		public boolean keyPressed(int key, int scanCode, int modifiers) {
			return textField.keyPressed(key, scanCode, modifiers) || super.keyPressed(key, scanCode, modifiers);
		}

		@Override
		public boolean match(String search) {
			return I18n.format(enchantment.getName()).toLowerCase().contains(search.toLowerCase());
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
			textField.tick();
			try {
				level = textField.getText().isEmpty() ? 0 : Integer.valueOf(textField.getText());
				err = false;
			} catch (NumberFormatException e) {
				err = true;
			}
			super.update();
		}
	}

	@SuppressWarnings("unchecked")
	public GuiEnchModifier(Screen parent, List<Tuple<Enchantment, Integer>> ench,
			Consumer<List<Tuple<Enchantment, Integer>>> setter) {
		super(parent, "gui.act.modifier.ench", new ArrayList<>(), setter, null);
		buttons = new Tuple[] { new Tuple<String, Tuple<Runnable, Runnable>>(I18n.format("gui.act.modifier.ench.max"),
				new Tuple<>(
						() -> getElements().stream().map(le -> (EnchListElement) le)
								.forEach(ele -> ele.textField
										.setText(String.valueOf(ele.level = ele.enchantment.getMaxLevel()))),
						() -> getElements().stream().map(le -> (EnchListElement) le).forEach(ele -> {
							ele.textField.setText("");
							ele.level = 0;
						}))) };
		ench.forEach(e -> addListElement(new EnchListElement(e.a, e.b)));

	}

	@Override
	protected List<Tuple<Enchantment, Integer>> get() {
		List<Tuple<Enchantment, Integer>> list = new ArrayList<>();
		getElements().forEach(le -> {
			EnchListElement ele = ((EnchListElement) le);
			list.add(new Tuple<>(ele.enchantment, ele.level));
		});
		return list;
	}

}
