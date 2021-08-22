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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GuiContainerModifier extends GuiModifier<ContainerData> {
    private ContainerData data;

    public GuiContainerModifier(Screen parent, Consumer<ContainerData> setter, ContainerData data) {
        super(parent, new TranslatableComponent("gui.act.modifier.inventory"), setter);
        this.data = data.copy();
    }

    @Override
    protected void init() {
        addRenderableWidget(
                new Button(width / 2 - 200, height / 2 + 80, 198, 20, new TranslatableComponent("gui.done"), b -> {
                    set(data);
                    mc.setScreen(parent);
                }));
        addRenderableWidget(
                new Button(width / 2 + 2, height / 2 + 80, 198, 20, new TranslatableComponent("gui.cancel"), b -> {
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

        GuiUtils.drawRect(stack, cx - 1, cy - 1, cx + size.sizeX() * 18 + 1, cy + size.sizeY() * 18 + 1, 0xFFDADADA);

        ItemStack hoverStack = null;

        var ir = minecraft.getItemRenderer();
        for (var j = 0; j < size.sizeY(); j++) {
            for (var i = 0; i < size.sizeX(); i++) {
                var slot = size.indexOf(i, j);
                var item = stacks.get(slot);
                var sx = cx + 18 * i + 1;
                GuiUtils.drawRect(stack, sx, cy + 1, sx + 16, cy + 1 + 16, 0xFFC2C2C2);
                ir.renderGuiItem(item, sx, cy + 1);
                ir.renderGuiItemDecorations(font, item, sx, cy + 1);
                if (GuiUtils.isHover(sx, cy + 1, 16, 16, (int) mouseX, (int) mouseY)) {
                    GuiUtils.drawRect(stack, sx, cy + 1, sx + 16, cy + 1 + 16, 0x22DADADA);
                    hoverStack = item;
                }
            }
            cy += 18;
        }
        super.render(stack, mouseX, mouseY, delta);
        if (hoverStack != null && hoverStack.getItem() != Items.AIR) {
            renderTooltip(stack, hoverStack, mouseX, mouseY);
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
                    mc.setScreen(new GuiGiver(this, item, stack -> {
                        stacks.set(slot, item == null ? ItemUtils.air() : item);
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
        entries.add(devInfo("Inventory", "(" + size.sizeX() + "," + size.sizeY() + ")"));
        super.generateDev(entries, mouseX, mouseY);
    }
}
