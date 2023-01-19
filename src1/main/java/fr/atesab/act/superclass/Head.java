package fr.atesab.act.superclass;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Head {
	public static String[] MHF_list = new String[] { "MHF_Alex", "MHF_Blaze", "MHF_CaveSpider", "MHF_Chicken",
			"MHF_Cow", "MHF_Creeper", "MHF_Enderman", "MHF_Ghast", "MHF_Herobrine", "MHF_Golem", "MHF_LavaSlime",
			"MHF_MushroomCow", "MHF_Ocelot", "MHF_Pig", "MHF_PigZombie", "MHF_Sheep", "MHF_Skeleton", "MHF_Slime",
			"MHF_Spider", "MHF_Squid", "MHF_Steve", "MHF_Villager", "MHF_WSkeleton", "MHF_Zombie", "MHF_Cactus",
			"MHF_TNT", "MHF_TNT2", "MHF_Cake", "MHF_Chest", "MHF_CoconutB", "MHF_CoconutG", "MHF_Melon", "MHF_OakLog",
			"MHF_Present1", "MHF_Present2", "MHF_Pumpkin", "MHF_ArrowUp", "MHF_ArrowDown", "MHF_ArrowLeft",
			"MHF_ArrowRight", "MHF_Exclamation", "MHF_Question" };
	public static String[] VIP_list = new String[] { "Aypierre", "Aurelien_Sama", "Roi_Louis", "Wotan", "DavLec",
			"Zephirr", "BillSilverlight", "Thefantasio974", "Thoyy", "Relient", "Edorock", "ShoukaSeikyo",
			"Fancois_Sama", "Notch", "Jeb_", "Siphano", "Zer4toR", "nemenems", "Mumbo", "_CrazyP_", "bspkrs", "C418",
			"Dinnerbone", "Grumm", "AntVenom", "pewdie", "CaptainSparklez", "Lunatrius", "Lycanite", "GregoriusT",
			"Etho", "SethBling", "direwolf20", "Bacon_Donut", "xlson", "JustePourJouer" };

	public static String[] BickerCraft_list = new String[] { "BickerCraft", "Eclipsen:a squid", "LightHN:a sheep",
			"jellyhunter:a green slime", "Bud_Bundy:a bear", "shwiny95: a enderman", "Calebis9:a spider",
			"Avaris11:a hamburger", "Y_D_domino:Jack O'Lantern with hat", "sleepiM1:pedobear", "RedPegasus:note block",
			"3423Kyle:trollface", "Itz_Assasin:Red Power Ranger", "chickolympics: a different chicken head",
			"omittingbread:pig skull", "neillyken:TMNT purple face", "Superyoshibros:Yoshi",
			"BaglesMan:Green Power Ranger", "msalihov: Working man", "Koeng101:Stormtrooper",
			"ARA275:Default Pokemon Trainer", "ghinterm:Cactus", "Samsam_Momo:Jake from Adventure Time",
			"MoulaTime:Grass head", "anamericandude:Wood head", "natejh:Pikachu", "JawaJish14:Chewbacca",
			"jakeypoo2005:Panda bear", "Merten123:Jack Sparrow", "EBOS23:Lava", "vennos93:Jigsaw",
			"spongebobtime2:Sub-Zero", "Scemm:Dispenser", "m0ose12321:Moose", "awsobuscus:Minecraft chicken",
			"Budwolf:Wolf", "pat2424:Walrus", "Esonicspeedster:Sonic the Hedgegog", "MrPeePeeCow:Domo", "henrik811:Pig",
			"gnhc:PSY", "tiger9a:Tiger", "erwintrude:another Chicken", "Mr_OfficeCreeper: The Joker",
			"morgans567:Zombie Pigman", "Poketostorm:Golden Steve Face", "safo:Iron Golem", "Freyr29:Donald Duck",
			"LycleLink:Penguin", "Herobrine:herobrine duh", "Natalieisawesome:a bunny", "rhyslarson:Batman",
			"heinzensmeinzen:ugly dude", "wolfgriffe:another wolf", "BoomerMan98: bricks", "Skipper3210:clown",
			"blader1176:? sign", "Ahiya:mudkip", "artseefartsee:monkey", "purplehayes:Wolverine", "iBlazeXrayZ:Blaze",
			"enaircf: Pencil eraser", "SquareHD:a dice [probably]", "semmieeeeee:Blue block", "crafterkid1k8:Ghast",
			"kyleman747:Grinch", "Comcastt:Front Squid Face", "sho2go27:Shiny blue light",
			"lancaster98:Companion Cube from Portal", "halo 99900:Black block", "lobwotscha:Wooden plank",
			"Zawern:Pumpkin", "ChoclateMuffin:a chocolate muffin", "edabonacci:Cobblestone",
			"spiriti1:Green monster eye", "pablo_asparagus:Nyan Cat", "Cheemz:Spider-man", "zsoccer23:Red angry bird",
			"kongHD:a monkey", "DeFrank:Donkey Kong", "andysam1999:Elmo", "pyrotnt1:Monster mouth", "hugge75:fox",
			"Becquerine:Redstone lamp", "HCTNT:TNT block", "IzanV:Kratos", "NiXWorld:a different Zombie",
			"forrynh:wood puppet", "nixzpatel:Naruto", "CraftingFire:Deadpool", "AGGRO965:another Deadpool",
			"begz:another penguin", "mrmaxfbk:another monster eye", "ZitterNipple:Duck", "Matt_5X4:Master Chief",
			"boyfromhell43:Viking", "LobsterDust:bullseye", "Solid_Snake3:Solid Snake", "Human_Kirby:Kirby",
			"ebiddytwister:Link", "Axterin:Connor Kenway", "Fransicodd:Blue Power Ranger",
			"Keanulaszlo:Crash Bandicoot", "Barnyard_Owl:Owl", "daminecraftninja:Ninja", "Azilizan:another Spider-Man",
			"Goodle:snowman", "TetroSpekt:robot", "Creem7116:Realistic man face", "Trusted23:another Solid Snake",
			"Explosion_936:Captain America", "roryo8:Falco Lombardi", "wetodd15:Old Herobrine",
			"ThunderRay34:Tron Helm", "jesusismyhomie:Old Man", "nicariox42:HD Mario Bros.", "NanobiteNpc:Rude Man",
			"samsamsam1234:Pirate", "lymibom:Elegant Man", "platypus99:Irish dwarf", "SuperGenoXP:Genie",
			"Monkeycapers:Cute Monkey", "fancypants39:Old Snake", "gothreaux:Lizard-Man", "JoeCMGIS:Dwarf",
			"Exeldoh:Turkey", "Kam627:Pink Power Ranger", "YObabewassup:somekind of Demon", "Villager:villager",
			"BigBadW0lf:Wolf", "flyman821:Guy with goggles", "xFlyier:Half human, half robot", "cam77890:Blue cow",
			"Pixology:No TV Signal", "MisterLamster:Charmander", "marvelousammar:Old Steve", "BabsHD:Lava Monster",
			"Nelson540:Captain America", "Linkwollf:Venom", "Preloom:Purple Cow", "TheVideoWiz:Bane",
			"epichickenuggets:Rainbow Dash", "General404:Knight", "TininchoMC:Ghast", "Robinho1502:Minion",
			"JoelPersson7:another minion", "_Trespassing_:Dumb face", "Crazybloxer:Dumb face with helmet",
			"ThePintor:Dumb and ugly face", "LegendzOfHoboz:another Dumb and ugly face", "citro30:The Joker",
			"burai564:Another hamburger", "king601:another penguin", "ferrase:Black Skull", "ptrinadadn:Wally",
			"The_SkittleZ:Guy that looks like Rambo", "Sugar_Cane_:Sugar Cane", "carterpaul:Platypus",
			"BJmatba:Blue light", "trolex213:Dark Purple Creeper", "unhappyworld:Dark Blue Creeper",
			"Scamalascazzu:Jiraiya", "facucarello12:Chuck Norris", "Praetoriian:Knight of Light",
			"chubbyderginger:Sun & Sky", "SHOCKN3SSK1D:Luffy", "Trusted23:Zombie", "Volc_Guy:Blaziken", "ThePoup:Clown",
			"Shinkao:Stitch", "Blast186:Sasuke Uchiha", "CandleGlow:Green Lantern", "zslorca:? Block" };

	public String Name;

	public String Description;

	public Head(String name) {
		this(name, "");
	}
	public Head(String name, String description) {
		Name = name;
		Description = description;
	}
	public ItemStack getHead() {
		ItemStack is = new ItemStack(Items.SKULL);
		is.setItemDamage(3);
		if (is.getTagCompound() == null)
			is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setString("SkullOwner", Name);
		String str = I18n.format("act.head");

		str = str.replaceFirst("PLAYERNAME", Name);
		str = str.replaceFirst("ADDTEXT", Description);
		is.setStackDisplayName(str);
		return is;
	}
}
