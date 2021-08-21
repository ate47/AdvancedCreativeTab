package fr.atesab.act.command;

import com.mojang.brigadier.Command;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ModdedCommandPalette extends ModdedCommand {

    public ModdedCommandPalette() {
        super("palette", "cmd.act.palette", CommandClickOption.doCommand);
    }

    @Override
    protected Command<CommandSourceStack> onNoArgument() {
        return c -> {
            var compo = new TranslatableComponent("cmd.act.palette").withStyle(ChatFormatting.WHITE)
                    .append(new TextComponent(":").withStyle(ChatFormatting.DARK_GRAY));
            for (var cf : ChatFormatting.values()) {
                var t = "&" + cf.getChar();
                compo = compo.append(t).append(" ").append(new TextComponent(t).withStyle(cf)).append(" ");
            }
            c.getSource().sendSuccess(compo, false);
            return 1;
        };
    }
}
