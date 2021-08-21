package fr.atesab.act.command;

import com.mojang.brigadier.Command;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;

public class ModdedCommandPalette extends ModdedCommand {

    public ModdedCommandPalette() {
        super("palette", "cmd.act.palette", CommandClickOption.doCommand);
    }

    @Override
    protected Command<CommandSourceStack> onNoArgument() {
        return c -> {
            var arrays = ChatFormatting.values();
            for (var cf : arrays) {
                c.getSource()
                        .sendSuccess(new TextComponent(cf.getName().toLowerCase()).withStyle(ChatFormatting.WHITE)
                                .append(": ").withStyle(ChatFormatting.DARK_GRAY).append("&" + cf.getChar()).append(" ")
                                .append(cf.getName().toLowerCase()).withStyle(cf), false);
            }
            return arrays.length;
        };
    }
}
