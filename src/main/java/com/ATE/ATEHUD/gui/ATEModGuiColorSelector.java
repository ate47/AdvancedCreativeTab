package com.ATE.ATEHUD.gui;

import java.io.IOException;

import com.ATE.ATEHUD.superclass.Colors;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

public class ATEModGuiColorSelector extends GuiScreen{
	private int color=Colors.WHITE;
	private int Index=0;
	private boolean FadeColor=false;
    private GuiScreen Last;
    private ATEModGuiExplosionSelector Editor;
    
    public GuiScreen getLastGui(){return Last;}
    public ATEModGuiExplosionSelector getEditor(){return Editor;}
	public int getColor(){return color;}
	
    private int nextGuiId=15;
    private int getNextGuiId() {
    	nextGuiId++;
    	return nextGuiId;
    }
	public ATEModGuiColorSelector(ATEModGuiExplosionSelector editor,GuiScreen lastGui, boolean fadeColor,int index,int value){
		super();
		if(!fadeColor){if(editor.getColorSize()>index && index>=0)Index=index;}
		else{if(editor.getFadeColorSize()>index && index>=0)Index=index;}
		color=value;
		FadeColor=fadeColor;
		Last=lastGui;
		Editor=editor;
	}
	public ATEModGuiColorSelector(ATEModGuiExplosionSelector editor,GuiScreen lastGui, boolean fadeColor,int index){
		if(!fadeColor){if(editor.getColorSize()>index && index>=0)Index=index;}
		else{if(editor.getFadeColorSize()>index && index>=0)Index=index;}
		color=Colors.WHITE;
		FadeColor=fadeColor;
		Last=lastGui;
		Editor=editor;
	}
	public void actionPerformed(GuiButton button){
		switch (button.id) {
		case 0:
			color=Colors.DYE_BLACK;
			break;
		case 1:
			color=Colors.DYE_RED;
			break;
		case 2:
			color=Colors.DYE_GREEN;
			break;
		case 3:
			color=Colors.DYE_BROWN;
			break;
		case 4:
			color=Colors.DYE_BLUE;
			break;
		case 5:
			color=Colors.DYE_PURPLE;
			break;
		case 6:
			color=Colors.DYE_CYAN;
			break;
		case 7:
			color=Colors.DYE_LIGHTGRAY;
			break;
		case 8:
			color=Colors.DYE_GRAY;
			break;
		case 9:
			color=Colors.DYE_PINK;
			break;
		case 10: //a
			color=Colors.DYE_LIME;
			break;
		case 11: //b
			color=Colors.DYE_YELLOW;
			break;
		case 12: //c
			color=Colors.DYE_LIGHTBLUE;
			break;
		case 13: //d
			color=Colors.DYE_MAGENTA;
			break;
		case 14: //e
			color=Colors.DYE_ORANGE;
			break;
		case 15: //e
			color=Colors.DYE_WHITE;
			break;
		default: //done
			if(FadeColor){
				Editor.setFadeColor(Index, color);
			}else{
				Editor.setColor(Index, color);
			}
			mc.displayGuiScreen(Last);
			break;
		}
		setTFColor();
	}
	GuiButton done;
	GuiTextField red,green,blue;

    public void updateScreen()
    {
    	red.updateCursorCounter();
    	green.updateCursorCounter();
    	blue.updateCursorCounter();
    }
	protected void keyTyped(char par1, int par2) throws IOException{
		red.textboxKeyTyped(par1, par2);
		green.textboxKeyTyped(par1, par2);
		blue.textboxKeyTyped(par1, par2);
        super.keyTyped(par1, par2);

    }
	protected void mouseClicked(int x, int y, int btn) throws IOException {
        super.mouseClicked(x, y, btn);
        red.mouseClicked(x, y, btn);
        green.mouseClicked(x, y, btn);
        blue.mouseClicked(x, y, btn);
	}
	public void setTFColor(){
		int cred = (int)(color >> 16 & 255);
		int cgreen = (int)(color >> 8 & 255);
		int cblue = (int)(color & 255);
        red.setText(Integer.toString(cred));
        green.setText(Integer.toString(cgreen));
        blue.setText(Integer.toString(cblue));
        color=Colors.getColorWithRGB(cred, cgreen, cblue);
        
	}
	public void setFTColor(){
		int var1=0;
		int var2=0;
		int var3=0;
		try {
			var1=Float.valueOf(red.getText()).intValue();
		} catch (Exception e) {}
		try {
			var2=Float.valueOf(green.getText()).intValue();
		} catch (Exception e) {}
		try {
			var3=Float.valueOf(blue.getText()).intValue();
		} catch (Exception e) {}
		color=Colors.getColorWithRGB(var1, var2, var3);
		done.packedFGColour=color;
        buttonList.remove(done);
        if(color==0){done.packedFGColour=1;}else{
        done.packedFGColour=color;}
		
	}
	public void initGui() {
		fontRendererObj.drawSplitString(I18n.format("item.fireworksCharge.red"), width/2-150, height/2-64-fontRendererObj.FONT_HEIGHT, 100, Colors.WHITE);
		fontRendererObj.drawSplitString(I18n.format("item.fireworksCharge.green"), width/2-50, height/2-64-fontRendererObj.FONT_HEIGHT, 100, Colors.WHITE);
		fontRendererObj.drawSplitString(I18n.format("item.fireworksCharge.blue"), width/2+50, height/2-64-fontRendererObj.FONT_HEIGHT, 100, Colors.WHITE);
		red=new GuiTextField(17, fontRendererObj, width/2-150, height/2-63, 99, 20);
		green=new GuiTextField(18, fontRendererObj, width/2-50, height/2-63, 99, 20);
		blue=new GuiTextField(19, fontRendererObj, width/2+50, height/2-63, 99, 20);
		setTFColor();
		//Line 1
		GuiButton pre=new GuiButton(0, width/2-150, height/2-42, 70, 20, I18n.format("item.fireworksCharge.black", new Object[0]));
		pre.packedFGColour=Colors.DYE_BLACK;
		buttonList.add(pre);
		pre=new GuiButton(1, width/2-75, height/2-42, 70, 20, I18n.format("item.fireworksCharge.red", new Object[0]));
		pre.packedFGColour=Colors.DYE_RED;
		buttonList.add(pre);
		pre=new GuiButton(2, width/2+5, height/2-42, 70, 20, I18n.format("item.fireworksCharge.green", new Object[0]));
		pre.packedFGColour=Colors.DYE_GREEN;
		buttonList.add(pre);
		pre=new GuiButton(3, width/2+80, height/2-42, 70, 20, I18n.format("item.fireworksCharge.brown", new Object[0]));
		pre.packedFGColour=Colors.DYE_BROWN;
		buttonList.add(pre);

		//Line 2
		
		pre=new GuiButton(4, width/2-150, height/2-21, 70, 20, I18n.format("item.fireworksCharge.blue", new Object[0]));
		pre.packedFGColour=Colors.DYE_BLUE;
		buttonList.add(pre);
		pre=new GuiButton(5, width/2-75, height/2-21, 70, 20, I18n.format("item.fireworksCharge.purple", new Object[0]));
		pre.packedFGColour=Colors.DYE_PURPLE;
		buttonList.add(pre);
		pre=new GuiButton(6, width/2+5, height/2-21, 70, 20, I18n.format("item.fireworksCharge.cyan", new Object[0]));
		pre.packedFGColour=Colors.DYE_CYAN;
		buttonList.add(pre);
		pre=new GuiButton(7, width/2+80, height/2-21, 70, 20, I18n.format("item.fireworksCharge.silver", new Object[0]));
		pre.packedFGColour=Colors.DYE_LIGHTGRAY;
		buttonList.add(pre);

		//Line 3
		
		pre=new GuiButton(8, width/2-150, height/2, 70, 20, I18n.format("item.fireworksCharge.gray", new Object[0]));
		pre.packedFGColour=Colors.DYE_GRAY;
		buttonList.add(pre);
		pre=new GuiButton(9, width/2-75, height/2, 70, 20, I18n.format("item.fireworksCharge.pink", new Object[0]));
		pre.packedFGColour=Colors.DYE_PINK;
		buttonList.add(pre);
		pre=new GuiButton(10, width/2+5, height/2, 70, 20, I18n.format("item.fireworksCharge.lime", new Object[0]));
		pre.packedFGColour=Colors.DYE_LIME;
		buttonList.add(pre);
		pre=new GuiButton(11, width/2+80, height/2, 70, 20, I18n.format("item.fireworksCharge.yellow", new Object[0]));
		pre.packedFGColour=Colors.DYE_YELLOW;
		buttonList.add(pre);

		//Line 4
		
		pre=new GuiButton(12, width/2-150, height/2+21, 70, 20, I18n.format("item.fireworksCharge.lightBlue", new Object[0]));
		pre.packedFGColour=Colors.DYE_LIGHTBLUE;
		buttonList.add(pre);
		pre=new GuiButton(13, width/2-75, height/2+21, 70, 20, I18n.format("item.fireworksCharge.magenta", new Object[0]));
		pre.packedFGColour=Colors.DYE_MAGENTA;
		buttonList.add(pre);
		pre=new GuiButton(14, width/2+5, height/2+21, 70, 20, I18n.format("item.fireworksCharge.orange", new Object[0]));
		pre.packedFGColour=Colors.DYE_ORANGE;
		buttonList.add(pre);
		pre=new GuiButton(15, width/2+80, height/2+21, 70, 20, I18n.format("item.fireworksCharge.white", new Object[0]));
		pre.packedFGColour=Colors.DYE_WHITE;
		buttonList.add(pre);
		
		
		done=new GuiButton(16, width/2-150, height/2+42, 300, 20, I18n.format("gui.done", new Object[0]));
        done.packedFGColour=color;
		buttonList.add(done);
		super.initGui();
	}
	private String tred;
	private String tgreen;
	private String tblue;
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		red.drawTextBox();
		green.drawTextBox();
		blue.drawTextBox();
        if(red.getText()!=tred){setFTColor();tred=red.getText();}
        if(green.getText()!=tgreen){setFTColor();tgreen=green.getText();}
        if(blue.getText()!=tblue){setFTColor();tblue=blue.getText();}
        buttonList.remove(done);
        if(color==0){done.packedFGColour=1;}else{
        done.packedFGColour=color;}
        buttonList.add(done);
		
        tred=red.getText();
        tgreen=green.getText();
        tblue=blue.getText();
        
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
