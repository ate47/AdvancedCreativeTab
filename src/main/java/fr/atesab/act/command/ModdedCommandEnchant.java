package fr.atesab.act.command;

import java.util.Map;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.ACTMod;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class ModdedCommandEnchant extends ModdedCommand {

	public ModdedCommandEnchant() {
		super("enchant", "cmd.act.enchant", CommandClickOption.suggestCommand);
		addAlias("en");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		return command.then(Commands.argument("enchantname", EnchantmentArgument.itemEnchantment()).executes(c -> {
			Enchantment e = EnchantmentArgument.getEnchantment(c, "enchantname");
			Minecraft mc = Minecraft.getInstance();
			ItemStack is = mc.player.getHeldItemMainhand().copy();
			if (is != null) {
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(is);
				map.put(e, e.getMaxLevel());
				EnchantmentHelper.setEnchantments(map, is);
				ItemUtils.give(is, 36 + mc.player.inventory.currentItem);
			}
			return 0;
		}).then(Commands.argument("enchantlevel", IntegerArgumentType.integer()).executes(c -> {
			Enchantment e = EnchantmentArgument.getEnchantment(c, "enchantname");
			int lvl = IntegerArgumentType.getInteger(c, "enchantlevel");
			Minecraft mc = Minecraft.getInstance();
			ItemStack is = mc.player.getHeldItemMainhand().copy();
			if (is != null) {
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(is);
				map.put(e, lvl);
				EnchantmentHelper.setEnchantments(map, is);
				ItemUtils.give(is, 36 + mc.player.inventory.currentItem);
			}
			return 0;
		})));
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			Minecraft mc = Minecraft.getInstance();
			ItemStack is = mc.player.getHeldItemMainhand();
			if (is != null) {
				is = is.copy();
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(is);
				for (EnchantmentData data : EnchantmentHelper.buildEnchantmentList(ACTMod.RANDOM, is, 30, true))
					map.put(data.enchantment, data.enchantmentLevel);
				EnchantmentHelper.setEnchantments(map, is);
				ItemUtils.give(is, 36 + mc.player.inventory.currentItem);
			}
			return 0;
		};
	}
}
