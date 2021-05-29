package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.command.arguments.StringListArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

public class ModdedCommandFormat extends ModdedCommand {
	public static final int ELEMENT_PER_LINE = 8;

	public ModdedCommandFormat() {
		super("format", "cmd.act.format", CommandClickOption.doCommand, true);
		addAlias("f");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		return command.then(
				Commands.argument("formatname", StringListArgumentType.enumList(TextFormatting.class)).executes(c -> {
					TextFormatting[] element = StringListArgumentType.getEnumList(TextFormatting.class, c,
							"formatname");
					for (TextFormatting f : element) {
						c.getSource().sendSuccess(
								createText(f.getName() + " (&" + f.toString().substring(1) + ")", TextFormatting.YELLOW)
										.append(createText(": ", TextFormatting.DARK_GRAY))
										.append(createText(f.getName(), f)),
								false);
					}

					return element.length;
				}));
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			IFormattableTextComponent text = new StringTextComponent("");
			int element = 0;
			int line = 0;
			for (TextFormatting format : TextFormatting.values()) {
				HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, createText(
						format.getName() + " (&" + format.toString().substring(1) + ")", TextFormatting.YELLOW));
				text = text.append(
						createText("&" + format.toString().substring(1) + " ", TextFormatting.RESET).withStyle(s -> {
							s.withHoverEvent(he); // setHoverEvent
							return s;
						})); // applyTextStyle
				text = text.append(createText("&" + format.toString().substring(1), format).withStyle(s -> {
					s.withHoverEvent(he); // setHoverEvent
					return s;
				}) // applyTextStyle
				).append(createText(" ", TextFormatting.RESET));
				if (++element == ELEMENT_PER_LINE) {
					c.getSource().sendSuccess(text, false);
					text = new StringTextComponent("");
					element = 0;
					line++;
				}
			}
			if (element != 0) {
				c.getSource().sendSuccess(text, false);
				line++;
			}
			return line;
		};
	}

}
