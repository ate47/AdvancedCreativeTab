package fr.atesab.act.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.item.ItemStack;

/**
 * A reader to read {@link ItemStack} from giveCode
 */
public class ItemReader {
	private static class StackReference {
		ItemStack stack;
	}

	private static final String CMD = "give";
	private CommandDispatcher<StackReference> dispatcher = new CommandDispatcher<>();

	public ItemReader() {
		dispatcher.register(LiteralArgumentBuilder.<StackReference>literal(CMD)
				.then(RequiredArgumentBuilder.<StackReference, ItemInput>argument("item", ItemArgument.itemStack())
						.then(RequiredArgumentBuilder
								.<StackReference, Integer>argument("count", IntegerArgumentType.integer())
								.executes(ctx -> {
									ctx.getSource().stack = ItemArgument.getItemStack(ctx, "item")
											.createStack(IntegerArgumentType.getInteger(ctx, "count"), false);
									return 1;
								}))
						.executes(ctx -> {
							ctx.getSource().stack = ItemArgument.getItemStack(ctx, "item").createStack(1, false);
							return 1;
						})));
	}

	/**
	 * read an item from a give code
	 * 
	 * @param giveCode
	 *            the give code
	 * @return the parsed item
	 */
	public ItemStack readItem(String giveCode) {
		StackReference ref = new StackReference();
		try {
			dispatcher.execute(CMD + " " + giveCode, ref);
		} catch (CommandSyntaxException | CommandException e) {
		}
		return ref.stack;
	}
}
