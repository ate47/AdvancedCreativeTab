package fr.atesab.act.gui.modifier;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.act.gui.components.ACTButton;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiEnchModifier extends GuiListModifier<List<Tuple<Enchantment, Integer>>> {

    static class EnchListElement extends ListElement {
        private final Enchantment enchantment;
        private int level;
        private final EditBox textField;
        private boolean err = false;

        public EnchListElement(Enchantment enchantment, int level) {
            super(200, 21);
            this.enchantment = enchantment;
            this.level = level;
            this.textField = new EditBox(font, 112, 1, 46, 18, Component.literal(""));
            textField.setMaxLength(6);
            textField.setValue(String.valueOf(level == 0 ? "" : level));
            buttonList.add(new ACTButton(160, 0, 40, 20, Component.translatable("gui.act.modifier.ench.max"), b -> textField.setValue(String.valueOf(enchantment.getMaxLevel()))));
        }

        @Override
        public void draw(PoseStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
            GuiUtils.drawRelative(matrixStack, textField, offsetX, offsetY, mouseX, mouseY, partialTicks);
            GuiUtils.drawRightString(font, I18n.get(enchantment.getDescriptionId()) + " : ", offsetX + textField.x,
                    offsetY + textField.y, (err ? Color.RED : level == 0 ? Color.GRAY : Color.WHITE).getRGB(),
                    textField.getHeight());
            super.draw(matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
        }

        public void init() {
            textField.setFocus(false);
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
            return I18n.get(enchantment.getDescriptionId()).toLowerCase().contains(search.toLowerCase());
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            textField.mouseClicked(mouseX, mouseY, mouseButton);
            if (mouseButton == 1 && GuiUtils.isHover(textField, mouseX, mouseY))
                textField.setValue("");
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public void update() {
            textField.tick();
            try {
                level = textField.getValue().isEmpty() ? 0 : Integer.parseInt(textField.getValue());
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
        super(parent, Component.translatable("gui.act.modifier.ench"), new ArrayList<>(), setter, null);
        buttons = new Tuple[]{new Tuple<String, Tuple<Runnable, Runnable>>(I18n.get("gui.act.modifier.ench.max"),
                new Tuple<>(
                        () -> getElements().stream().map(le -> (EnchListElement) le)
                                .forEach(ele -> ele.textField
                                        .setValue(String.valueOf(ele.level = ele.enchantment.getMaxLevel()))),
                        () -> getElements().stream().map(le -> (EnchListElement) le).forEach(ele -> {
                            ele.textField.setValue("");
                            ele.level = 0;
                        })))};
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
