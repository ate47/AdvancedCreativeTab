package com.ATE.ATEHUD.superclass;

public class Effect {
	private int Id;
	private int Duration;
	private int Amplifier;
	private int Ambient;
	private int ShowParticles;
	
	public int getId(){return Id;}
	public int getDuration(){return Duration;}
	public int getAmplifier(){return Amplifier;}
	public int getShowParticles(){return ShowParticles;}
	public int getAmbient(){return Ambient;}
	
	public void setDuration(int value){Duration=value;}
	public void setAmplifier(int value){Amplifier=value;}
	public void setShowParticles(int value){ShowParticles=value;}
	public void setId(int value){Id=value;}
	public void setAmbient(int value){Ambient=value;}
	
	public static Effect SPEED=new Effect(1);
	public static Effect SLOWNESS=new Effect(2);
	public static Effect HASTE=new Effect(3);
	public static Effect MINING_FATIGUE=new Effect(4);
	public static Effect STRENGH=new Effect(5);
	public static Effect INSTANT_HEALTH=new Effect(6);
	public static Effect INSTANT_DAMAGE=new Effect(7);
	public static Effect JUMP_BOOST=new Effect(8);
	public static Effect NAUSEA=new Effect(9);
	public static Effect REGENERATION=new Effect(10);
	public static Effect RESISTANCE=new Effect(11);
	public static Effect FIRE_RESISTANCE=new Effect(12);
	public static Effect WATER_BREATHING=new Effect(13);
	public static Effect INVISIBILITY=new Effect(14);
	public static Effect BLINDNESS=new Effect(15);
	public static Effect NIGHT_VISION=new Effect(16);
	public static Effect HUNGER=new Effect(17);
	public static Effect WEAKNESS=new Effect(18);
	public static Effect POISON=new Effect(19);
	public static Effect WITHER=new Effect(20);
	public static Effect HEALTH_BOOST=new Effect(21);
	public static Effect ABSORPTION=new Effect(22);
	public static Effect SATURATION=new Effect(23);

	/**
	 * 
	 * @param id The effect ID.
	 */
	public Effect(int id){
		Id=id;
		Duration=600;
		Amplifier=0;
		Ambient=0;
		ShowParticles=1;
	}
	/**
	 * 
	 * @param id The effect ID.
	 * @param duration The number of ticks before the effect wears off.
	 */
	public Effect(int id,int duration){

		Id=id;
		Duration=duration;
		Amplifier=0;
		Ambient=0;
		ShowParticles=1;
	}
	/**
	 * 
	 * @param id The effect ID.
	 * @param duration The number of ticks before the effect wears off.
	 * @param amplifier The potion effect level. 0 is level 1.
	 */
	public Effect(int id,int duration,int amplifier){

		Id=id;
		Duration=duration;
		Amplifier=amplifier;
		Ambient=0;
		ShowParticles=1;
	}
	/**
	 * 
	 * @param id The effect ID.
	 * @param duration The number of ticks before the effect wears off.
	 * @param amplifier The potion effect level. 0 is level 1.
	 * @param ambient 1 or 0 (true/false) - true if this effect is provided by a Beacon and therefore should be less intrusive on screen.
	 */
	public Effect(int id,int duration,int amplifier,int ambient){
		Id=id;
		Duration=duration;
		Amplifier=amplifier;
		Ambient=ambient;
		ShowParticles=1;
	}
	/**
	 * 
	 * @param id The effect ID.
	 * @param duration The number of ticks before the effect wears off.
	 * @param amplifier The potion effect level. 0 is level 1.
	 * @param ambient 1 or 0 (true/false) - true if this effect is provided by a Beacon and therefore should be less intrusive on screen.
	 * @param showParticles 1 or 0 (true/false) - true if particles are shown (affected by "Ambient"). false if no particles are shown.
	 */
	public Effect(int id,int duration,int amplifier,int ambient,int showParticles){
		Id=id;
		Duration=duration;
		Amplifier=amplifier;
		Ambient=ambient;
		ShowParticles=showParticles;
	}
	@Override
	public String toString() {
		return "Effect [Id=" + Id + ", Duration=" + Duration + ", Amplifier="
				+ Amplifier + ", Ambient=" + Ambient + ", ShowParticles="
				+ ShowParticles + "]";
	}
	
}
