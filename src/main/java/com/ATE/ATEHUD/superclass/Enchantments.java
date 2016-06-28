package com.ATE.ATEHUD.superclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.enchantment.Enchantment;

public class Enchantments {
	public static final EnchantmentType ALL=new EnchantmentType("All", 0);
	public static final EnchantmentType ARMOR=new EnchantmentType("Armor", 1);
	public static final EnchantmentType SWORD=new EnchantmentType("Sword", 2);
	public static final EnchantmentType TOOL=new EnchantmentType("Tool", 3);
	public static final EnchantmentType BOW=new EnchantmentType("Bow", 4);
	public static final EnchantmentType FISHINGROD=new EnchantmentType("Fishing Rod", 5);
	public static final Enchantments[] enchantments=new Enchantments[]{
	//Armor Enchant 9
	new Enchantments(ARMOR,"PROTECTION",0,Enchantment.protection),
	new Enchantments(ARMOR,"FIRE_PROTECTION",1,Enchantment.fireProtection),
	new Enchantments(ARMOR,"FEATHER_PROTECTION",2,Enchantment.featherFalling),
	new Enchantments(ARMOR,"BLAST_PROTECTION",3,Enchantment.blastProtection),
	new Enchantments(ARMOR,"PROJECTILE_PROTECTION",4,Enchantment.projectileProtection),
	new Enchantments(ARMOR,"RESPIRATION",5,Enchantment.respiration),
	new Enchantments(ARMOR,"AQUA_AFFINITY",6,Enchantment.aquaAffinity,true),
	new Enchantments(ARMOR,"THORNS",7,Enchantment.thorns),
	new Enchantments(ARMOR,"DEPTH_STRIDER",8,Enchantment.depthStrider),
	//Sword Enchant 6 15
	new Enchantments(SWORD,"SHARPNESS",16,Enchantment.sharpness),
	new Enchantments(SWORD,"SMITE",17,Enchantment.smite),
	new Enchantments(SWORD,"BANE_OF_ARTHROPODS",18,Enchantment.baneOfArthropods),
	new Enchantments(SWORD,"KNOCKBACK",19,Enchantment.knockback),
	new Enchantments(SWORD,"FIRE_ASPECT",20,Enchantment.fireAspect),
	new Enchantments(SWORD,"LOOTING",21,Enchantment.looting),
	//Tools Enchant 4 19
	new Enchantments(TOOL,"EFFICIENCY",32,Enchantment.efficiency),
	new Enchantments(TOOL,"SILK_TOUCH",33,Enchantment.silkTouch,true),
	new Enchantments(ALL,"UNBREAKING",34,Enchantment.unbreaking),
	new Enchantments(TOOL,"FORTUNE",35,Enchantment.fortune),
	//Bow Enchant 6 23
	new Enchantments(BOW,"POWER",48,Enchantment.power),
	new Enchantments(BOW,"PUNCH",49,Enchantment.punch),
	new Enchantments(BOW,"FLAME",50,Enchantment.flame,true),
	new Enchantments(BOW,"INFINITY",51,Enchantment.infinity,true),
	//CP 2 25
	new Enchantments(FISHINGROD,"LUCK_OF_THE_SEA",61,Enchantment.luckOfTheSea),
	new Enchantments(FISHINGROD,"LURE",62,Enchantment.lure)};

	public Enchantment getEnchantment(){return enchantment;}
	public boolean isOneLevel(){return OneLevel;}
	public int getId(){return Id;}
	
	private Enchantment enchantment;
	private boolean OneLevel=false;
	private int Id;
	public Enchantments(EnchantmentType type,String name,int id,Enchantment enchantment){
		Id=id;
		this.enchantment=enchantment;
	}
	public Enchantments(EnchantmentType type,String name,int id,Enchantment enchantment,boolean isOneLevel){
		Id=id;
		OneLevel=isOneLevel;
		this.enchantment=enchantment;
	}
}
