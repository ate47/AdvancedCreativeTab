package com.ATE.ATEHUD.superclass;

import java.util.Arrays;

import com.ATE.ATEHUD.FakeItems2;
import com.ATE.ATEHUD.ModMain;

public class Explosion {
	public String toString() {
		return "Explosion [Flicker=" + Flicker + ", Trail=" + Trail + ", Type="
				+ Type + ", Colors=" + Arrays.toString(Colors)
				+ ", FadeColors=" + Arrays.toString(FadeColors) + "]";
	}
	 private int Flicker=0;  
	 private int Trail=0;  
	 private int Type=0;  
	 private int[] Colors={0}; 
	 private int[] FadeColors={};
	 public void setFlicker(int flicker){Flicker=flicker;}  
	 public void setTrail(int trail){Trail=trail;}  
	 public void setType(int type){Type=type;}
	 public void setColors(int[] colors){Colors=colors;} 
	 public void setFadeColors(int[] fadecolors){}
	 public int getFlicker(){return Flicker;}  
	 public int getTrail(){return Trail;}  
	 public int getType(){return Type;}
	 public int[] getColors(){return  Colors;} 
	 public int[] getFadeColors(){return FadeColors;}
	 /**
	  * 
	  * @param flicker 1 or 0 (true/false) - true if this explosion will have the Twinkle effect (glowstone dust)
	  * @param trail 1 or 0 (true/false) - true if this explosion will have the Trail effect (diamond)
	  * @param type The shape of this firework's explosion. 0 = Small Ball, 1 = Large Ball, 2 = Star-shaped, 3 = Creeper-shaped, 4 = Burst. Other values will be named "Unknown Shape" and render as Small Ball.
	  * @param colors Array of integer values corresponding to the primary colors of this firework's explosion.
	  * @param fadecolors Array of integer values corresponding to the fading colors of this firework's explosion.
	  */
	 public Explosion(int flicker, int trail, int type, int[] colors, int[] fadecolors) {
		 Flicker=flicker;
		 Trail=trail;
		 Type=type;
		 if(colors==new int[]{}) {
			 Colors=new int[]{getColorWithRGB(255,255,255)};
		 }else{
			 Colors=colors;
		 }
		 FadeColors=fadecolors;
	 }
	 public Explosion() {
		 Flicker=0;
		 Trail=0;
		 Type=0;
		 Colors=new int[]{com.ATE.ATEHUD.superclass.Colors.DYE_WHITE};
		 FadeColors=new int[]{};
	 }
	 public static int getColorWithRGB(int red, int green, int blue) {
		return blue+256*green+256*256*red;
	 }
	 public static int hex2Rgb(String colorStr) {
		    return getColorWithRGB(Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
		    Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
		}
	 
	 public String getNBTExplosion() {
		 if(Colors.length==0) {
			 Colors=new int[]{getColorWithRGB(255,255,255)};
		 }
		 String outColors="Colors:["+Colors[0];
		 
		 for (int i = 1; i < Colors.length; i++) {
			 outColors=outColors+","+Colors[i]+"";
		 }
		 outColors=outColors+"]";
		 
		 String outFadeColors="";
		 if(FadeColors.length!=0) {
			 outFadeColors=", FadeColors:["+FadeColors[0];
			 for (int j = 1; j < FadeColors.length; j++) {
				 outFadeColors=outFadeColors+","+Colors[j]+"";
			 }
			 outFadeColors=outFadeColors+"]";
		 }
		 return "{Type:"+Type+",Trail:"+Trail+",Flicker:"+Flicker+", "+outColors+outFadeColors+"}";
	 }
	 
}
