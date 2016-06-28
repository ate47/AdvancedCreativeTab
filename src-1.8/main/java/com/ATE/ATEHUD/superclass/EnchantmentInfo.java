package com.ATE.ATEHUD.superclass;

public class EnchantmentInfo {
	public int Level=0;
	public Enchantments Enchantment;
	public String getEnchNbt(){
		return "{id:"+Enchantment.getId()+",lvl:"+Level+"}";
	}
	public EnchantmentInfo(Enchantments ench,int level){
		Level=level;
		Enchantment=ench;
	}
}
