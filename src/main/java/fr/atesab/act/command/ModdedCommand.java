package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class ModdedCommand {

	public static final int MAX_EXAMPLE = 6;

	public static ITextComponent createPrefix(String text, TextFormatting color, TextFormatting border) {
		return createText("[", border).appendSibling(createText(text, color)).appendSibling(createText("] ", border));
	}

	public static ITextComponent createText(String text, TextFormatting color) {
		return new StringTextComponent(Objects.requireNonNull(text)).applyTextStyle(color);
	}

	public static ITextComponent createTranslatedPrefix(String text, TextFormatting color, TextFormatting border,
			Object... args) {
		return createText("[", border).appendSibling(createTranslatedText(text, color, args))
				.appendSibling(createText("] ", border));
	}

	public static ITextComponent createTranslatedText(String lang, TextFormatting color, Object... args) {
		return new TranslationTextComponent(Objects.requireNonNull(lang), args).applyTextStyle(color);
	}

	private final Map<String, ModdedCommand> NAME_TO_COMMAND = new TreeMap<>();
	private final List<ModdedCommand> SUB_COMMANDS = new ArrayList<>();
	private List<String> subCommandTitleExample = new ArrayList<>(MAX_EXAMPLE);
	private ModdedCommand parent;
	public ModdedCommand defaultCommand;
	private String name;
	private String description;
	private CommandClickOption clickOption;

	private boolean displayInHelp;
	private List<String> aliases;
	protected LiteralCommandNode<CommandSource> node;
	private CommandDispatcher<CommandSource> dispatcher;
	private boolean registered = false;

	public ModdedCommand(String name) {
		this(name, "", CommandClickOption.doCommand);
	}

	public ModdedCommand(String name, String description, CommandClickOption clickOption) {
		this(name, description, clickOption, true);
	}

	public ModdedCommand(String name, String description, CommandClickOption clickOption, boolean displayInHelp) {
		this.description = description;
		this.clickOption = clickOption;
		this.displayInHelp = displayInHelp;
		this.name = name.toLowerCase();
		aliases = new ArrayList<>(1);
		addAlias(getName());
	}

	/**
	 * add an alias to this command
	 * 
	 * @param alias
	 *            the alias
	 * @return the current command
	 */
	public ModdedCommand addAlias(String alias) {
		this.aliases.add(alias.toLowerCase());
		return this;
	}

	/**
	 * @return a unmodifiable list of the aliases
	 */
	public List<String> getAliases() {
		return Collections.unmodifiableList(aliases);
	}

	/**
	 * @return the option in a {@link ModdedCommandHelp} command
	 */
	public CommandClickOption getClickOption() {
		return clickOption;
	}

	/**
	 * @return the translated description of this command
	 */
	public String getDescription() {
		return I18n.format(getDescriptionTranslationKey());
	}

	/**
	 * @return the translation key of the description
	 */
	public String getDescriptionTranslationKey() {
		return description;
	}

	/**
	 * @return the command on which this {@link ModdedCommand} has been registered
	 */
	public CommandDispatcher<CommandSource> getDispatcher() {
		return dispatcher;
	}

	/**
	 * @return the name with the parent(s) name(s)
	 */
	public String getGlobalName() {
		return parent != null ? parent.getGlobalName() + " " + getName() : getName();
	}

	/**
	 * @return the command name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the node
	 */
	public LiteralCommandNode<CommandSource> getNode() {
		return node;
	}

	/**
	 * @return the parent (if registered) of this command
	 */
	public ModdedCommand getParent() {
		return parent;
	}

	/**
	 * @return an unmodifiable {@link Collection} of the sub commands
	 */
	public List<ModdedCommand> getSubCommands() {
		return Collections.unmodifiableList(SUB_COMMANDS);
	}

	/**
	 * @return an unmodifiable {@link Map} of the sub commands
	 */
	public Map<String, ModdedCommand> getSubCommandsMap() {
		return Collections.unmodifiableMap(NAME_TO_COMMAND);
	}

	/**
	 * @return if this command is display in a {@link ModdedCommandHelp} call
	 */
	public boolean isDisplayInHelp() {
		return displayInHelp;
	}

	/**
	 * Called for registering argument command, should not be used for no argument
	 * command, see {@link #onNoArgument()} for that
	 * 
	 * @param command
	 *            the argument builder
	 * @return the same command argument builder modified
	 */
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		return command;
	}

	/**
	 * Called for registering non argument command, should not be used for argument
	 * command, see {@link #onArgument(LiteralArgumentBuilder)} for that
	 * 
	 * @return the command to execute
	 */
	protected Command<CommandSource> onNoArgument() {
		return defaultCommand != null ? defaultCommand.onNoArgument() : c -> {
			c.getSource().sendErrorMessage(new TranslationTextComponent("cmd.act.error.noargument")
					.applyTextStyles(new TextFormatting[] { TextFormatting.RED, TextFormatting.ITALIC }));
			return 1;
		};
	}

	private void register() {
		if (registered)
			throw new IllegalStateException("A command can't be registered more than once");
		registered = true;
	}

	/**
	 * register the command to a {@link CommandDispatcher}
	 * 
	 * @param dispatcher
	 *            the dispatcher
	 */
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		register();
		this.dispatcher = dispatcher;
		register(dispatcher.getRoot());
	}

	private void register(CommandNode<CommandSource> node) {
		LiteralArgumentBuilder<CommandSource> bld;
		Command<CommandSource> noArgument;
		for (String alias : aliases) {
			noArgument = onNoArgument();
			bld = onArgument(Commands.literal(alias));
			if (noArgument != null)
				bld.executes(noArgument);
			this.node = bld.build();
			NAME_TO_COMMAND.forEach((salias, cmd) -> {
				cmd.dispatcher = dispatcher;
				cmd.register(this.node);
			});
			node.addChild(this.node);
		}
	}

	/**
	 * register a {@link SubCommand} and made it default
	 * 
	 * @param subCommand
	 *            the {@link SubCommand}
	 * @return the parent command
	 */
	public ModdedCommand registerDefaultSubCommand(ModdedCommand subCommand) {
		subCommand.register();
		for (String alias : subCommand.getAliases()) {
			NAME_TO_COMMAND.put(alias, subCommand);
		}

		if (subCommandTitleExample.size() < MAX_EXAMPLE)
			subCommandTitleExample.add(0, subCommand.getName());
		else {
			subCommandTitleExample.remove(MAX_EXAMPLE - 1);
			subCommandTitleExample.add(0, subCommand.getName());
		}
		SUB_COMMANDS.add(subCommand);
		defaultCommand = subCommand;
		return this;
	}

	/**
	 * register a {@link SubCommand}
	 * 
	 * @param subCommand
	 *            the {@link SubCommand}
	 * @return the parent command
	 */
	public ModdedCommand registerSubCommand(ModdedCommand subCommand) {
		subCommand.register();
		subCommand.parent = this;
		for (String alias : subCommand.getAliases()) {
			NAME_TO_COMMAND.put(alias, subCommand);
		}
		if (subCommandTitleExample.size() < MAX_EXAMPLE)
			subCommandTitleExample.add(subCommand.getName());
		SUB_COMMANDS.add(subCommand);
		return this;
	}
}
