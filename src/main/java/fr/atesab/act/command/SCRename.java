package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class SCRename extends SubCommand {

	public SCRename() {
		super("rename", "cmd.act.rename", CommandClickOption.suggestCommand);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return new ArrayList<>();
	}

	@Override
	public List<String> getAlias() {
		return new ArrayList<>();
	}

	@Override
	public String getDescription() {
		return I18n.format("cmd.act.rename");
	}

	@Override
	public String getSubCommandUsage(ICommandSender sender) {
		return getName() + " [" + I18n.format("cmd.act.ui.name") + "]";
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		Minecraft mc = Minecraft.getMinecraft();
		ItemStack is = mc.thePlayer.getHeldItem();
		if (is == null) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("[ACT] You cannot rename your hand !"));
			return;
		}
		if (args.length == 0)
			is.clearCustomName();
		else
			is.setStackDisplayName(
					CommandBase.buildString(args, 0).replaceAll("&([0-9a-fA-FrRk-oK-O])", ChatUtils.MODIFIER + "$1")
							.replaceAll("&" + ChatUtils.MODIFIER, "&"));
		ItemUtils.give(mc, is, 36 + mc.thePlayer.inventory.currentItem);
	}

}
