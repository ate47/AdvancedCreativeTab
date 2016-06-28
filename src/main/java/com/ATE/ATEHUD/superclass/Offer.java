package com.ATE.ATEHUD.superclass;

import net.minecraft.item.ItemStack;

public class Offer {
	private int RewardExp;
	private int MaxUses;
	private int Uses;
	private ItemStack Buy;
	private ItemStack BuyB;
	private ItemStack Sell;

	public int getRewardExp(){return RewardExp;}
	public int getMaxUses(){return MaxUses;}
	public int getUses(){return Uses;}
	public ItemStack getBuy(){return Buy;}
	public ItemStack getBuyB(){return BuyB;}
	public ItemStack getSell(){return Sell;}
	
	public void setRewardExp(int value){RewardExp=value;}
	public void setMaxUses(int value){MaxUses=value;}
	public void setUses(int value){Uses=value;}
	public void setBuy(ItemStack value){Buy=value;}
	public void setBuyB(ItemStack value){BuyB=value;}
	public void setSell(ItemStack value){Sell=value;}
	/**
	 * 
	 * @param rewardExp 1 or 0 (true/false) - true if this trade will provide XP orb drops.
	 * @param maxUses The maximum number of times this trade can be used before it is disabled. Increases by a random amount from 2 to 12 when offers are refreshed.
	 * @param uses The number of times this trade has been used. The trade becomes disabled when this is greater or equal to maxUses.
	 * @param buy The first 'cost' item, without the Slot tag.
	 * @param buyB May not exist. The second 'cost' item, without the Slot tag.
	 * @param sell The item being sold for each set of cost items, without the Slot tag.
	 */
	public Offer(int rewardExp, int maxUses,int uses,ItemStack buy,ItemStack buyB,ItemStack sell) {
		RewardExp=rewardExp;
		MaxUses=maxUses;
		Uses=uses;
		Buy=buy;
		BuyB=buyB;
		Sell=sell;
	}
	/**
	 * 
	 * @param rewardExp 1 or 0 (true/false) - true if this trade will provide XP orb drops.
	 * @param maxUses The maximum number of times this trade can be used before it is disabled. Increases by a random amount from 2 to 12 when offers are refreshed.
	 * @param uses The number of times this trade has been used. The trade becomes disabled when this is greater or equal to maxUses.
	 * @param buy The first 'cost' item, without the Slot tag.
	 * @param sell The item being sold for each set of cost items, without the Slot tag.
	 */
	public Offer(int rewardExp, int maxUses,int uses,ItemStack buy,ItemStack sell) {
		RewardExp=rewardExp;
		MaxUses=maxUses;
		Uses=uses;
		Buy=buy;
		BuyB=null;
		Sell=sell;
		
	}
}
