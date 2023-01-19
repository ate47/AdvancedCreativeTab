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
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class SCHead extends SubCommand {

	public SCHead() {
		super("head", "cmd.act.head", CommandClickOption.suggestCommand);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return CommandUtils.getTabCompletion(CommandUtils.getPlayerList(), args);
	}

	@Override
	public List<String> getAlias() {
		return new ArrayList<>();
	}

	@Override
	public String getDescription() {
		return I18n.format("cmd.act.head");
	}

	@Override
	public String getSubCommandUsage(ICommandSender sender) {
		return getName() + " [" + I18n.format("gui.act.config.name") + "]";
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		Minecraft mc = Minecraft.getMinecraft();
		if (args.length == 0)
			args = new String[] { mc.getSession().getUsername() };
		try {
			ItemUtils.getHeads(args).forEach(ItemUtils::give);
		} catch (Exception e) {
			ChatUtils.error(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
