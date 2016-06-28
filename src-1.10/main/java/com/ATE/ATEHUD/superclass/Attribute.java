package com.ATE.ATEHUD.superclass;

public class Attribute {
	public String Name;
	/**
	 * Number or percentage ?
	 */
	public int Operation;
	/**
	 * Value
	 */
	public float Amount;
	public int UUIDMost;
	public int UUIDLeast;
	/**
	 * Object[]
	 * {
	 *    String[]{"NAME","LANG_NAME"}
	 * }
	 */
	public static final Object[] AttributeList=new Object[]{
		new String[]{"generic.maxHealth","attribute.name.generic.maxHealth"},
		new String[]{"generic.followRange","attribute.name.generic.followRange"},
		new String[]{"generic.knockbackResistance","attribute.name.generic.knockbackResistance"},
		new String[]{"generic.movementSpeed","attribute.name.generic.movementSpeed"},
		new String[]{"generic.attackDamage","attribute.name.generic.attackDamage"}
		
	};
	public String getAttriNbt(){
		return "{AttributeName:\""+Name+"\",Name:\""+Name+"\",Amount:"+Amount+",Operation:"+Operation+
				",UUIDLeast:"+UUIDLeast+",UUIDMost:"+UUIDMost+"}";
	}
	public Attribute(String name,int operation,float amount){
		Name=name;
		Operation=operation;
		Amount=amount;
		UUIDLeast=Integer.valueOf((int) (1000000.0*Math.random()));
		UUIDMost=Integer.valueOf((int) (1000000.0*Math.random()));
	}
	public Attribute(String name,int operation,float amount,int uuidLeast,int uuidMost){
		Name=name;
		Operation=operation;
		Amount=amount;
		UUIDLeast=uuidLeast;
		UUIDMost=uuidMost;
	}
	public String toString() {
		return "Attribute [Name=" + Name + ", Operation=" + Operation
				+ ", Amount=" + Amount + ", UUIDMost=" + UUIDMost
				+ ", UUIDLeast=" + UUIDLeast + "]";
	}
}
