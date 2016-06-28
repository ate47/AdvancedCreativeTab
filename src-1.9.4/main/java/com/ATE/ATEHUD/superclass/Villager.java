package com.ATE.ATEHUD.superclass;

public class Villager extends SEntity {
	public Villager(String id, int invulnerable, String customName,
			int customNameVisible, Effect[] activeEffects, int noAI,
			enumVillagerType type,Offer[] Offers) {
		super(id, invulnerable, customName, customNameVisible, activeEffects, noAI);
		this.type=type;
		this.Offers=Offers;
	}
	private enumVillagerType type;
	private Offer[] Offers;
	public enum enumVillagerType{
		farmer_farmer(1,0,"farmer"),
		farmer_fisherman(2,0,"fisherman"),
		farmer_shepherd(3,0,"shepherd"),
		farmer_fletcher(4,0,"fletcher"),
		librarian(1,1,"librarian"),
		cleric(1,2,"cleric"),
		blacksmith_armorer(1,3,"armor"),
		blacksmith_weaponSmith(2,3,"weapon"),
		blacksmith_toolSmith(3,3,"tool"),
		butcher_butcher(1,4,"butcher"),
		butcher_leatherworker(2,4,"leather");
		private int id;
		private int proffessionId;
		private String unlocalisedName;
		String getUnlocalisedName(){
			return "entity.Villager."+unlocalisedName;
		}
		int getId(){
			return id;
		}
		int getProffessionId(){
			return proffessionId;
		}
		enumVillagerType(int id,int proffessionId,String name){
			this.id=id;
			this.proffessionId=proffessionId;			
		}
	}
	public String getSummonCommand() {
		String addedInformation="Profession:"+type.proffessionId+",Career:"+type.id+",Offers:{Recipes:[";
		for (int i = 0; i < Offers.length; i++) {
			
		}
		addedInformation+="]}";
		return super.getSummonCommand(addedInformation);
	}
}
