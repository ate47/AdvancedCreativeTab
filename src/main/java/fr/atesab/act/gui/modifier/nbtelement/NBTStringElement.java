package fr.atesab.act.gui.modifier.nbtelement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.ACTMod;
import fr.atesab.act.StringModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;

public class NBTStringElement extends NBTElement {
	private String value;
	private TextFieldWidget field;

	public NBTStringElement(GuiListModifier<?> parent, String key, String value) {
		super(parent, key, 200, 42);
		this.value = value.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&");
		fieldList.add(field = new TextFieldWidget(fontRenderer, 2, 2, 196, 16, ""));
		buttonList.add(new Button(0, 21, 200, 20, I18n.format("gui.act.modifier.tag.editor.string.data"), b -> {
			// Create a selector to select category or root modifier
			GuiButtonListSelector<Supplier<Screen>> root = new GuiButtonListSelector<>(parent, null, sup -> sup.get());
			List<Tuple<String, Supplier<Screen>>> rootButtons = new ArrayList<>();
			ACTMod.getStringModifier().keySet().stream().filter(k -> !k.isEmpty()).forEach(k -> {
				Map<String, Consumer<StringModifier>> categoryModifiers = ACTMod.getStringModifier().get(k);
				rootButtons
						.add(new Tuple<String, Supplier<Screen>>(I18n.format("gui.act.modifier.string." + k), () -> {
							// Create a selector to select between modifier in this category
							GuiButtonListSelector<Supplier<Screen>> categorySelector = new GuiButtonListSelector<>(
									root, null, sup -> sup.get());
							List<Tuple<String, Supplier<Screen>>> categorySelectorButtons = new ArrayList<>();
							categoryModifiers.keySet().forEach(modKey -> {
								categorySelectorButtons
										.add(new Tuple<String, Supplier<Screen>>(I18n.format(modKey), () -> {
											StringModifier stringModifier = new StringModifier(field.getText(), parent,
													s -> field.setText(NBTStringElement.this.value = s));
											categoryModifiers.get(modKey).accept(stringModifier);
											return stringModifier.getNextScreen();
										}));
							});
							categorySelector.setElements(categorySelectorButtons);
							return categorySelector;
						}));
			});
			// empty -> modifier at root
			if (ACTMod.getStringModifier().containsKey("")) {
				Map<String, Consumer<StringModifier>> rootModifiers = ACTMod.getStringModifier().get("");
				rootModifiers.keySet().forEach(modKey -> {
					rootButtons.add(new Tuple<String, Supplier<Screen>>(I18n.format(modKey), () -> {
						StringModifier stringModifier = new StringModifier(field.getText(), parent,
								s -> field.setText(NBTStringElement.this.value = s));
						rootModifiers.get(modKey).accept(stringModifier);
						return stringModifier.getNextScreen();
					}));
				});
			}
			root.setElements(rootButtons);
			mc.displayGuiScreen(root);
		}));
		field.setMaxStringLength(Integer.MAX_VALUE);
		field.setText(this.value);
	}

	@Override
	public NBTElement clone() {
		return new NBTStringElement(parent, key, value);
	}

	@Override
	public INBT get() {
		return new StringNBT(value.replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.string");
	}

	@Override
	public boolean match(String search) {
		return field.getText().toLowerCase().contains(search.toLowerCase()) || super.match(search);
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		if (super.charTyped(key, modifiers)) {
			value = field.getText();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (super.keyPressed(key, scanCode, modifiers)) {
			value = field.getText();
			return true;
		}
		return false;
	}

}