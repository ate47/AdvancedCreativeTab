package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public class ModdedCommandRename extends ModdedCommand {

	public ModdedCommandRename() {
		super("rename", "cmd.act.rename", CommandClickOption.suggestCommand);
		addAlias("r");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
			LiteralArgumentBuilder<CommandSourceStack> command) {
		return command.then(Commands.argument("itemname", StringArgumentType.greedyString()).executes(c -> {
			Minecraft mc = Minecraft.getInstance();
			ItemStack is = mc.player.getMainHandItem();
			if (is != null) {
				is.setHoverName(new TextComponent(StringArgumentType.getString(c, "itemname")
						.replaceAll("&([0-9a-fA-FrRk-oK-O])", ChatUtils.MODIFIER + "$1")
						.replaceAll("&" + ChatUtils.MODIFIER, "&")));
				ItemUtils.give(is, 36 + mc.player.getInventory().selected);
			}
			return 0;
		}));
	}

	@Override
	protected Command<CommandSourceStack> onNoArgument() {
		return c -> {
			Minecraft mc = Minecraft.getInstance();
			ItemStack is = mc.player.getMainHandItem();
			if (is != null) {
				is = is.copy();
				is.resetHoverName();
				ItemUtils.give(is, 36 + mc.player.getInventory().selected);
			}
			return 0;
		};
	}

}
