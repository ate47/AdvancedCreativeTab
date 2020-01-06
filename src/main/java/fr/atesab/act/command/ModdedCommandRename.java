package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public class ModdedCommandRename extends ModdedCommand {

	public ModdedCommandRename() {
		super("rename", "cmd.act.rename", CommandClickOption.suggestCommand);
		addAlias("r");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		return command.then(Commands.argument("itemname", StringArgumentType.greedyString()).executes(c -> {
			Minecraft mc = Minecraft.getInstance();
			ItemStack is = mc.player.getHeldItemMainhand();
			if (is != null) {
				is.setDisplayName(new StringTextComponent(StringArgumentType.getString(c, "itemname")
						.replaceAll("&([0-9a-fA-FrRk-oK-O])", ChatUtils.MODIFIER + "$1")
						.replaceAll("&" + ChatUtils.MODIFIER, "&")));
				ItemUtils.give(is, 36 + mc.player.inventory.currentItem);
			}
			return 0;
		}));
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			Minecraft mc = Minecraft.getInstance();
			ItemStack is = mc.player.getHeldItemMainhand();
			if (is != null) {
				is = is.copy();
				is.clearCustomName();
				ItemUtils.give(is, 36 + mc.player.inventory.currentItem);
			}
			return 0;
		};
	}

}
