package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.command.arguments.StringListArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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
						c.getSource()
								.sendFeedback(createText(f.getFriendlyName() + " (&" + f.toString().substring(1) + ")",
										TextFormatting.YELLOW).appendSibling(createText(": ", TextFormatting.DARK_GRAY))
												.appendSibling(createText(f.getFriendlyName(), f)),
										false);
					}

					return element.length;
				}));
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			ITextComponent text = new TextComponentString("");
			int element = 0;
			int line = 0;
			for (TextFormatting format : TextFormatting.values()) {
				HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						createText(format.getFriendlyName() + " (&" + format.toString().substring(1) + ")",
								TextFormatting.YELLOW));
				text = text.appendSibling(createText("&" + format.toString().substring(1) + " ", TextFormatting.RESET)
						.applyTextStyle(s -> s.setHoverEvent(he)));
				text = text
						.appendSibling(createText("&" + format.toString().substring(1), format)
								.applyTextStyle(s -> s.setHoverEvent(he)))
						.appendSibling(createText(" ", TextFormatting.RESET));
				if (++element == ELEMENT_PER_LINE) {
					c.getSource().sendFeedback(text, false);
					text = new TextComponentString("");
					element = 0;
					line++;
				}
			}
			if (element != 0) {
				c.getSource().sendFeedback(text, false);
				line++;
			}
			return line;
		};
	}

}
