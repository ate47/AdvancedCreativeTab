package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModdedCommandOpenMenu extends ModdedCommand {
    private final Function<String[], Screen> menu;
    private final boolean allowArguments;

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
    protected Command<CommandSourceStack> onNoArgument() {
        return allowArguments ? c -> {
            GuiUtils.displayScreen(menu.apply(new String[0]));
            return 0;
        } : c -> {
            GuiUtils.displayScreen(menu.apply(null));
            return 0;
        };
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
            LiteralArgumentBuilder<CommandSourceStack> command, CommandBuildContext context) {
        return allowArguments
                ? command.then(Commands.argument("menuoptions", StringArgumentType.greedyString()).executes(c -> {
            GuiUtils.displayScreen(menu.apply(StringArgumentType.getString(c, "menuoptions").split(" ")));
            return 0;
        }))
                : command;
    }

}
