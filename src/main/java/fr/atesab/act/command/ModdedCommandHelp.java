package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

import fr.atesab.act.command.arguments.StringListArgumentType;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ModdedCommandHelp extends ModdedCommand {
	public static enum CommandClickOption {
		doCommand, suggestCommand;
	}

	private final TextFormatting titleColor;
	private final TextFormatting commandColor;
	private final TextFormatting textColor;
	private final String title;
	private final ModdedCommand mainCommand;

	public ModdedCommandHelp(ModdedCommand mainCommand, String title, TextFormatting titleColor,
			TextFormatting commandColor, TextFormatting textColor) {
		super("help", "cmd.act.help.cmd", CommandClickOption.suggestCommand);
		this.title = title;
		this.mainCommand = mainCommand;
		this.titleColor = titleColor;
		this.commandColor = commandColor;
		this.textColor = textColor;
		addAlias("?");
	}

	public List<ModdedCommand> getVisibleSubCommand(ModdedCommand mainCommand) {
		List<ModdedCommand> sc = new ArrayList<ModdedCommand>();
		for (ModdedCommand c : mainCommand.getSubCommands())
			if (c.isDisplayInHelp())
				sc.add(c);
		return sc;
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		// show command help
		return command
				.then(Commands
						.argument("commandforhelp", StringListArgumentType
								.stringList(mainCommand.getSubCommandsMap().keySet(), Arrays.asList(getName()), true))
						.executes(c -> {
							int count = 0;
							for (String commandName : StringListArgumentType.getStringList(c, "commandforhelp")) {
								ModdedCommand cmd = mainCommand.getSubCommandsMap().get(commandName);
								CommandSource src = c.getSource();
								String parentName = mainCommand.getGlobalName();
								if (cmd != null) {
									count += 3;
									src.sendSuccess(createText("-- " + I18n.get("cmd.act.help", cmd.getName()) + " --",
											titleColor), false);
									src.sendSuccess(createTranslatedText(cmd.getDescriptionTranslationKey(), textColor),
											false);
									src.sendSuccess(
											createTranslatedText("cmd.act.aliases",
													titleColor)
															.append(createText(": ",
																	TextFormatting.DARK_GRAY))
															.append(createText(
																	cmd.getAliases().stream()
																			.filter(s -> !s.equals(cmd.getName()))
																			.collect(Collectors.joining(", ")),
																	textColor)), // appendSibling
											false);
									Map<CommandNode<CommandSource>, String> usages = getDispatcher()
											.getSmartUsage(cmd.getNode(), src);
									if (usages.isEmpty()) { // no argument
										showCommand(cmd, src, "/" + parentName + " " + cmd.getName(), "", false);
										count++;
									} else // arguments
										for (String usage : usages.values()) {
											showCommand(cmd, src, "/" + parentName + " " + cmd.getName(), usage, false);
											count++;
										}

								} else {
									src.sendFailure(createText(
											I18n.get("cmd.act.mc.invalid", "/" + parentName + " " + getName()),
											TextFormatting.RED));
									return count + 1;
								}
							}
							return count;
						}));
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		// show command list
		return c -> {
			CommandSource src = c.getSource();
			int count = 1;
			src.sendSuccess(createText("-- " + I18n.get("cmd.act.help", title) + " --", titleColor), false);
			Map<CommandNode<CommandSource>, String> usages;
			String parentName = mainCommand.getGlobalName();
			for (ModdedCommand command : mainCommand.getSubCommands()) {
				if (command.isDisplayInHelp()) {
					usages = getDispatcher().getSmartUsage(command.getNode(), src);
					if (usages.isEmpty()) { // no argument
						showCommand(command, src, "/" + parentName + " " + command.getName(), "", true);
						count++;
					} else // arguments
						for (String usage : usages.values()) {
							showCommand(command, src, "/" + parentName + " " + command.getName(), usage, true);
							count++;
						}
				}
			}
			return count;
		};
	}

	private void showCommand(ModdedCommand command, CommandSource src, String name, String usage,
			boolean showDescription) {
		IFormattableTextComponent component;

		if (usage.isEmpty())
			component = createText(name, commandColor);
		else
			component = createText(name + " " + usage, commandColor);

		if (command.getClickOption() == CommandClickOption.doCommand)
			component = component.withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, // SHOW_TEXT
					createTranslatedText("cmd.act.help.do", titleColor)))
					.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, name)));
		else
			component = component.withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, // SHOW_TEXT
					createTranslatedText("cmd.act.help.click", titleColor)))
					.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, name + " ")));
		if (showDescription)
			src.sendSuccess(component.append(createText(": ", TextFormatting.DARK_GRAY))
					.append(createTranslatedText(command.getDescriptionTranslationKey(), textColor)), false);
		else
			src.sendSuccess(component, false);
	}

}
