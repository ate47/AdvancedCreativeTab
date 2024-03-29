package fr.atesab.act.gui.modifier;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.act.gui.GuiValueButton;
import fr.atesab.act.gui.components.ACTButton;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class GuiStringArrayModifier extends GuiModifier<String[]> {
    private final List<String> values;
    private EditBox[] tfs;
    private Button next, last;
    private GuiValueButton<Integer>[] btsDel, btsAdd;
    private int elms;
    private int page = 0;

    public GuiStringArrayModifier(Screen parent, Component name, String[] values, Consumer<String[]> setter) {
        super(parent, name, setter);
        this.values = new ArrayList<>();
        for (String v : values)
            this.values.add(v.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&"));
    }

    @SuppressWarnings("unchecked")
    private void defineMenu() {
        clearWidgets();
        addRenderableWidget(
                new ACTButton(width / 2 - 100, height - 21, 100, 20, Component.translatable("gui.done"), b -> {
                    String[] result = new String[values.size()];
                    for (int i = 0; i < result.length; i++)
                        result[i] = values.get(i).replaceAll("&", String.valueOf(ChatUtils.MODIFIER));
                    set(result);
                    mc.setScreen(parent);
                }));
        addRenderableWidget(new ACTButton(width / 2 + 1, height - 21, 99, 20, Component.translatable("gui.act.cancel"),
                b -> mc.setScreen(parent)));
        addRenderableWidget(last = new ACTButton(width / 2 - 121, height - 21, 20, 20, Component.literal("<-"), b -> {
            page--;
            b.active = page != 0;
            next.active = page + 1 <= values.size() / elms;
        }) {

            @Override
            protected MutableComponent createNarrationMessage() {
                return Component.translatable("gui.narrate.button", I18n.get("gui.act.leftArrow"));
            }

        });
        addRenderableWidget(next = new ACTButton(width / 2 + 101, height - 21, 20, 20, Component.literal("->"), b -> {
            page++;
            last.active = page != 0;
            b.active = page + 1 <= values.size() / elms;
        }) {

            @Override
            protected MutableComponent createNarrationMessage() {
                return Component.translatable("gui.narrate.button", I18n.get("gui.act.rightArrow"));
            }
        });
        last.active = page != 0;
        next.active = page + 1 <= values.size() / elms;
        tfs = new EditBox[values.size()];
        btsDel = new GuiValueButton[values.size()];
        btsAdd = new GuiValueButton[values.size() + 1];

        int i;
        for (i = 0; i < values.size(); i++) {
            tfs[i] = new EditBox(font, width / 2 - 178, 21 + 21 * i % (elms * 21) + 2, 340, 16, Component.literal(""));
            tfs[i].setMaxLength(Integer.MAX_VALUE);
            tfs[i].setValue(values.get(i));
            btsDel[i] =

                    addRenderableWidget(new GuiValueButton<Integer>(width / 2 + 165, 21 + 21 * i % (elms * 21), 20, 20,
                            Component.literal("-"), i, b -> {
                        values.remove(b.getValue().intValue());
                        defineMenu();
                    }) {

                        @Override
                        protected MutableComponent createNarrationMessage() {
                            return Component.translatable("gui.narrate.button", I18n.get("gui.act.delete"));
                        }
                    });
            btsDel[i].setFGColor(Objects.requireNonNull(ChatFormatting.RED.getColor()));
            btsAdd[i] = addRenderableWidget(new GuiValueButton<Integer>(width / 2 + 187, 21 + 21 * i % (elms * 21), 20,
                    20, Component.literal("+"), i, b -> {
                values.add(Objects.requireNonNull(b.getValue()), "");
                defineMenu();
            }) {

                @Override
                protected MutableComponent createNarrationMessage() {
                    return Component.translatable("gui.narrate.button", I18n.get("gui.act.new"));
                }

            });
            btsAdd[i].setFGColor(Objects.requireNonNull(ChatFormatting.GREEN.getColor()));
            addWidget(tfs[i]);
        }
        btsAdd[i] = addRenderableWidget(new GuiValueButton<Integer>(width / 2 - 100, 21 + 21 * i % (elms * 21), 200, 20,
                Component.literal("+"), i, b -> {
            values.add(Objects.requireNonNull(b.getValue()), "");
            defineMenu();
        }) {

            @Override
            protected MutableComponent createNarrationMessage() {
                return Component.translatable("gui.narrate.button", I18n.get("gui.act.new"));
            }

        });
        btsAdd[i].setFGColor(Objects.requireNonNull(ChatFormatting.GREEN.getColor()));

    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        for (int i = page * elms; i < (page + 1) * elms && i < tfs.length; i++) {
            EditBox tf = tfs[i];
            GuiUtils.drawRightString(font, i + " : ", tf.getX(), tf.getY(), Objects.requireNonNull(ChatFormatting.WHITE.getColor()), tf.getHeight());
            tf.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void init() {
        elms = (height - 42) / 21;
        defineMenu();
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
            tfs[i].setFocus(false);
            if (mouseButton == 1 && GuiUtils.isHover(tfs[i], (int) mouseX, (int) mouseY)) {
                tfs[i].setValue("");
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void tick() {
        for (int i = page * elms; i < (page + 1) * elms && i < values.size(); i++) {
            values.set(i, tfs[i].getValue());
        }
        for (int i = 0; i < btsAdd.length; i++)
            if (i < (page + 1) * elms && i >= page * elms) {
                if (btsAdd[i] != null)
                    btsAdd[i].visible = true;
                if (i < btsDel.length && btsDel[i] != null)
                    btsDel[i].visible = true;
            } else {
                if (btsAdd[i] != null)
                    btsAdd[i].visible = false;
                if (i < btsDel.length && btsDel[i] != null)
                    btsDel[i].visible = false;
            }
        super.tick();
    }
}
