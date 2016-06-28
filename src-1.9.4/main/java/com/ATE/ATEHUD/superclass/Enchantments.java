package com.ATE.ATEHUD.superclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Object;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.ATE.ATEHUD.ModMain;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class Enchantments {

    @Nullable
    private static Enchantment getRegisteredEnchantment(String id)
    {
        Enchantment enchantment = (Enchantment)Enchantment.REGISTRY.getObject(new ResourceLocation(id));
        return enchantment;
    }
	public static int theUnknowTypeId;
	public static String[] TypeList,EnchantmentList;
	public static Enchantments[] enchantments;

	public static void defineEnchantments(){
		Field[] flds=net.minecraft.init.Enchantments.class.getFields();int j=0;
		String[] enchs=new String[flds.length];
		for (int i = 0; i < flds.length; i++) {
			try {
				Enchantment ench=(Enchantment) flds[i].get(Enchantment.class);
				enchs[j]=ench.type.name()+","+ench.getName()+","+ench.getEnchantmentID(ench)+","
						+net.minecraft.init.Enchantments.class.getName()+","+
						flds[i].getName();j++;
			} catch (Exception e) {}
		}
		String[] enchs2=new String[j];
		for (int i = 0; i < j; i++) {
			enchs2[i]=enchs[i];
		}
		EnchantmentList=enchs2;
		Field[] flds2 =EnumEnchantmentType.class.getFields();
		String[] types=new String[flds2.length];j=0;
		for (int i = 0; i < flds2.length; i++) {
			try {
				EnumEnchantmentType value=(EnumEnchantmentType) flds2[i].get(EnumEnchantmentType.class);
				types[j]=value.name();j++;
			} catch (Exception e) {}
		}
		String[] types2=new String[j];
		for (int i = 0; i < types2.length; i++) {
			types2[i]=types[i];
		}
		TypeList=types2;
		String[] TypeList1=new String[TypeList.length+1];
    	boolean hasUnknow=false;
    	for (int i = 0; i < TypeList.length; i++) {
    		if(TypeList[i]=="UNKNOW"){
    			hasUnknow=true;
    			theUnknowTypeId=i;
    		}
		}
    	if(!hasUnknow) {
	    	for (int i = 0; i < TypeList.length; i++) {
	    		TypeList1[i]=TypeList[i];
			}
	    	TypeList1[TypeList1.length-1]="UNKNOW";
	    	theUnknowTypeId=TypeList1.length-1;
	    	TypeList=TypeList1;
    	}
		Enchantments[] enchantments1=new Enchantments[EnchantmentList.length];
		int finalSize=EnchantmentList.length;j=0;
		for (int i = 0; i < EnchantmentList.length; i++) {
			String[] strl=EnchantmentList[i].split(",");
			if(strl.length==5){
				try {
					String type=strl[0].toUpperCase();
					String name=strl[1].toLowerCase();
					int id=Integer.valueOf(strl[2]);
					Enchantment enchantment=(Enchantment) Class.forName(strl[3]).getField(strl[4]).get(Enchantment.class);
					if(enchantment!=null){
						Enchantments a=new Enchantments(new EnchantmentType(name, id), name, id, enchantment);
						if(a!=null){
							enchantments1[j]=a;
							j++;
						}
					}
				} catch (Exception e) {}
			} else {
				finalSize--;
			}
		}
		enchantments=new Enchantments[j];
		for (int i = 0; i < enchantments.length; i++) {
			enchantments[i]=enchantments1[i];
		}
		for (int i = 0; i < enchantments.length; i++) {
			System.out.println(enchantments[i].toString());
		}
	} 
	public Enchantment getEnchantment(){return enchantment;}
	public boolean isOneLevel(){return OneLevel;}
	public int getId(){return Id;}
	public String Name;
	private Enchantment enchantment;
	private boolean OneLevel=false;
	private int Id;
	public Enchantments(EnchantmentType type,String name,int id,Enchantment enchantment){
		Id=id;
		Name=name;
		if(enchantment.getMaxLevel()==1)
		OneLevel=true;
		this.enchantment=enchantment;
	}
	public String toString(){
		return "Enchantments Name:"+Name+", Id:"+Id+", Enchantment:"+enchantment.toString();
	}
}
