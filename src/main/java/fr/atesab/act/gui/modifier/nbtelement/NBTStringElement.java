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
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagString;

public class NBTStringElement extends NBTElement {
	private String value;
	private GuiTextField field;

	public NBTStringElement(GuiListModifier<?> parent, String key, String value) {
		super(parent, key, 200, 42);
		this.value = value.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&");
		fieldList.add(field = new GuiTextField(0, fontRenderer, 2, 2, 196, 16));
		buttonList.add(new GuiButton(0, 0, 21, I18n.format("gui.act.modifier.tag.editor.string.data")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				// Create a selector to select category or root modifier
				GuiButtonListSelector<Supplier<GuiScreen>> root = new GuiButtonListSelector<>(parent, null,
						sup -> sup.get());
				List<Tuple<String, Supplier<GuiScreen>>> rootButtons = new ArrayList<>();
				ACTMod.getStringModifier().keySet().stream().filter(key -> !key.isEmpty()).forEach(key -> {
					Map<String, Consumer<StringModifier>> categoryModifiers = ACTMod.getStringModifier().get(key);
					rootButtons.add(new Tuple<String, Supplier<GuiScreen>>(
							I18n.format("gui.act.modifier.string." + key), () -> {
								// Create a selector to select between modifier in this category
								GuiButtonListSelector<Supplier<GuiScreen>> categorySelector = new GuiButtonListSelector<>(
										root, null, sup -> sup.get());
								List<Tuple<String, Supplier<GuiScreen>>> categorySelectorButtons = new ArrayList<>();
								categoryModifiers.keySet().forEach(modKey -> {
									categorySelectorButtons
											.add(new Tuple<String, Supplier<GuiScreen>>(I18n.format(modKey), () -> {
												StringModifier stringModifier = new StringModifier(field.getText(),
														parent, s -> field.setText(NBTStringElement.this.value = s));
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
						rootButtons.add(new Tuple<String, Supplier<GuiScreen>>(I18n.format(modKey), () -> {
							StringModifier stringModifier = new StringModifier(field.getText(), parent,
									s -> field.setText(NBTStringElement.this.value = s));
							rootModifiers.get(modKey).accept(stringModifier);
							return stringModifier.getNextScreen();
						}));
					});
				}
				root.setElements(rootButtons);
				mc.displayGuiScreen(root);
				super.onClick(mouseX, mouseY);
			}
		});
		field.setMaxStringLength(Integer.MAX_VALUE);
		field.setText(this.value);
	}

	@Override
	public NBTElement clone() {
		return new NBTStringElement(parent, key, value);
	}

	@Override
	public INBTBase get() {
		return new NBTTagString(value.replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
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