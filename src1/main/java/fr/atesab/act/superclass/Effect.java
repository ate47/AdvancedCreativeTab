package fr.atesab.act.superclass;

import java.lang.reflect.Field;

import fr.atesab.act.ModMain;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class Effect {
	public static Item[] itm_type;

	public static EffectType[] eff_effect;

	public static PotionSkin[] psk_skin;

	public static String[] type = { getPotionItemField(0), getPotionItemField(1), getPotionItemField(2) },
			skin = { "empty,0,0", "water,0,0", "mundane,0,0", "thick,0,0", "ankward,0,0", "night_vision,0,1",
					"invisibility,0,1", "leaping,1,1", "fire_resistance,0,1", "swiftness,1,1", "slowness,0,1",
					"water_breathing,0,1", "healing,1,0", "harming,1,0", "poison,1,1", "regeneration,1,1",
					"strength,1,1", "weakness,0,1", "luck,0,0" },
			effects = { "moveSpeed,1", "moveSlowdown,2", "digSpeed,3", "digSlowDown,4", "damageBoost,5", "heal,6",
					"harm,7", "jump,8", "confusion,9", "regeneration,10", "resistance,11", "fireResistance,12",
					"waterBreathing,13", "invisibility,14", "blindness,15", "nightVision,16", "hunger,17",
					"weakness,18", "poison,19", "wither,20", "healthBoost,21", "absorption,22", "saturation,23",
					"glowing,24", "levitation,25", "luck,26", "unluck,27" };

	public static void defineEffects() {
		Item[] list = new Item[type.length];
		int j = 0;
		for (int i = 0; i < type.length; i++) {
			String[] strl = type[i].split(",");
			if (strl.length == 2) {
				try {
					list[j] = (Item) Class.forName(strl[0]).getField(strl[1]).get(Item.class);
					j++;
				} catch (Exception e) {
					if (ModMain.AdvancedModActived)
						System.out.println(
								"Fail to add Class/Field (Class or Field doesn't exist) " + strl[0] + "." + strl[1]);
				}
			} else {
				if (ModMain.AdvancedModActived)
					System.out.println("Fail to add Class/Field (data_size!=2) \"" + type[i] + "\"");
			}
		}
		itm_type = new Item[j];
		for (int i = 0; i < itm_type.length; i++) {
			itm_type[i] = list[i];
		}
		EffectType[] list1 = new EffectType[effects.length];
		j = 0;
		for (int i = 0; i < list1.length; i++) {
			String[] strl = effects[i].split(",");
			if (strl.length == 2) {
				try {
					list1[j] = new EffectType(Integer.valueOf(strl[1]), strl[0]);
					j++;
				} catch (Exception e) {
					if (ModMain.AdvancedModActived)
						System.out.println("Fail to add effect " + strl[0] + " : " + strl[1] + " isn't an integer");
				}
			} else {
				if (ModMain.AdvancedModActived)
					System.out.println("Fail to add effect (data_size!=2) \"" + effects[i] + "\"");
			}
		}
		eff_effect = new EffectType[j];
		for (int i = 0; i < eff_effect.length; i++) {
			eff_effect[i] = list1[i];
		}
		PotionSkin[] list2 = new PotionSkin[skin.length];
		j = 0;
		for (int i = 0; i < list2.length; i++) {
			String[] strl = skin[i].split(",");
			if (strl.length == 3) {
				boolean isLon = false, isStr = false;
				if (strl[1] == "1")
					isStr = true;
				if (strl[2] == "1")
					isLon = true;
				list2[j] = new PotionSkin(strl[0], isLon, isStr);
				j++;
			} else {
				if (ModMain.AdvancedModActived)
					System.out.println("Fail to add skin (data_size!=3) \"" + skin[i] + "\"");
			}
		}
		psk_skin = new PotionSkin[j];
		for (int i = 0; i < list2.length; i++) {
			psk_skin[i] = list2[i];
		}
	}

	public static String getAllNBT(String[] nbts) {
		String str = "[";
		if (nbts.length > 0) {
			str += nbts[0];
		}
		for (int i = 1; i < nbts.length; i++) {
			str += "," + nbts[i];
		}
		return str + "]";
	}

	private static String getPotionItemField(int type) {
		Field[] flds = Items.class.getFields();
		for (int i = 0; i < Items.class.getFields().length; i++) {
			try {
				Item itm = (Item) flds[i].get(Item.class);
				String ClassName = Items.class.getName();
				String FieldName = flds[i].getName();
				switch (type) {
				case 1:
					if (itm == Items.LINGERING_POTION) {
						return ClassName + "," + FieldName;
					}
					break;
				case 2:
					if (itm == Items.SPLASH_POTION) {
						return ClassName + "," + FieldName;
					}
					break;
				default:
					if (itm == Items.POTIONITEM) {
						return ClassName + "," + FieldName;
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "ERROR";
	}

	private int Id, Duration, Amplifier, Ambient, ShowParticles;

	/**
	 * 
	 * @param id
	 *            The effect ID.
	 */
	public Effect(int id) {
		this(id, 600);
	}

	/**
	 * 
	 * @param id
	 *            The effect ID.
	 * @param duration
	 *            The number of ticks before the effect wears off.
	 */
	public Effect(int id, int duration) {
		this(id, duration, 0);
	}

	/**
	 * 
	 * @param id
	 *            The effect ID.
	 * @param duration
	 *            The number of ticks before the effect wears off.
	 * @param amplifier
	 *            The potion effect level. 0 is level 1. (max=126)
	 */
	public Effect(int id, int duration, int amplifier) {
		this(id, duration, amplifier, 0);
	}

	/**
	 * 
	 * @param id
	 *            The effect ID.
	 * @param duration
	 *            The number of ticks before the effect wears off.
	 * @param amplifier
	 *            The potion effect level. 0 is level 1.
	 * @param ambient
	 *            1 or 0 (true/false) - true if this effect is provided by a Beacon
	 *            and therefore should be less intrusive on screen.
	 */
	public Effect(int id, int duration, int amplifier, int ambient) {
		this(id, duration, amplifier, ambient, 1);
	}
	/**
	 * 
	 * @param id
	 *            The effect ID.
	 * @param duration
	 *            The number of ticks before the effect wears off.
	 * @param amplifier
	 *            The potion effect level. 0 is level 1.
	 * @param ambient
	 *            1 or 0 (true/false) - true if this effect is provided by a Beacon
	 *            and therefore should be less intrusive on screen.
	 * @param showParticles
	 *            1 or 0 (true/false) - true if particles are shown (affected by
	 *            "Ambient"). false if no particles are shown.
	 */
	public Effect(int id, int duration, int amplifier, int ambient, int showParticles) {
		Id = id;
		Duration = duration;
		if (amplifier > 127)
			Amplifier = 126;
		if (amplifier < -127)
			Amplifier = -126;
		Amplifier = amplifier;
		Ambient = ambient;
		ShowParticles = showParticles;
	}
	public int getAmbient() {
		return Ambient;
	}

	public int getAmplifier() {
		return Amplifier;
	}

	public int getDuration() {
		return Duration;
	}

	public int getId() {
		return Id;
	}

	public String getNBT() {
		return "{Id:" + Id + ",Amplifier:" + Amplifier + ",Duration:" + Duration + ",ShowParticles:" + ShowParticles
				+ ",Ambient:" + Ambient + "}";
	}

	public int getShowParticles() {
		return ShowParticles;
	}

	public void setAmbient(int value) {
		Ambient = value;
	}

	public void setAmplifier(int value) {
		Amplifier = value;
	}

	public void setDuration(int value) {
		Duration = value;
	}

	public void setId(int value) {
		Id = value;
	}

	public void setShowParticles(int value) {
		ShowParticles = value;
	}

	public String toString() {
		return "Effect [Id=" + Id + ", Duration=" + Duration + ", Amplifier=" + Amplifier + ", Ambient=" + Ambient
				+ ", ShowParticles=" + ShowParticles + "]";
	}

}
