package fr.atesab.act.gui.modifier;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.gui.GuiGiver;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.ItemUtils.ContainerData;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GuiContainerModifier extends GuiModifier<ContainerData> {
    private ContainerData data;
    private Component title;

    public GuiContainerModifier(Screen parent, Component title, Consumer<ContainerData> setter, ContainerData data) {
        super(parent, new TranslatableComponent("gui.act.modifier.inventory"), setter);
        this.data = data.copy();
        this.title = title;
    }

    @Override
    protected void init() {
        addRenderableWidget(
                new Button(width / 2 - 96, height / 2 + 60, 94, 20, new TranslatableComponent("gui.done"), b -> {
                    set(data);
                    mc.setScreen(parent);
                }));
        addRenderableWidget(
                new Button(width / 2 + 2, height / 2 + 60, 94, 20, new TranslatableComponent("gui.cancel"), b -> {
                    mc.setScreen(parent);
                }));
        super.init();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        var size = data.size();
        var stacks = data.stacks();
        var cy = height / 2 - size.sizeY() * 18 / 2;
        var cx = width / 2 - size.sizeX() * 18 / 2;

        GuiUtils.drawRect(stack, width / 2 - 104, height / 2 - 80, width / 2 + 104, height / 2 + 84,
                GuiUtils.COLOR_CONTAINER_BORDER | 0xFF000000);

        GuiUtils.drawCenterString(font, title.getString(), width / 2, height / 2 - 76, 0xFF7F7F7F);

        ItemStack hoverStack = null;

        var ir = minecraft.getItemRenderer();
        for (var j = 0; j < size.sizeY(); j++) {
            for (var i = 0; i < size.sizeX(); i++) {
                var slot = size.indexOf(i, j);
                var item = stacks.get(slot);
                var sx = cx + 18 * i + 1;
                GuiUtils.drawRect(stack, sx, cy + 1, sx + 16, cy + 1 + 16, GuiUtils.COLOR_CONTAINER_SLOT | 0xFF000000);
                ir.renderGuiItem(item, sx, cy + 1);
                ir.renderGuiItemDecorations(font, item, sx, cy + 1);
                if (GuiUtils.isHover(sx, cy + 1, 16, 16, (int) mouseX, (int) mouseY)) {
                    GuiUtils.drawRect(stack, sx, cy, sx + 18, cy + 18, GuiUtils.COLOR_CONTAINER_SLOT | 0x66000000);
                    hoverStack = item;
                }
            }
            cy += 18;
        }
        super.render(stack, mouseX, mouseY, delta);
        if (hoverStack != null && hoverStack.getItem() != Items.AIR) {
            stack.pushPose();
            stack.translate(0.0D, 0.0D, 400.0D);
            renderTooltip(stack, hoverStack, mouseX, mouseY);
            stack.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int delta) {
        var size = data.size();
        var stacks = data.stacks();
        var cy = height / 2 - size.sizeY() * 18 / 2;
        var cx = width / 2 - size.sizeX() * 18 / 2;

        for (var j = 0; j < size.sizeY(); j++) {
            for (var i = 0; i < size.sizeX(); i++) {
                var slot = size.indexOf(i, j);
                var item = stacks.get(slot);
                var sx = cx + 18 * i + 1;
                if (GuiUtils.isHover(sx, cy + 1, 16, 16, (int) mouseX, (int) mouseY)) {
                    playClick();
                    mc.setScreen(new GuiGiver(this, item, code -> {
                        var codeStack = ItemUtils.getFromGiveCode(code);
                        stacks.set(slot, codeStack == null ? ItemUtils.air() : codeStack);
                    }, true));
                    return true;
                }
            }
            cy += 18;
        }

        return super.mouseClicked(mouseX, mouseY, delta);
    }

    @Override
    protected void generateDev(List<ACTDevInfo> entries, int mouseX, int mouseY) {
        var size = data.size();
        entries.add(devInfo("Inventory", "(W,H) = (" + size.sizeX() + ", " + size.sizeY() + ")"));
        super.generateDev(entries, mouseX, mouseY);
    }
}
