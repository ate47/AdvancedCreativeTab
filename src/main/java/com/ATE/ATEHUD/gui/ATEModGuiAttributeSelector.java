package com.ATE.ATEHUD.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Attribute;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.EnchantmentInfo;
import com.ATE.ATEHUD.superclass.Enchantments;
import com.ATE.ATEHUD.utils.GuiUtils;

public class ATEModGuiAttributeSelector extends GuiScreen{
	private boolean isAdvanced=false;
	private ATEModGuiItemFactory Last;
	private GuiButton bdone,bcancel;
	public static int buttonYESColor=Colors.LimeGreen,buttonNOColor=Colors.RED;
	private int preButton=10,elementSize=80;
	private Object[] list=new Object[]{};
	private GuiTextField[] amountList=new GuiTextField[]{},UUIDLeast=new GuiTextField[]{},UUIDMost=new GuiTextField[]{};
	private GuiButton[] operation0List=new GuiButton[]{}, operation1List=new GuiButton[]{};
	public void actionPerformed(GuiButton button){
		for (int i = 0; i <operation0List.length; i++) {
			if(operation0List[i]==button){
				operation0List[i].packedFGColour=buttonYESColor;
				operation1List[i].packedFGColour=buttonNOColor;
			}
			if(operation1List[i]==button){
				operation0List[i].packedFGColour=buttonNOColor;
				operation1List[i].packedFGColour=buttonYESColor;
			}
		}
		if(button==bdone){
			Attribute[] attriList=(Attribute[]) ModMain.copyOf(new Attribute[]{}, list.length, Attribute[].class);
			for (int i = 0; i < attriList.length; i++) {
				attriList[i]=null;
			}int j=0;
			for (int i = 0; i < list.length; i++) {
				//get Amount and operationType
				int Amount=0;
				if(!amountList[i].getText().isEmpty())
					try {
						Amount =Integer.valueOf(amountList[i].getText());
					} catch (Exception e) {}
				if(Amount!=0){
					int Operation=0;
					if(operation1List[i].packedFGColour==buttonYESColor)Operation=1;
					//get UUID information
					int UUIDLeast=Integer.valueOf((int) (1000000.0*Math.random()));
					if(!this.UUIDLeast[i].getText().isEmpty())
						try {
							UUIDLeast =Integer.valueOf(this.UUIDLeast[i].getText());
						} catch (Exception e) {}
					int UUIDMost=Integer.valueOf((int) (1000000.0*Math.random()));
					if(!this.UUIDMost[i].getText().isEmpty())
						try {
							UUIDMost = Integer.valueOf(this.UUIDMost[i].getText());
						} catch (Exception e) {}
					String[] strl=(String[])list[i];
					String str=I18n.format(strl[0]);
					Attribute attri=new Attribute(str,Operation, Amount, UUIDLeast, UUIDMost);
					attriList[j]=attri;
					j++;
				}
			}
			Last.attributes=attriList;
			mc.displayGuiScreen(Last);
		}
		if(button==bcancel)mc.displayGuiScreen(Last);
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		list=Attribute.AttributeList;
		int adv=0;if(isAdvanced)adv=1;
		elementSize=20+2+fontRendererObj.FONT_HEIGHT+adv*(20+2+fontRendererObj.FONT_HEIGHT);
		drawDefaultBackground();
		//Test if Amount is a integer or null and change textfield and title color
		for (int i = 0; i < amountList.length; i++) {
			String[] strl=(String[])list[i];
			String str=I18n.format(strl[1]);
			amountList[i].setTextColor(Colors.WHITE);
			int outColor=Colors.GRAY;
			if(!amountList[i].getText().isEmpty()){
				try {
					int a =Integer.valueOf(amountList[i].getText());
					outColor=Colors.WHITE;
				} catch (Exception e) {
					amountList[i].setTextColor(Colors.RED);
					outColor=Colors.DARK_RED;
				}
			}
			fontRendererObj.drawString(str, width/2-fontRendererObj.getStringWidth(str)/2, 25+i*elementSize, outColor);
		}
		String str="UUID",str2="Least = ",str3="Most = ";
		for (int i = 0; i < amountList.length; i++) {
			amountList[i].drawTextBox();
			GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.itemfactory.attribute.amount"), width/2-80, 25+fontRendererObj.FONT_HEIGHT+1+i*elementSize, 20, Colors.GOLD);
			
		}
		if(isAdvanced)for (int i = 0; i < UUIDLeast.length; i++) {
			fontRendererObj.drawString(str, width/2-fontRendererObj.getStringWidth(str)/2, 25+i*elementSize+fontRendererObj.FONT_HEIGHT+1+20, Colors.RED);
			GuiUtils.drawRightString(fontRendererObj, str2, width/2-80, 45+2*(fontRendererObj.FONT_HEIGHT+1)+i*elementSize, 20, Colors.RED);
			GuiUtils.drawRightString(fontRendererObj, str3, width/2+125, 45+2*(fontRendererObj.FONT_HEIGHT+1)+i*elementSize, 20, Colors.RED);
			UUIDLeast[i].drawTextBox();
			UUIDMost[i].drawTextBox();
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	public void updateScreen()
	{
		for (int i = 0; i < amountList.length; i++) {
			amountList[i].updateCursorCounter();
		}
		if(isAdvanced){
			for (int i = 0; i < UUIDLeast.length; i++) {
				UUIDLeast[i].updateCursorCounter();
			}
			for (int i = 0; i < UUIDMost.length; i++) {
				UUIDMost[i].updateCursorCounter();
			}
		}
		super.updateScreen();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
		for (int i = 0; i < amountList.length; i++) {
			amountList[i].textboxKeyTyped(par1, par2);
		}
		if(isAdvanced){
			for (int i = 0; i < UUIDLeast.length; i++) {
				UUIDLeast[i].textboxKeyTyped(par1, par2);
			}
			for (int i = 0; i < UUIDMost.length; i++) {
				UUIDMost[i].textboxKeyTyped(par1, par2);
			}
		}
		super.keyTyped(par1, par2);
	}
	protected void mouseClicked(int x, int y, int btn) throws IOException {

		for (int i = 0; i < amountList.length; i++) {
			amountList[i].mouseClicked(x, y, btn);
		}
		if(isAdvanced){
			for (int i = 0; i < UUIDLeast.length; i++) {
				UUIDLeast[i].mouseClicked(x, y, btn);
			}
			for (int i = 0; i < UUIDMost.length; i++) {
				UUIDMost[i].mouseClicked(x, y, btn);
			}			
		}
		super.mouseClicked(x, y, btn);
	}
	public void initGui() {
		list=Attribute.AttributeList;
		int adv=0;if(isAdvanced)adv=1;
		int tfid=0;
		elementSize=20+2+fontRendererObj.FONT_HEIGHT+adv*(20+2+fontRendererObj.FONT_HEIGHT);
		amountList=(GuiTextField[]) ModMain.copyOf(new GuiTextField[]{}, list.length, GuiTextField[].class);
		for (int i = 0; i < amountList.length; i++) {
			amountList[i]=new GuiTextField(tfid, fontRendererObj, width/2-80, 2+25+fontRendererObj.FONT_HEIGHT+1+i*elementSize, 75, 16);
		tfid++;}
		operation0List=(GuiButton[]) ModMain.copyOf(new GuiButton[]{}, list.length, GuiButton[].class);
		for (int i = 0; i < operation0List.length; i++) {
			operation0List[i]=new GuiButton(tfid, width/2, 25+fontRendererObj.FONT_HEIGHT+1+i*elementSize,124, 20, "+- Amount");
			operation0List[i].packedFGColour=buttonYESColor;
			buttonList.add(operation0List[i]);
		tfid++;}
		operation1List=(GuiButton[]) ModMain.copyOf(new GuiButton[]{}, list.length, GuiButton[].class);
		for (int i = 0; i < operation1List.length; i++) {
			operation1List[i]=new GuiButton(tfid, width/2+125, 25+fontRendererObj.FONT_HEIGHT+1+i*elementSize,124, 20, "+- Amount % (additive)");
			operation1List[i].packedFGColour=buttonNOColor;
			buttonList.add(operation1List[i]);
		tfid++;}
		UUIDLeast=(GuiTextField[]) ModMain.copyOf(new GuiTextField[]{}, list.length, GuiTextField[].class);
		for (int i = 0; i < amountList.length; i++) {
			UUIDLeast[i]=new GuiTextField(tfid, fontRendererObj, width/2-80, 2+45+2*(fontRendererObj.FONT_HEIGHT+1)+i*elementSize, 75, 16);
			UUIDLeast[i].setText(String.valueOf(Integer.valueOf((int) (1000000.0*Math.random()))));
		tfid++;}
		UUIDMost=(GuiTextField[]) ModMain.copyOf(new GuiTextField[]{}, list.length, GuiTextField[].class);
		for (int i = 0; i < amountList.length; i++) {
			UUIDMost[i]=new GuiTextField(tfid, fontRendererObj, width/2+125, 2+45+2*(fontRendererObj.FONT_HEIGHT+1)+i*elementSize, 75, 16);
			UUIDMost[i].setText(String.valueOf(Integer.valueOf((int) (1000000.0*Math.random()))));
		tfid++;}
		//re-enter values from Last...
		for (int i = 0; i < Last.attributes.length; i++) {
			//search the id j of attribute i
			if(Last.attributes[i]!=null){
				for (int j = 0; j < list.length; j++) {
					String[] strl=(String[])list[j];
					if(Last.attributes[i].Name.equals(strl[0])){
						if(Last.attributes[i].Operation==1){
							operation0List[j].packedFGColour=buttonNOColor;
							operation1List[j].packedFGColour=buttonYESColor;
						}else{
							operation0List[j].packedFGColour=buttonYESColor;
							operation1List[j].packedFGColour=buttonNOColor;
						}
						amountList[j].setText(String.valueOf((int) Last.attributes[i].Amount));
						UUIDLeast[j].setText(String.valueOf((int) Last.attributes[i].UUIDLeast));
						UUIDMost[j].setText(String.valueOf((int) Last.attributes[i].UUIDMost));
					}
				}
			}
		}
		bcancel=new GuiButton(5+list.length,width/2-150,2,149,20,I18n.format("gui.act.cancel"));
		bdone =new GuiButton(6+list.length,width/2+0,2,150,20,I18n.format("gui.done"));
		
		buttonList.add(bdone);
		buttonList.add(bcancel);
		super.initGui();
	}
	public ATEModGuiAttributeSelector(ATEModGuiItemFactory last){
		isAdvanced=ModMain.AdvancedModActived;
		Last=last;
	}
}