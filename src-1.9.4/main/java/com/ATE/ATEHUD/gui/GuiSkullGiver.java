package com.ATE.ATEHUD.gui;

import java.io.IOException;

import com.ATE.ATEHUD.FakeItems2;
import com.ATE.ATEHUD.FakeItems3;
import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.Head;
import com.ATE.ATEHUD.utils.Chat;
import com.ATE.ATEHUD.utils.GiveItem;
import com.ATE.ATEHUD.utils.GuiUtils;
import com.ATE.ATEHUD.utils.ItemStackGenHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class GuiSkullGiver extends GuiScreen {
	private GuiScreen Last;
	private GuiButton done,give,add,myhead,mylink;
	public GuiTextField name,adv_link;
	public GuiSkullGiver(GuiScreen last){
		Last=last;
		
	}
	public GuiSkullGiver(){
		Last=null;
		
	}
	public void updateScreen()
	{
		if(ModMain.AdvancedModActived)adv_link.updateCursorCounter();
		name.updateCursorCounter();
		super.updateScreen();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
		name.textboxKeyTyped(par1, par2);
		if(ModMain.AdvancedModActived)adv_link.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		name.mouseClicked(x, y, btn);
		if(ModMain.AdvancedModActived)adv_link.mouseClicked(x, y, btn);
		super.mouseClicked(x, y, btn);
	}
	public void initGui() {
		buttonList.add(done=new GuiButton(2, width/2-100, height/2+42,200,20, I18n.format("gui.done")));
		buttonList.add(give=new GuiButton(4, width/2, height/2+21,100,20, I18n.format("gui.act.give")));
		int adv=0;
		if(ModMain.AdvancedModActived){adv=1;
			adv_link=new GuiTextField(2, fontRendererObj, width/2-100, height/2-22, 200, 20);
			adv_link.setMaxStringLength(200);
			buttonList.add(mylink=new GuiButton(6, width/2, height/2,100,20, I18n.format("gui.act.give.mylink")));
		}
		buttonList.add(myhead=new GuiButton(1, width/2-100, height/2,(int)(200*(float)((1/2)*adv)-adv),20, I18n.format("gui.act.give.myhead")));
		buttonList.add(add=new GuiButton(3, width/2-100, height/2+21,99,20, I18n.format("gui.act.add")));
		int a=0;if(ModMain.AdvancedModActived)a=1;
		name=new GuiTextField(1, fontRendererObj, width/2-100, height/2-22-22*a, 200, 20);
		super.initGui();
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		fontRendererObj.drawString(I18n.format("gui.act.skullfactory"), width/2-fontRendererObj.getStringWidth(I18n.format("gui.act.skullfactory"))/2, height/2-1-fontRendererObj.FONT_HEIGHT, Colors.GOLD);
		int a=0;if(ModMain.AdvancedModActived)a=1;
		GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.fwf.name")+" : ", width/2-102, height/2-22-22*a, 20, Colors.GOLD);
		if(ModMain.AdvancedModActived)GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.skullfactory.link")+" : ", width/2-102, height/2-22, 20, Colors.GOLD);
		name.drawTextBox();
		if(ModMain.AdvancedModActived)adv_link.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(!mc.thePlayer.capabilities.isCreativeMode)GuiUtils.buttonHoverMessage(this, mc, give, mouseX, mouseY, fontRendererObj, new String[]{I18n.format("gui.act.nocreative")}, Colors.RED);
		if(ModMain.AdvancedModActived &&GuiUtils.isHover(width/2-102, height/2-22, 200, 20, mouseX, mouseY))GuiUtils.drawTextBox(this, mc, fontRendererObj, I18n.format("gui.act.skullfactory.help").split("::"), mouseX+5, mouseY+5, Colors.GREEN);
		give.enabled=!name.getText().isEmpty();
		add.enabled=!name.getText().isEmpty();
		if(ModMain.AdvancedModActived)mylink.enabled=!name.getText().isEmpty();
	}
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button==give){
			ItemStack is=new Head(name.getText()).getHead();
			if(ModMain.AdvancedModActived && !adv_link.getText().isEmpty()){
				is=ItemStackGenHelper.getCustomSkull(adv_link.getText(), name.getText());
			}
			GiveItem.give(mc, is);
		}
		if(button==done)Minecraft.getMinecraft().displayGuiScreen(Last);
		if(button==myhead){
			name.setText(Minecraft.getMinecraft().getSession().getUsername());
		}
		if(button==add){
			ModMain.addConfig("HeadNames",name.getText());
			Chat.show(I18n.format("gui.act.add.msg"));
		}
		if(button==mylink)this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, "skins.minecraft.net/MinecraftSkins/"+name.getText()+".png", 31102009, false));
		super.actionPerformed(button);
	}
}
