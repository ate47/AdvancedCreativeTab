package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.CommandUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class SCGive extends SubCommand {
	public SCGive() {
		super("give", "", CommandClickOption.suggestCommand, true);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length < 2 ? CommandUtils.getTabCompletion(CommandUtils.getItemList(), args)
				: new ArrayList<String>();
	}

	@Override
	public List<String> getAlias() {
		return new ArrayList<String>();
	}

	@Override
	public String getDescription() {
		return I18n.format("cmd.act.give");
	}

	@Override
	public String getSubCommandUsage(ICommandSender sender) {
		return getName() + " <" + I18n.format("cmd.act.ui.data") + ">";
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		if (args.length == 0) {
			ChatUtils.error("/" + mainCommand.getName() + " " + getSubCommandUsage(sender));
			return;
		}
		ItemStack is = ItemUtils.getFromGiveCode(CommandBase.buildString(args, 0));
		if (is != null)
			ItemUtils.give(Minecraft.getMinecraft(), is);
		else
			ChatUtils.error(I18n.format("gui.act.give.fail2"));
	}

}
