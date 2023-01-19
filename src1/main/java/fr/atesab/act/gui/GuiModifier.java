package fr.atesab.act.gui;

import java.io.IOException;

import fr.atesab.act.utils.ChatUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiModifier extends GuiScreen {
	public GuiScreen parent;
	public ItemStack value;
	public GuiTextField name, meta, item;
	public GuiButton change, done;

	public GuiModifier(GuiScreen parent, ItemStack value) {
		this.parent = parent;
		this.value = value;
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			if (mc.player.capabilities.isCreativeMode) {
				mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(mc.player.inventory.currentItem, value));
			} else {
				ChatUtils.error(I18n.format("gui.act.nocreative"));
			}
			break;
		case 1:
			FMLClientHandler.instance().showGuiScreen(new GuiNbtCode(this));
			break;
		}
		super.actionPerformed(button);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		meta.drawTextBox();
		name.drawTextBox();
		item.drawTextBox();
		value.setStackDisplayName(item.getText().replaceAll("&&", String.valueOf(ChatUtils.MODIFIER)));
		super.drawScreen(mouseX, mouseY, partialTicks);
		change.enabled = !item.getText().isEmpty();
	}

	public void initGui() {
		name = new GuiTextField(4, fontRenderer, width / 2 - 148, height / 2 - 40, 296, 16);
		item = new GuiTextField(3, fontRenderer, width / 2 - 148, height / 2 - 19,
				296 - 30 - fontRenderer.getStringWidth(" : "), 16);
		meta = new GuiTextField(2, fontRenderer, width / 2 + 122, height / 2 - 19, 26, 16);
		String str = value.getDisplayName();
		str.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&&");
		buttonList.add(change = new GuiButton(0, width / 2 - 150, height / 2, 149, 20, I18n.format("gui.act.change")));
		buttonList.add(done = new GuiButton(1, width / 2, height / 2, 150, 20, I18n.format("gui.done")));
		name.setText(str);
		meta.setText(String.valueOf(value.getMetadata()));
		item.setText(String.valueOf(Item.REGISTRY.getNameForObject(value.getItem())).toString());
		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		name.textboxKeyTyped(par1, par2);
		meta.textboxKeyTyped(par1, par2);
		item.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		name.mouseClicked(x, y, btn);
		meta.mouseClicked(x, y, btn);
		item.mouseClicked(x, y, btn);
		super.mouseClicked(x, y, btn);
	}

	public void updateScreen() {
		name.updateCursorCounter();
		meta.updateCursorCounter();
		item.updateCursorCounter();
	}
}
