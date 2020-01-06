package fr.atesab.act.command;

import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModdedCommandOpenMenu extends ModdedCommand {
	private Function<String[], Screen> menu;
	private boolean allowArguments;

	public ModdedCommandOpenMenu(String name, String description, Function<String[], Screen> menu) {
		super(name, description, CommandClickOption.doCommand);
		this.menu = menu;
		this.allowArguments = true;
	}

	public ModdedCommandOpenMenu(String name, String description, Supplier<Screen> menu) {
		super(name, description, CommandClickOption.doCommand);
		this.menu = arg -> menu.get();
		this.allowArguments = false;
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return allowArguments ? c -> {
			GuiUtils.displayScreen(menu.apply(new String[0]));
			return 0;
		} : c -> {
			GuiUtils.displayScreen(menu.apply(null));
			return 0;
		};
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		return allowArguments
				? command.then(Commands.argument("menuoptions", StringArgumentType.greedyString()).executes(c -> {
					GuiUtils.displayScreen(menu.apply(StringArgumentType.getString(c, "menuoptions").split(" ")));
					return 0;
				}))
				: command;
	}

}
