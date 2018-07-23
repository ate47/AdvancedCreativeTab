package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.utils.ChatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class SCHelp extends SubCommand {

	public static ITextComponent getCommandPage(String commandName, int page, int maxPage) {
		ITextComponent c = new TextComponentString("\n");
		if (page != 1)
			c.appendSibling(new TextComponentString("<--").setChatStyle(new Style()
					.setChatClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName + " " + (int) (page - 1)))
					.setColor(TextFormatting.RED)));
		else
			c.appendSibling(new TextComponentString("<--").setChatStyle(new Style().setColor(TextFormatting.DARK_GRAY)));
		c.appendSibling(new TextComponentString(" | ").setChatStyle(new Style().setColor(TextFormatting.GRAY)));
		if (page != maxPage)
			c.appendSibling(new TextComponentString("-->").setChatStyle(new Style()
					.setChatClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName + " " + (int) (page + 1)))
					.setColor(TextFormatting.RED)));
		else
			c.appendSibling(new TextComponentString("-->").setChatStyle(new Style().setColor(TextFormatting.DARK_GRAY)));
		return c;
	}

	public static ITextComponent getHelp(String commandName, SubCommand cmd, ICommandSender sender) {
		ITextComponent c = new TextComponentString("\n");
		Style cs = new Style().setColor(TextFormatting.GOLD);
		switch (cmd.getClickOption()) {
		case suggestCommand:
			cs.setChatClickEvent(
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + commandName + " " + cmd.getName() + " "))
					.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new TextComponentString(I18n.format("cmd.act.help.click"))
									.setChatStyle(new Style().setColor(TextFormatting.YELLOW))));
			break;
		default:
			cs.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName + " " + cmd.getName()))
					.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new TextComponentString(I18n.format("cmd.act.help.do"))
									.setChatStyle(new Style().setColor(TextFormatting.YELLOW))));
			break;
		}
		c.appendSibling(new TextComponentString("/" + commandName + " " + cmd.getSubCommandUsage(sender)).setChatStyle(cs));
		c.appendSibling(new TextComponentString(" : ").setChatStyle(new Style().setColor(TextFormatting.GRAY)));
		c.appendSibling(
				new TextComponentString(cmd.getDescription()).setChatStyle(new Style().setColor(TextFormatting.WHITE)));
		return c;
	}

	private final List<String> aliases;

	private final String title;

	private final int elm;

	public SCHelp(String title, int elementByPage) {
		super("help", I18n.format("cmd.act.help.cmd"), CommandClickOption.suggestCommand);
		this.title = title;
		this.elm = elementByPage;
		aliases = new ArrayList<String>();
		aliases.add(getName());
		aliases.add("?");
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return new ArrayList<String>();
	}

	public List<String> getAlias() {
		return this.aliases;
	}

	@Override
	public String getDescription() {
		return I18n.format("cmd.act.help.cmd");
	}

	public String getSubCommandUsage(ICommandSender sender) {
		return getName() + " (" + I18n.format("cmd.act.ui.page") + ")";
	}

	public ArrayList<SubCommand> getVisibleSubCommand(MainCommand mainCommand) {
		ArrayList<SubCommand> sc = new ArrayList<SubCommand>();
		for (int i = 0; i < mainCommand.subCommands.size(); i++) {
			if (mainCommand.subCommands.get(i).isDisplayInHelp())
				sc.add(mainCommand.subCommands.get(i));
		}
		return sc;
	}

	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		ArrayList<SubCommand> sc = getVisibleSubCommand(mainCommand);
		int page = 0;
		int maxPage = mainCommand.subCommands.size() / elm;
		if (args.length == 1) {
			try {
				page = Integer.valueOf(args[0]) - 1;
				if (page > maxPage)
					page = maxPage;
				else if (page < 0)
					page = 0;
			} catch (Exception e) {
				ChatUtils.error(I18n.format("cmd.act.NaN", '\"' + args[0] + '\"'));
				return;
			}
		}
		ITextComponent help = new TextComponentString(
				"-- " + I18n.format("cmd.act.help", title) + " (" + (page + 1) + " / " + (maxPage + 1) + ") --")
						.setChatStyle(new Style().setColor(TextFormatting.RED));

		for (int i = page * elm; i < (page + 1) * elm && i < mainCommand.subCommands.size(); i++) {
			help.appendSibling(getHelp(mainCommand.getCommandName(), mainCommand.subCommands.get(i), sender));
		}
		if (maxPage != 0)
			help.appendSibling(getCommandPage(mainCommand.getCommandName() + " " + getName(), page + 1, maxPage + 1));
		ChatUtils.send(help);

	}
}
