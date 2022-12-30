package fr.atesab.act.gui.modifier;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.act.gui.components.ACTButton;
import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.awt.*;
import java.util.function.Consumer;

public class GuiCommandBlockModifier extends GuiModifier<ItemStack> {
    private ItemStack stack;
    private EditBox command, name;
    private Button auto;
    private boolean autoValue;

    public GuiCommandBlockModifier(Screen parent, Consumer<ItemStack> setter, ItemStack stack) {
        super(parent, Component.translatable("gui.act.modifier.meta.command"), setter);
        this.stack = stack.copy();
    }

    private void setData() {
        CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
        stack.setHoverName(Component.literal(name.getValue().isEmpty() ? "@"
                : name.getValue().replaceAll("&", "" + ChatUtils.MODIFIER) + ChatFormatting.RESET));
        tag.putString("Command", command.getValue().replaceAll("&", "" + ChatUtils.MODIFIER));
        tag.putByte("auto", (byte) (autoValue ? 1 : 0));
    }

    private void loadData() {
        CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
        name.setValue((stack.hasCustomHoverName() ? stack.getHoverName().getString() : "@")
                .replaceAll("" + ChatUtils.MODIFIER, "&"));
        command.setValue(
                (tag.contains("Command", 8) ? tag.getString("Command") : "").replaceAll("" + ChatUtils.MODIFIER, "&"));
        autoValue = tag.contains("auto", 99) && tag.getByte("auto") == (byte) 1;
    }

    @Override
    public void init() {
        int l = Math.max(font.width(I18n.get("gui.act.modifier.meta.command.cmd") + " : "),
                font.width(I18n.get("gui.act.modifier.meta.command.name") + " : ")) + 5;
        name = new EditBox(font, width / 2 - 148 + l, height / 2 - 19, 296 - l, 16, Component.literal(""));
        command = new EditBox(font, width / 2 - 148 + l, height / 2 + 2, 296 - l, 16, Component.literal(""));
        name.setMaxLength(Integer.MAX_VALUE);
        command.setMaxLength(Integer.MAX_VALUE);
        auto = addRenderableWidget(new ACTButton(width / 2 + 1, height / 2 + 21, 149, 20,
                Component.translatable("advMode.mode.redstoneTriggered"), b -> autoValue = !autoValue));
        addRenderableWidget(new ACTButton(width / 2 - 150, height / 2 + 21, 150, 20,
                Component.translatable("gui.act.modifier.type"), b -> {
            setData();
            NonNullList<ItemStack> potionType = NonNullList.create();
            potionType.add(new ItemStack(Blocks.COMMAND_BLOCK));
            potionType.add(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK));
            potionType.add(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK));
            potionType.add(new ItemStack(Items.COMMAND_BLOCK_MINECART));
            getMinecraft().setScreen(new GuiTypeListSelector(GuiCommandBlockModifier.this,
                    Component.translatable("gui.act.modifier.type"), is -> {
                stack = ItemUtils.setItem(is.getItem(), stack);
                return null;
            }, potionType));
        }));
        addRenderableWidget(new ACTButton(width / 2 + 1, height / 2 + 42, 149, 20,
                Component.translatable("gui.act.cancel"), b -> getMinecraft().setScreen(parent)));
        addRenderableWidget(
                new ACTButton(width / 2 - 150, height / 2 + 42, 150, 20, Component.translatable("gui.done"), b -> {
                    setData();
                    set(stack);
                    getMinecraft().setScreen(parent);
                }));
        loadData();
        super.init();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        GuiUtils.drawString(font, I18n.get("gui.act.modifier.meta.command.cmd") + " : ", width / 2 - 150, command.y,
                Color.WHITE.getRGB(), command.getHeight());
        GuiUtils.drawString(font, I18n.get("gui.act.modifier.meta.command.name") + " : ", width / 2 - 150, name.y,
                Color.WHITE.getRGB(), name.getHeight());
        command.render(matrixStack, mouseX, mouseY, partialTicks);
        name.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        GuiUtils.drawItemStack(itemRenderer, this, stack, width / 2 - 10, name.y - 20);
        if (GuiUtils.isHover(width / 2 - 10, name.y - 20, 20, 20, mouseX, mouseY))
            renderTooltip(matrixStack, stack, mouseX, mouseY);
    }

    @Override
    public void tick() {
        command.tick();
        name.tick();
        auto.setFGColor(GuiUtils.getRedGreen(!autoValue));
        super.tick();
    }

    @Override
    public boolean charTyped(char key, int modifiers) {
        return command.charTyped(key, modifiers) || name.charTyped(key, modifiers) || super.charTyped(key, modifiers);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        command.keyPressed(key, scanCode, modifiers);
        name.keyPressed(key, scanCode, modifiers);
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        command.mouseClicked(mouseX, mouseY, mouseButton);
        name.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1) {
            if (GuiUtils.isHover(command, (int) mouseX, (int) mouseY))
                command.setValue("");
            else if (GuiUtils.isHover(name, (int) mouseX, (int) mouseY))
                name.setValue("");
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
