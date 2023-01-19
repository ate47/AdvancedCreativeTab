package fr.atesab.act.command.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.CommandUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public abstract class SCMainCommand extends SubCommand {
	public static class SubInterface extends MainCommand {
		public String name;

		public SubInterface(List<SubCommand> subCommands, String defaultCommand, String name) {
			super(subCommands, defaultCommand);
			this.name = name;
		}

		@Override
		public boolean canCommandSenderUseCommand(ICommandSender sender) {
			return false;
		}

		@Override
		public List<String> getCommandAliases() {
			return new ArrayList<String>();
		}

		@Override
		public String getCommandName() {
			return name;
		}

		@Override
		public String getCommandUsage(ICommandSender sender) {
			return name;
		}

	}

	public ArrayList<SubCommand> subCommands = new ArrayList<SubCommand>();

	public String defaultCommand;

	public SCMainCommand(ArrayList<SubCommand> subCommands, String defaultCommand, String name, String description,
			CommandClickOption clickOption) {
		super(name, description, clickOption);
		this.subCommands = subCommands;
		this.defaultCommand = defaultCommand;

	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		List<String> ls = new ArrayList<String>();
		subCommands.sort(new Comparator<SubCommand>() {
			public int compare(SubCommand o1, SubCommand o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
		if (args.length == 0)
			return new ArrayList<String>();
		for (int i = 0; i < subCommands.size(); i++) {
			List<String> alias = subCommands.get(i).getAlias();
			if (!alias.contains(subCommands.get(i).getName()))
				alias.add(subCommands.get(i).getName());
			for (int j = 0; j < alias.size(); j++) {
				if (args[0].equalsIgnoreCase(alias.get(j))) {
					String[] SCargs = new String[args.length - 1];
					System.arraycopy(args, 1, SCargs, 0, SCargs.length);
					return subCommands.get(i).addTabCompletionOptions(sender, SCargs, pos);
				}
			}
		}
		for (int i = 0; i < subCommands.size(); i++) {
			List<String> alias = subCommands.get(i).getAlias();
			for (int j = 0; j < alias.size(); j++) {
				ls.add(alias.get(j));
			}
		}
		if (args.length == 1)
			return CommandUtils.getTabCompletion(ls, args);
		return new ArrayList<String>();
	}

	@Override
	public abstract List<String> getAlias();

	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		Minecraft mc = Minecraft.getMinecraft();
		if (args.length == 0)
			args = new String[] { defaultCommand };
		for (int i = 0; i < subCommands.size(); i++) {
			List<String> alias = subCommands.get(i).getAlias();
			if (!alias.contains(subCommands.get(i).getName()))
				alias.add(subCommands.get(i).getName());
			for (int j = 0; j < alias.size(); j++) {
				if (args[0].equalsIgnoreCase(alias.get(j))) {
					String[] SCargs = new String[args.length - 1];
					System.arraycopy(args, 1, SCargs, 0, SCargs.length);
					subCommands.get(i).processSubCommand(sender, SCargs, new SubInterface(subCommands, defaultCommand,
							mainCommand.getCommandName() + " " + getName()));
					return;
				}
			}
		}
		ChatUtils
				.send(new ChatComponentText(
						I18n.format("cmd.act.mc.invalid", "/" + defaultCommand).replaceAll("::", " "))
								.setChatStyle(
										new ChatStyle()
												.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
														"/" + mainCommand.getCommandName() + " " + this.getName()
																+ " help"))
												.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
														new ChatComponentText(I18n.format("cmd.act.help.do"))
																.setChatStyle(
																		new ChatStyle().setColor(EnumChatFormatting.BLUE))))
												.setColor(EnumChatFormatting.RED)));
	}
}
