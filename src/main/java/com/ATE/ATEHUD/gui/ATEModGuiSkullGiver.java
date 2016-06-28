package com.ATE.ATEHUD.gui;

import java.io.IOException;

import com.ATE.ATEHUD.Chat;
import com.ATE.ATEHUD.FakeItems2;
import com.ATE.ATEHUD.FakeItems3;
import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.utils.GiveItem;
import com.ATE.ATEHUD.utils.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class ATEModGuiSkullGiver extends GuiScreen {
	private GuiScreen Last;
	private GuiButton done,give,add;
	public ATEModGuiSkullGiver(GuiScreen last){Last=last;}
	public ATEModGuiSkullGiver(){Last=null;}
	public GuiTextField name;
	public void updateScreen()
	{
		name.updateCursorCounter();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
		name.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		name.mouseClicked(x, y, btn);
		super.mouseClicked(x, y, btn);
	}
	public void initGui() {
		give=new GuiButton(1, width/2-100, height/2,200,20, I18n.format("gui.act.give"));
		done=new GuiButton(2, width/2-100, height/2+42,200,20, I18n.format("gui.done"));
		add=new GuiButton(3, width/2-100, height/2+21,200,20, I18n.format("gui.act.add"));
		buttonList.add(done);
		buttonList.add(give);
		buttonList.add(add);
		name=new GuiTextField(1, fontRendererObj, width/2-100, height/2-22, 200, 20);
		super.initGui();
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		fontRendererObj.drawString(I18n.format("gui.act.skullfactory"), width/2-fontRendererObj.getStringWidth(I18n.format("gui.act.skullfactory"))/2, height/2-1-fontRendererObj.FONT_HEIGHT, Colors.GOLD);
		GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.fwf.name"), width/2-102, height/2-22, 20, Colors.GOLD);
		name.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(!mc.thePlayer.capabilities.isCreativeMode)GuiUtils.buttonHoverMessage(this, mc, give, mouseX, mouseY, fontRendererObj, new String[]{I18n.format("gui.act.nocreative")}, Colors.RED);
	}
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button==give)GiveItem.give(mc, FakeItems2.getHead(name.getText()));
		if(button==done)Minecraft.getMinecraft().displayGuiScreen(Last);
		if(button==add){
			ModMain.addConfig("HeadNames",name.getText());
			Chat.show(I18n.format("gui.act.add.msg"));
		}
		super.actionPerformed(button);
	}
}
