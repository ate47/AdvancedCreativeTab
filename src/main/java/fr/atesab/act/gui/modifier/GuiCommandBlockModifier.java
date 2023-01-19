package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiCommandBlockModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;
	private GuiTextField command, name;
	private GuiButton auto;
	private boolean autoValue;

	public GuiCommandBlockModifier(GuiScreen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, setter);
		this.stack = stack.copy();
	}

	private void setData() {
		NBTTagCompound tag = ItemUtils.getOrCreateSubCompound(stack, "BlockEntityTag");
		tag.setString("CustomName",
				name.getText().isEmpty() ? "@" : name.getText().replaceAll("&", "" + ChatUtils.MODIFIER));
		tag.setString("Command", command.getText().replaceAll("&", "" + ChatUtils.MODIFIER));
		tag.setByte("auto", (byte) (autoValue ? 1 : 0));
	}

	private void loadData() {
		NBTTagCompound tag = ItemUtils.getOrCreateSubCompound(stack, "BlockEntityTag");
		name.setText((tag.hasKey("CustomName", 8) ? tag.getString("CustomName") : "@")
				.replaceAll("" + ChatUtils.MODIFIER, "&"));
		command.setText(
				(tag.hasKey("Command", 8) ? tag.getString("Command") : "").replaceAll("" + ChatUtils.MODIFIER, "&"));
		autoValue = tag.hasKey("auto", 99) && tag.getByte("auto") == (byte) 1;
	}

	@Override
	public void initGui() {
		int l = Math.max(fontRendererObj.getStringWidth(I18n.format("gui.act.modifier.meta.command.cmd") + " : "),
				fontRendererObj.getStringWidth(I18n.format("gui.act.modifier.meta.command.name") + " : ")) + 5;
		name = new GuiTextField(0, fontRendererObj, width / 2 - 148 + l, height / 2 - 19, 296 - l, 16);
		command = new GuiTextField(0, fontRendererObj, width / 2 - 148 + l, height / 2 + 2, 296 - l, 16);
		name.setMaxStringLength(Integer.MAX_VALUE);
		command.setMaxStringLength(Integer.MAX_VALUE);
		buttonList.add(auto = new GuiButton(2, width / 2 + 1, height / 2 + 21, 149, 20,
				I18n.format("advMode.mode.redstoneTriggered")));
		buttonList
				.add(new GuiButton(3, width / 2 - 150, height / 2 + 21, 150, 20, I18n.format("gui.act.modifier.type")));
		buttonList.add(new GuiButton(1, width / 2 + 1, height / 2 + 42, 149, 20, I18n.format("gui.act.cancel")));
		buttonList.add(new GuiButton(0, width / 2 - 150, height / 2 + 42, 150, 20, I18n.format("gui.done")));
		loadData();
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			setData();
			set(stack);
		case 1:
			mc.displayGuiScreen(parent);
			break;
		case 2:
			autoValue = !autoValue;
			break;
		case 3:
			setData();
			List<ItemStack> potionType = new ArrayList<>();
			potionType.add(new ItemStack(Item.getItemFromBlock(Blocks.command_block)));
			potionType.add(new ItemStack(Items.command_block_minecart));
			mc.displayGuiScreen(new GuiTypeListSelector(this, is -> {
				stack = ItemUtils.setItem(is.getItem(), stack);
				return null;
			}, potionType));
			break;
		}
		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GuiUtils.drawString(fontRendererObj, I18n.format("gui.act.modifier.meta.command.cmd") + " : ", width / 2 - 150,
				command.yPosition, Color.WHITE.getRGB(), command.height);
		GuiUtils.drawString(fontRendererObj, I18n.format("gui.act.modifier.meta.command.name") + " : ", width / 2 - 150,
				name.yPosition, Color.WHITE.getRGB(), name.height);
		command.drawTextBox();
		name.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GuiUtils.drawItemStack(itemRender, zLevel, this, stack, width / 2 - 10, name.yPosition - 20);
		if (GuiUtils.isHover(width / 2 - 10, name.yPosition - 20, 20, 20, mouseX, mouseY))
			renderToolTip(stack, mouseX, mouseY);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
	}

	@Override
	public void updateScreen() {
		command.updateCursorCounter();
		name.updateCursorCounter();
		auto.packedFGColour = GuiUtils.getRedGreen(!autoValue);
		super.updateScreen();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		command.textboxKeyTyped(typedChar, keyCode);
		name.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		command.mouseClicked(mouseX, mouseY, mouseButton);
		name.mouseClicked(mouseX, mouseY, mouseButton);
		if (GuiUtils.isHover(command, mouseX, mouseY) && mouseButton == 1)
			command.setText("");
		if (GuiUtils.isHover(name, mouseX, mouseY) && mouseButton == 1)
			name.setText("");
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

}
