package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.utils.ChatUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class SCHelp extends SubCommand {

	public static IChatComponent getCommandPage(String commandName, int page, int maxPage) {
		IChatComponent c = new ChatComponentText("\n");
		if (page != 1)
			c.appendSibling(new ChatComponentText("<--").setChatStyle(new ChatStyle()
					.setChatClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName + " " + (int) (page - 1)))
					.setColor(EnumChatFormatting.RED)));
		else
			c.appendSibling(
					new ChatComponentText("<--").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GRAY)));
		c.appendSibling(new ChatComponentText(" | ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
		if (page != maxPage)
			c.appendSibling(new ChatComponentText("-->").setChatStyle(new ChatStyle()
					.setChatClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName + " " + (int) (page + 1)))
					.setColor(EnumChatFormatting.RED)));
		else
			c.appendSibling(
					new ChatComponentText("-->").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GRAY)));
		return c;
	}

	public static IChatComponent getHelp(String commandName, SubCommand cmd, ICommandSender sender) {
		IChatComponent c = new ChatComponentText("\n");
		ChatStyle cs = new ChatStyle().setColor(EnumChatFormatting.GOLD);
		switch (cmd.getClickOption()) {
		case suggestCommand:
			cs.setChatClickEvent(
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + commandName + " " + cmd.getName() + " "))
					.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ChatComponentText(I18n.format("cmd.act.help.click"))
									.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW))));
			break;
		default:
			cs.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandName + " " + cmd.getName()))
					.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ChatComponentText(I18n.format("cmd.act.help.do"))
									.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW))));
			break;
		}
		c.appendSibling(
				new ChatComponentText("/" + commandName + " " + cmd.getSubCommandUsage(sender)).setChatStyle(cs));
		c.appendSibling(new ChatComponentText(" : ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
		c.appendSibling(new ChatComponentText(cmd.getDescription())
				.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE)));
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
		IChatComponent help = new ChatComponentText(
				"-- " + I18n.format("cmd.act.help", title) + " (" + (page + 1) + " / " + (maxPage + 1) + ") --")
						.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));

		for (int i = page * elm; i < (page + 1) * elm && i < mainCommand.subCommands.size(); i++) {
			help.appendSibling(getHelp(mainCommand.getCommandName(), mainCommand.subCommands.get(i), sender));
		}
		if (maxPage != 0)
			help.appendSibling(getCommandPage(mainCommand.getCommandName() + " " + getName(), page + 1, maxPage + 1));
		ChatUtils.send(help);

	}
}
