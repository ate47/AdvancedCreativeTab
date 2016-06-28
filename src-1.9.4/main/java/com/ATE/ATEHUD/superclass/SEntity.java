package com.ATE.ATEHUD.superclass;

public class SEntity {
	private String id;
	private int Invulnerable;
	private String CustomName;
	private int CustomNameVisible;
	private Effect[] ActiveEffects;
	private int NoAI;
	public SEntity(String id, int invulnerable, String customName,
			int customNameVisible, Effect[] activeEffects, int noAI) {
		this.id = id;
		Invulnerable = invulnerable;
		CustomName = customName;
		CustomNameVisible = customNameVisible;
		ActiveEffects = activeEffects;
		NoAI = noAI;
	}
	public String getSummonCommand(String nbtToAdd){
		String[] list={"","","","","",""};
		if(Invulnerable==1)list[0]="Invulnerable:1";
		if(CustomNameVisible==1)list[1]=",CustomNameVisible:1";
		if(NoAI==1)list[2]=",NoAI:1";
		if(!CustomName.isEmpty())list[3]=",CustomName:\""+CustomName+"\"";
		if(ActiveEffects.length!=0){
			String[] nbts=new String[ActiveEffects.length];
			for (int i = 0; i < ActiveEffects.length; i++) {
				nbts[i]=ActiveEffects[0].getNBT();
			}
			list[4]=Effect.getAllNBT(nbts);
		}
		return "/summon "+id+" ~ ~1 ~ {"+(list[0]+list[1]+list[2]+list[3]+list[4]+","+nbtToAdd).replaceFirst(",","")+"}";
	}
	
}
