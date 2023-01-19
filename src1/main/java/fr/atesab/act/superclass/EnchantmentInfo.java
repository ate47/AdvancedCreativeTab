package fr.atesab.act.superclass;

public class EnchantmentInfo {
	public int Level = 0;
	public Enchantments Enchantment;

	public EnchantmentInfo(Enchantments ench, int level) {
		Level = level;
		Enchantment = ench;
	}

	public String getEnchNbt() {
		return "{id:" + Enchantment.getId() + ",lvl:" + Level + "}";
	}
}
