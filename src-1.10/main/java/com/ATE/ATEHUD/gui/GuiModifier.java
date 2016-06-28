package com.ATE.ATEHUD.gui;

import java.io.IOException;

import com.ATE.ATEHUD.utils.Chat;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiModifier extends GuiScreen {
	public GuiScreen Last;
	public ItemStack Value;
	public GuiTextField name,meta,item;
	public GuiButton change,done;
	public void updateScreen()
	{
		name.updateCursorCounter();
		meta.updateCursorCounter();
		item.updateCursorCounter();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		meta.drawTextBox();
		name.drawTextBox();
		item.drawTextBox();
		Value.setStackDisplayName(item.getText().replaceAll("&&", "\u00a7"));
		super.drawScreen(mouseX, mouseY, partialTicks);
		change.enabled=!item.getText().isEmpty();
	}
	public void initGui() {
		name=new GuiTextField(4, fontRendererObj, width/2-148, height/2-40, 296, 16);
		item=new GuiTextField(3, fontRendererObj, width/2-148, height/2-19, 296-30-fontRendererObj.getStringWidth(" : "), 16);
		meta=new GuiTextField(2, fontRendererObj, width/2+122, height/2-19, 26, 16);
		String str=Value.getDisplayName();
		str.replaceAll("§", "&&");
		buttonList.add(change=new GuiButton(0, width/2-150, height/2,149,20, I18n.format("gui.act.change")));
		buttonList.add(done=new GuiButton(1, width/2, height/2,150,20, I18n.format("gui.done")));
		name.setText(str);
		meta.setText(String.valueOf(Value.getMetadata()));
		item.setText(String.valueOf(Item.REGISTRY.getNameForObject(Value.getItem())).toString());
		super.initGui();
	}
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			if(mc.thePlayer.capabilities.isCreativeMode){
				mc.thePlayer.inventory.setItemStack(Value);
			}else{
				Chat.error(I18n.format("gui.act.nocreative"));
			}
			break;
		case 1:
			FMLClientHandler.instance().showGuiScreen(new  GuiNbtCode());
			break;
		}
		super.actionPerformed(button);
	}
	public GuiModifier(GuiScreen last,ItemStack value) {
		Last=last;
		Value=value;
	}
}
