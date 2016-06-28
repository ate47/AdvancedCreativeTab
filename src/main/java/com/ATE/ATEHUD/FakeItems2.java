package com.ATE.ATEHUD;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Session;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

public class FakeItems2 extends Item {
	@Deprecated
    public FakeItems2(){
		setCreativeTab(ModMain.ATEcreativeTAB2);
		setHasSubtypes(true);
	}
    public static ItemStack getHead(String name, String addtext) {
    	ItemStack is=new ItemStack(Items.skull);
    	is.setItemDamage(3);
    	if (is.getTagCompound() == null) is.setTagCompound(new NBTTagCompound());
    	if (is.getTagCompound() != null) is.getTagCompound().setString("SkullOwner", name);
    	String str = LanguageRegistry.instance().getStringLocalization("act.head");
    	
    	str=str.replaceFirst("PLAYERNAME", name);
    	str=str.replaceFirst("ADDTEXT", addtext);
    	is.setStackDisplayName(str);
    	return is;
    }

    public static ItemStack setMeta(ItemStack is, int meta,String name) {
    	is.setItemDamage(meta);
    	is.setStackDisplayName(name);
    	return is;
    }
    public static ItemStack setName(ItemStack is,String name) {
    	is.setStackDisplayName(name);
    	return is;
    }
    public static ItemStack getHead(String name) {
    	return getHead(name, "");
    }

    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems){
    if(tab!=CreativeTabs.tabAllSearch){
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4, LanguageRegistry.instance().getStringLocalization("act.devfriend"))); //5
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9
	
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//1
	    subItems.add(getHead("ATE47","(Dev)"));			//1
	    subItems.add(getHead("DarkArthurCT","(El Tardos)"));	//2
	    subItems.add(getHead("theluckier59"));	//3
	    subItems.add(getHead("PikSel42","(Babycraft Owner)"));		//4
	    subItems.add(getHead("AlphaSeven278"));	//5
	    subItems.add(getHead("Paralogos"));			//7
	    subItems.add(getHead(Minecraft.getMinecraft().getSession().getUsername()));		//6
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//1
	    
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4,LanguageRegistry.instance().getStringLocalization("act.mhf"))); //5
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9
	
	    subItems.add(getHead("MHF_Alex","(MHF)"));				//1
	    subItems.add(getHead("MHF_Blaze","(MHF)"));				//2
	    subItems.add(getHead("MHF_CaveSpider","(MHF)"));				//3
	    subItems.add(getHead("MHF_Chicken","(MHF)"));				//4
	    subItems.add(getHead("MHF_Cow","(MHF)"));				//5
	    subItems.add(getHead("MHF_Creeper","(MHF)"));				//6
	    subItems.add(getHead("MHF_Enderman","(MHF)"));				//7
	    subItems.add(getHead("MHF_Ghast","(MHF)"));				//8
	    subItems.add(getHead("MHF_Herobrine","(MHF)"));				//9
	
	    subItems.add(getHead("MHF_Golem","(MHF)"));				//1
	    subItems.add(getHead("MHF_LavaSlime","(MHF)"));				//2
	    subItems.add(getHead("MHF_MushroomCow","(MHF)"));				//3
	    subItems.add(getHead("MHF_Ocelot","(MHF)"));				//4
	    subItems.add(getHead("MHF_Pig","(MHF)"));				//5
	    subItems.add(getHead("MHF_PigZombie","(MHF)"));				//6
	    subItems.add(getHead("MHF_Sheep","(MHF)"));				//7
	    subItems.add(getHead("MHF_Skeleton","(MHF)"));				//8
	    subItems.add(getHead("MHF_Slime","(MHF)"));				//9
	
	    subItems.add(getHead("MHF_Spider","(MHF)"));				//1
	    subItems.add(getHead("MHF_Squid","(MHF)"));				//2
	    subItems.add(getHead("MHF_Steve","(MHF)"));				//3
	    subItems.add(getHead("MHF_Villager","(MHF)"));				//4
	    subItems.add(getHead("MHF_WSkeleton","(MHF)"));				//5
	    subItems.add(getHead("MHF_Zombie","(MHF)"));				//6
	    subItems.add(getHead("MHF_Cactus","(MHF)"));				//7
	    subItems.add(getHead("MHF_TNT","(MHF)"));				//8
	    subItems.add(getHead("MHF_TNT2","(MHF)"));				//9
	
	    subItems.add(getHead("MHF_Cake","(MHF)"));				//1
	    subItems.add(getHead("MHF_Chest","(MHF)"));				//2
	    subItems.add(getHead("MHF_CoconutB","(MHF)"));				//3
	    subItems.add(getHead("MHF_CoconutG","(MHF)"));				//4
	    subItems.add(getHead("MHF_Melon","(MHF)"));				//5
	    subItems.add(getHead("MHF_OakLog","(MHF)"));				//6
	    subItems.add(getHead("MHF_Present1","(MHF)"));				//7
	    subItems.add(getHead("MHF_Present2","(MHF)"));				//8
	    subItems.add(getHead("MHF_Pumpkin","(MHF)"));				//9
	
	    subItems.add(getHead("MHF_ArrowUp","(MHF)"));							//1
	    subItems.add(getHead("MHF_ArrowDown","(MHF)"));							//2
	    subItems.add(getHead("MHF_ArrowLeft","(MHF)"));							//3
	    subItems.add(getHead("MHF_ArrowRight","(MHF)"));						//4
	    subItems.add(getHead("MHF_Exclamation","(MHF)"));						//5
	    subItems.add(getHead("MHF_Question","(MHF)"));							//6
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//7
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//8
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//9
	
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4,LanguageRegistry.instance().getStringLocalization("act.vipdev"))); //5
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9
	    
	    subItems.add(getHead("Aypierre"));			//1
	    subItems.add(getHead("Aurelien_Sama"));		//2
	    subItems.add(getHead("Roi_Louis"));			//3
	    subItems.add(getHead("Wotan"));				//4
	    subItems.add(getHead("DavLec"));			//5
	    subItems.add(getHead("Zephirr"));			//6
	    subItems.add(getHead("BillSilverlight"));	//7
	    subItems.add(getHead("Thefantasio974"));	//8
	    subItems.add(getHead("Thoyy"));				//9
	
	    subItems.add(getHead("Relient"));			//1
	    subItems.add(getHead("Edorock"));			//2
	    subItems.add(getHead("ShoukaSeikyo"));		//3
	    subItems.add(getHead("Fancois_Sama"));		//4
	    subItems.add(getHead("Notch"));				//5
	    subItems.add(getHead("Jeb_"));				//6
	    subItems.add(getHead("Siphano"));			//7
	    subItems.add(getHead("Zer4toR"));			//8
	    subItems.add(getHead("nemenems"));			//9
	
	    subItems.add(getHead("Mumbo"));			//1
	    subItems.add(getHead("_CrazyP_"));			//2
	    subItems.add(getHead("bspkrs"));		//3
	    subItems.add(getHead("C418"));		//4
	    subItems.add(getHead("Dinnerbone"));				//5
	    subItems.add(getHead("Grumm"));				//6
	    subItems.add(getHead("AntVenom"));			//7
	    subItems.add(getHead("pewdie"));			//8
	    subItems.add(getHead("CaptainSparklez"));			//9
	
	    subItems.add(getHead("Lunatrius"));			//1
	    subItems.add(getHead("Lycanite"));			//2
	    subItems.add(getHead("GregoriusT"));		//3
	    subItems.add(getHead("Etho"));				//4
	    subItems.add(getHead("SethBling"));			//5
	    subItems.add(getHead("direwolf20"));		//6
	    subItems.add(getHead("Bacon_Donut"));		//7
	    subItems.add(getHead("xlson"));				//8
	    subItems.add(getHead("JustePourJouer"));	//9
	
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4,LanguageRegistry.instance().getStringLocalization("act.list"))); //5
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
	    subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9
	    
	    subItems.add(getHead("BickerCraft"));
	    subItems.add(getHead("Eclipsen","a squid"));
	    subItems.add(getHead("LightHN","a sheep"));
	    subItems.add(getHead("jellyhunter","a green slime"));
	    subItems.add(getHead("Bud_Bundy","a bear"));
	    subItems.add(getHead("shwiny95"," a enderman"));
	    subItems.add(getHead("Calebis9","a spider"));
	    subItems.add(getHead("Avaris11","a hamburger"));
	    subItems.add(getHead("Y_D_domino","Jack O'Lantern with hat"));
	
	    subItems.add(getHead("sleepiM1","pedobear"));
	    subItems.add(getHead("RedPegasus","note block"));
	    subItems.add(getHead("3423Kyle","trollface"));
	    subItems.add(getHead("Itz_Assasin","Red Power Ranger"));
	    subItems.add(getHead("chickolympics"," a different chicken head"));
	    subItems.add(getHead("omittingbread","pig skull"));
	    subItems.add(getHead("neillyken","TMNT purple face"));
	    subItems.add(getHead("Superyoshibros","Yoshi"));
	    subItems.add(getHead("BaglesMan","Green Power Ranger"));
	    
	    subItems.add(getHead("msalihov"," Working man"));
	    subItems.add(getHead("Koeng101","Stormtrooper"));
	    subItems.add(getHead("ARA275","Default Pokemon Trainer"));
	    subItems.add(getHead("ghinterm","Cactus"));
	    subItems.add(getHead("Samsam_Momo","Jake from Adventure Time"));
	    subItems.add(getHead("MoulaTime","Grass head"));
	    subItems.add(getHead("anamericandude","Wood head"));
	    subItems.add(getHead("natejh","Pikachu"));
	    subItems.add(getHead("JawaJish14","Chewbacca"));
	    
	    subItems.add(getHead("jakeypoo2005","Panda bear"));
	    subItems.add(getHead("Merten123","Jack Sparrow"));
	    subItems.add(getHead("EBOS23","Lava"));
	    subItems.add(getHead("vennos93","Jigsaw"));
	    subItems.add(getHead("spongebobtime2","Sub-Zero"));
	    subItems.add(getHead("Scemm","Dispenser"));
	    subItems.add(getHead("m0ose12321","Moose"));
	    subItems.add(getHead("awsobuscus","Minecraft chicken"));
	    subItems.add(getHead("Budwolf","Wolf"));
	    
	    subItems.add(getHead("pat2424","Walrus"));
	    subItems.add(getHead("Esonicspeedster","Sonic the Hedgegog"));
	    subItems.add(getHead("MrPeePeeCow","Domo"));
	    subItems.add(getHead("henrik811","Pig"));
	    subItems.add(getHead("gnhc","PSY"));
	    subItems.add(getHead("tiger9a","Tiger"));
	    subItems.add(getHead("erwintrude","another Chicken"));
	    subItems.add(getHead("Mr_OfficeCreeper"," The Joker"));
	    subItems.add(getHead("morgans567","Zombie Pigman"));
	    
	    subItems.add(getHead("Poketostorm","Golden Steve Face"));
	    subItems.add(getHead("safo","Iron Golem"));
	    subItems.add(getHead("Freyr29","Donald Duck"));
	    subItems.add(getHead("LycleLink","Penguin"));
	    subItems.add(getHead("Herobrine","herobrine duh"));
	    subItems.add(getHead("Natalieisawesome","a bunny"));
	    subItems.add(getHead("rhyslarson","Batman"));
	    subItems.add(getHead("heinzensmeinzen","ugly dude"));
	    subItems.add(getHead("wolfgriffe","another wolf"));
	    
	    subItems.add(getHead("BoomerMan98"," bricks"));
	    subItems.add(getHead("Skipper3210","clown"));
	    subItems.add(getHead("blader1176","? sign"));
	    subItems.add(getHead("Ahiya","mudkip"));
	    subItems.add(getHead("artseefartsee","monkey"));
	    subItems.add(getHead("purplehayes","Wolverine"));
	    subItems.add(getHead("iBlazeXrayZ","Blaze"));
	    subItems.add(getHead("enaircf"," Pencil eraser"));
	    subItems.add(getHead("SquareHD","a dice [probably]"));
	    
	    subItems.add(getHead("semmieeeeee","Blue block"));
	    subItems.add(getHead("crafterkid1k8","Ghast"));
	    subItems.add(getHead("kyleman747","Grinch"));
	    subItems.add(getHead("Comcastt","Front Squid Face"));
	    subItems.add(getHead("sho2go27","Shiny blue light"));
	    subItems.add(getHead("lancaster98","Companion Cube from Portal"));
	    subItems.add(getHead("halo 99900","Black block"));
	    subItems.add(getHead("lobwotscha","Wooden plank"));
	    subItems.add(getHead("Zawern","Pumpkin"));
	    
	    subItems.add(getHead("ChoclateMuffin","a chocolate muffin"));
	    subItems.add(getHead("edabonacci","Cobblestone"));
	    subItems.add(getHead("spiriti1","Green monster eye"));
	    subItems.add(getHead("pablo_asparagus","Nyan Cat"));
	    subItems.add(getHead("Cheemz","Spider-man"));
	    subItems.add(getHead("zsoccer23","Red angry bird"));
	    subItems.add(getHead("kongHD","a monkey"));
	    subItems.add(getHead("DeFrank","Donkey Kong"));
	    subItems.add(getHead("andysam1999","Elmo"));
	    
	    subItems.add(getHead("pyrotnt1","Monster mouth"));
	    subItems.add(getHead("hugge75","fox"));
	    subItems.add(getHead("Becquerine","Redstone lamp"));
	    subItems.add(getHead("HCTNT","TNT block"));
	    subItems.add(getHead("IzanV","Kratos"));
	    subItems.add(getHead("NiXWorld","a different Zombie"));
	    subItems.add(getHead("forrynh","wood puppet"));
	    subItems.add(getHead("nixzpatel","Naruto"));
	    subItems.add(getHead("CraftingFire","Deadpool"));
	    
	    subItems.add(getHead("AGGRO965","another Deadpool"));
	    subItems.add(getHead("begz","another penguin"));
	    subItems.add(getHead("mrmaxfbk","another monster eye"));
	    subItems.add(getHead("ZitterNipple","Duck"));
	    subItems.add(getHead("Matt_5X4","Master Chief"));
	    subItems.add(getHead("boyfromhell43","Viking"));
	    subItems.add(getHead("LobsterDust","bullseye"));
	    subItems.add(getHead("Solid_Snake3","Solid Snake"));
	    subItems.add(getHead("Human_Kirby","Kirby"));
	    
	    subItems.add(getHead("ebiddytwister","Link"));
	    subItems.add(getHead("Axterin","Connor Kenway"));
	    subItems.add(getHead("Fransicodd","Blue Power Ranger"));
	    subItems.add(getHead("Keanulaszlo","Crash Bandicoot"));
	    subItems.add(getHead("Barnyard_Owl","Owl"));
	    subItems.add(getHead("daminecraftninja","Ninja"));
	    subItems.add(getHead("Azilizan","another Spider-Man"));
	    subItems.add(getHead("Goodle","snowman"));
	    subItems.add(getHead("TetroSpekt","robot"));
	    
	    subItems.add(getHead("Creem7116","Realistic man face"));
	    subItems.add(getHead("Trusted23","another Solid Snake"));
	    subItems.add(getHead("Explosion_936","Captain America"));
	    subItems.add(getHead("roryo8","Falco Lombardi"));
	    subItems.add(getHead("wetodd15","Old Herobrine"));
	    subItems.add(getHead("ThunderRay34","Tron Helm"));
	    subItems.add(getHead("jesusismyhomie","Old Man"));
	    subItems.add(getHead("nicariox42","HD Mario Bros."));
	    subItems.add(getHead("NanobiteNpc","Rude Man"));
	    
	    subItems.add(getHead("samsamsam1234","Pirate"));
	    subItems.add(getHead("lymibom","Elegant Man"));
	    subItems.add(getHead("platypus99","Irish dwarf"));
	    subItems.add(getHead("SuperGenoXP","Genie"));
	    subItems.add(getHead("Monkeycapers","Cute Monkey"));
	    subItems.add(getHead("fancypants39","Old Snake"));
	    subItems.add(getHead("gothreaux","Lizard-Man"));
	    subItems.add(getHead("JoeCMGIS","Dwarf"));
	    subItems.add(getHead("Exeldoh","Turkey"));
	    
	    subItems.add(getHead("Kam627","Pink Power Ranger"));
	    subItems.add(getHead("YObabewassup","somekind of Demon"));
	    subItems.add(getHead("Villager","villager"));
	    subItems.add(getHead("BigBadW0lf","Wolf"));
	    subItems.add(getHead("flyman821","Guy with goggles"));
	    subItems.add(getHead("xFlyier","Half human, half robot"));
	    subItems.add(getHead("cam77890","Blue cow"));
	    subItems.add(getHead("Pixology","No TV Signal"));
	    subItems.add(getHead("MisterLamster","Charmander"));
	    
	    subItems.add(getHead("marvelousammar","Old Steve"));
	    subItems.add(getHead("BabsHD","Lava Monster"));
	    subItems.add(getHead("Nelson540","Captain America"));
	    subItems.add(getHead("Linkwollf","Venom"));
	    subItems.add(getHead("Preloom","Purple Cow"));
	    subItems.add(getHead("TheVideoWiz","Bane"));
	    subItems.add(getHead("epichickenuggets","Rainbow Dash"));
	    subItems.add(getHead("General404","Knight"));
	    subItems.add(getHead("TininchoMC","Ghast"));
	    
	    subItems.add(getHead("Robinho1502","Minion"));
	    subItems.add(getHead("JoelPersson7","another minion"));
	    subItems.add(getHead("_Trespassing_","Dumb face"));
	    subItems.add(getHead("Crazybloxer","Dumb face with helmet"));
	    subItems.add(getHead("ThePintor","Dumb and ugly face"));
	    subItems.add(getHead("LegendzOfHoboz","another Dumb and ugly face"));
	    subItems.add(getHead("citro30","The Joker"));
	    subItems.add(getHead("burai564","Another hamburger"));
	    subItems.add(getHead("king601","another penguin"));
	    
	    subItems.add(getHead("ferrase","Black Skull"));
	    subItems.add(getHead("ptrinadadn","Wally"));
	    subItems.add(getHead("The_SkittleZ","Guy that looks like Rambo"));
	    subItems.add(getHead("Sugar_Cane_","Sugar Cane"));
	    subItems.add(getHead("carterpaul","Platypus"));
	    subItems.add(getHead("BJmatba","Blue light"));
	    subItems.add(getHead("trolex213","Dark Purple Creeper"));
	    subItems.add(getHead("unhappyworld","Dark Blue Creeper"));
	    subItems.add(getHead("Scamalascazzu","Jiraiya"));
	    
	    subItems.add(getHead("facucarello12","Chuck Norris"));
	    subItems.add(getHead("Praetoriian","Knight of Light"));
	    subItems.add(getHead("chubbyderginger","Sun & Sky"));
	    subItems.add(getHead("SHOCKN3SSK1D","Luffy"));
	    subItems.add(getHead("Trusted23","Zombie"));
	    subItems.add(getHead("Volc_Guy","Blaziken"));
	    subItems.add(getHead("ThePoup","Clown"));
	    subItems.add(getHead("Shinkao","Stitch"));
	    subItems.add(getHead("Blast186","Sasuke Uchiha"));
	    
	    subItems.add(getHead("CandleGlow","Green Lantern"));
	    subItems.add(getHead("zslorca","? Block"));
    }
}

}
