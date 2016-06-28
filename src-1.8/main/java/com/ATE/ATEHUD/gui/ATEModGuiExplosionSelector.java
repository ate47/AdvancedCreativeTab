package com.ATE.ATEHUD.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ATE.ATEHUD.superclass.Explosion;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

public class ATEModGuiExplosionSelector extends GuiScreen {
	private boolean buffer=false;
	private boolean isTrail=false, isFlicker=false;
	private Explosion exp;
    private GuiButton buttonDone,buttonCancel,Flicker,Trail;
    private ATEModGuiFireWorkFactory Last;
    private int[] colors={-1,-1,-1,-1,-1,-1,-1},
    			  fadeColors={-1,-1,-1,-1,-1,-1,-1};
    public int getColorSize(){
    	int a=0;
    	for (int i = 0; i < colors.length; i++) {
			if(colors[i]!=-1)a++;
		}
    	return a;
    }
    public int getFadeColorSize(){
    	int a=0;
    	for (int i = 0; i < fadeColors.length; i++) {
			if(fadeColors[i]!=-1)a++;
		}
    	return a;
    }
    /**
     * Set the value of a specefic color
     * @param index Index in the array
     * @param value Color / -1=remove
     */
    public void setColor(int index, int value) {
    	if(index<colors.length && index>=0){
    		colors[index]=value;
    		int[] array=colors;
    		int a=0;
    		for (int i = 0; i < colors.length; i++) {
				if(colors[i]!=-1) {
					array[a]=colors[i];
					a++;
				}
			}
			for (int j = a; j < array.length; j++) {
				array[j]=-1;
			}
    		colors=array;
    	}else{System.out.println("BAD INDEX: 0 | <"+index+"> | "+colors.length);}
    }
	/**
	 * Set the value of a specefic fadcolor
	 * @param index Index in the array
	 * @param value Color / -1=remove
	 */
    public void setFadeColor(int index, int value) {
    	if(index<fadeColors.length && index>=0){
    		fadeColors[index]=value;
    		int[] array=fadeColors;
    		int a=0;
    		for (int i = 0; i < fadeColors.length; i++) {
				if(fadeColors[i]!=-1) {
					array[a]=fadeColors[i];
					a++;
				}
			}
			for (int j = a; j < array.length; j++) {
				array[j]=-1;
			}
    		fadeColors=array;
    	}else{System.out.println("BAD INDEX: 0 | <"+index+"> | "+fadeColors.length);}
    }
    public void addColor(int value) {
    	if(getColorSize()<colors.length)colors[getColorSize()]=value;
    }
    public void addFadeColor(int value) {
    	if(getFadeColorSize()<fadeColors.length)fadeColors[getFadeColorSize()]=value;
    }
    
    
    private int ExpIndex,nextGuiId=0, Type=0,nbColor=0,nbFadeColor=0,maxColor=colors.length,maxFadeColor=fadeColors.length;
    private int getNextGuiId() {nextGuiId++;return nextGuiId;}
    private GuiButton addColor,addFadeColor;
    private List color=new ArrayList(),fadeColor=new ArrayList(); 
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		buttonList.clear();
		if(!buffer)buffer=true;
		buttonCancel= new GuiButton(0, width/2 -76, height-35, 76, 20, I18n.format("gui.act.cancel", new Object[0]));
		buttonDone = new GuiButton(1, width/2+1, height-35, 76, 20, I18n.format("gui.done", new Object[0]));
		Flicker=new GuiButton(2, width/2-200, height/2-50,200,20, getBooSign(isFlicker)+I18n.format("item.fireworksCharge.flicker", new Object[0]));
		Trail=  new GuiButton(3, width/2-200, height/2-71,200,20, getBooSign(isTrail)+I18n.format("item.fireworksCharge.trail", new Object[0]));
		fontRendererObj.drawString(LanguageRegistry.instance().getStringLocalization("gui.act.type"),
				width/2-200+100-fontRendererObj.getStringWidth(LanguageRegistry.instance().getStringLocalization("gui.act.type"))/2,
				height/2-9-fontRendererObj.FONT_HEIGHT,com.ATE.ATEHUD.superclass.Colors.GOLD);
		buttonList.add(new GuiButton(4, width/2-200, height/2-8,100,20, getBooSign(Type==0,new String[]{"2","b"})+I18n.format("item.fireworksCharge.type.0", new Object[0])));
		buttonList.add(new GuiButton(5, width/2-99, height/2-8,99,20, getBooSign(Type==1,new String[]{"2","b"})+I18n.format("item.fireworksCharge.type.1", new Object[0])));
		buttonList.add(new GuiButton(6, width/2-200, height/2-8+21,100,20, getBooSign(Type==2,new String[]{"2","b"})+I18n.format("item.fireworksCharge.type.2", new Object[0])));
		buttonList.add(new GuiButton(7, width/2-99, height/2-8+21,99,20, getBooSign(Type==3,new String[]{"2","b"})+I18n.format("item.fireworksCharge.type.3", new Object[0])));
		buttonList.add(new GuiButton(8, width/2-200, height/2-8+42,200,20, getBooSign(Type==4,new String[]{"2","b"})+I18n.format("item.fireworksCharge.type.4", new Object[0])));
		int a=0,a1=0,b=0,b1=0;
		for (int i = 0; i < getColorSize(); i++) {
			GuiButton c=new GuiButton(16+a, width/2+5, height/2-71+21*a,79,20, I18n.format("gui.act.color", new Object[0])+" "+i);
			c.packedFGColour=colors[i];
			buttonList.add(c);
			buttonList.add(new GuiButton(16+a+colors.length, width/2+85, height/2-71+21*a,20,20, "\u00a7c-"));
			a++;
		}
		if(addColor!=null)addColor.visible=false;
		if(getColorSize()<colors.length){
		if(buttonList.contains(addColor))buttonList.remove(addColor);
		addColor=(new GuiButton(9, width/2+5, height/2-71+21*a,100,20, "\u00a7a+"));
		buttonList.add(addColor);}
		nbColor=a;

		for (int i = 0; i < getFadeColorSize(); i++) {
			GuiButton fc=new GuiButton(16+b+2*colors.length, width/2+110, height/2-71+21*b,79,20, I18n.format("gui.act.fadecolor", new Object[0])+" "+i);
			fc.packedFGColour=fadeColors[i];
			buttonList.add(fc);
			buttonList.add(new GuiButton(16+b+3*colors.length, width/2+85+105, height/2-71+21*b,20,20, "\u00a7c-"));
			b++;
		}
		if(addFadeColor!=null)addFadeColor.visible=false;
		if(getFadeColorSize()<fadeColors.length){
			if(buttonList.contains(addFadeColor))buttonList.remove(addFadeColor);
			addFadeColor=(new GuiButton(10, width/2+110, height/2-71+21*b,100,20, "\u00a7a+"));
			buttonList.add(addFadeColor);}
		nbFadeColor=b;
		String s=LanguageRegistry.instance().getStringLocalization("gui.act.explosion")+(Integer)(ExpIndex+1);
		fontRendererObj.drawString(s, width/2-fontRendererObj.getStringWidth(s)/2, 40, com.ATE.ATEHUD.superclass.Colors.GOLD);
		
		buttonList.add(Flicker);
		buttonList.add(Trail);
		buttonList.add(buttonDone);
		buttonList.add(buttonCancel);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	public void initGui() {
		super.initGui();
	}
    public static int[] copyOf(int[] original, int newLength) {
        int[] arr = new int[newLength];
        int len  = (original.length < newLength ? original.length : newLength);
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
	public void actionPerformed(GuiButton button){
		if(button==buttonCancel)mc.displayGuiScreen(Last);
		if(button==buttonDone){
			int flicker=0;if(isFlicker)flicker=1;
			int trail=0;if(isTrail)trail=1;
			int newLengthC=0,newLengthFC=0;
			int[] doneColors,doneFadeColors;
			if(colors.length>0){
				doneColors=copyOf(colors, getColorSize());
			}else{
				doneColors=new int[]{};
			}
			if(fadeColors.length>0){
				doneFadeColors=copyOf(fadeColors, getFadeColorSize());
			}else{
				doneFadeColors=new int[]{};
			}
			
			Last.Explosions[ExpIndex]=new Explosion(flicker, trail, Type, doneColors, doneFadeColors);
			System.out.print("Explosion : \n Flicker: "+flicker+"\n Trail: "+trail+"\n Type: "+Type+"\n");
			for (int i = 0; i < getColorSize(); i++){System.out.println(doneColors[i]);}
			for (int i = 0; i < getFadeColorSize(); i++){System.out.println(doneFadeColors[i]);}
			mc.displayGuiScreen(Last);
		}
		if(button==Trail){
			if(isTrail){isTrail=false;}else{isTrail=true;}
		}
		if(button==Flicker){
			if(isFlicker){isFlicker=false;}else{isFlicker=true;}
		}
		switch (button.id) {
		case 4:
			Type=0;
			break;
		case 5:
			Type=1;
			break;
		case 6:
			Type=2;
			break;
		case 7:
			Type=3;
			break;
		case 8:
			Type=4;
			break;
		}
		int id=0;
		if(button.id>=16 && button.id<16+colors.length) {
			id=button.id-16;
			mc.displayGuiScreen(new ATEModGuiColorSelector(this, this, false, id,colors[id]));
			
		}
		if(button.id>=16+colors.length && button.id<16+2*colors.length) {
			id=button.id-16-colors.length;
			setColor(id, -1);
		}
		if(button.id>=16+2*colors.length && button.id<16+3*colors.length) {
			id=button.id-16-2*colors.length;
			mc.displayGuiScreen(new ATEModGuiColorSelector(this, this, true, id,fadeColors[id]));
			
		}
		if(button.id>=16+3*colors.length && button.id<16+4*colors.length) {
			id=button.id-16-3*colors.length;
			setFadeColor(id, -1);
		}
		if(button.id==9){if(getColorSize()<colors.length){if(buffer){buffer=false;addColor(com.ATE.ATEHUD.superclass.Colors.WHITE);}}}
		if(button.id==10){if(getFadeColorSize()<fadeColors.length){if(buffer){buffer=false;addFadeColor(com.ATE.ATEHUD.superclass.Colors.WHITE);}}}
		System.out.println(button.id);
	}
	public ATEModGuiExplosionSelector(ATEModGuiFireWorkFactory last,int expIndex,Explosion exp) {
		super();
		Last=last;
		ExpIndex=expIndex;
		boolean trl=false;
		boolean flr=false;
		if(exp!=null) {
			if(exp.getTrail()==1)isTrail=true;
			if(exp.getFlicker()==1)isFlicker=true;
			Type=exp.getType();
			setColorArray(exp.getColors());
			setFadeColorArray(exp.getFadeColors());
		}
			
	}
	private void setColorArray(int[] array) {
		if(array.length>=colors.length) {
			colors=array;
		}else{
			for (int i = 0; i < array.length; i++) {
				colors[i]=array[i];
			}
		}
	}
	private void setFadeColorArray(int[] array) {
		if(array.length>=fadeColors.length) {
			fadeColors=array;
		}else{
			for (int i = 0; i < array.length; i++) {
				fadeColors[i]=array[i];
			}
		}
	}
	public String getBooSign(boolean bool,String[] format) {
		if(format.length!=2)format=new String[]{"a","c"};
		if(bool) {
			return "\u00a7"+format[0];
		}else{
			return "\u00a7"+format[1];
		}
	}
	public String getBooSign(boolean bool) {
		return getBooSign(bool,new String[]{"a","c"});
	}
}
