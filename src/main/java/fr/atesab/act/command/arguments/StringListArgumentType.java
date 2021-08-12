package fr.atesab.act.command.arguments;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import fr.atesab.act.command.ModdedCommand;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;

public class StringListArgumentType implements ArgumentType<String[]> {
	public static <E extends Enum<E>> StringListArgumentType enumList(Class<E> cls) {
		return enumList(cls, e -> e.name().toLowerCase());
	}

	public static <E extends Enum<E>> StringListArgumentType enumList(Class<E> cls, Function<E, String> formatter) {
		E[] values = cls.getEnumConstants();
		String[] array = new String[values.length];
		for (int i = 0; i < array.length; i++)
			array[i] = formatter.apply(values[i]);
		String[] example = new String[3];
		System.arraycopy(array, 0, example, 0, example.length);
		return new StringListArgumentType(Arrays.asList(array), Arrays.asList(example), true);
	}

	public static <E extends Enum<E>> E[] getEnumList(Class<E> cls, final CommandContext<?> context,
			final String name) {
		E[] values = cls.getEnumConstants();
		return getEnumList(cls, context, name, s -> {
			for (E e : values)
				if (e.name().equalsIgnoreCase(s))
					return e;
			throw new CommandRuntimeException(
					ModdedCommand.createTranslatedText("cmd.act.enumlistargument.invalid", ChatFormatting.RED, s));
		});
	}

	public static <E extends Enum<E>> E[] getEnumList(Class<E> cls, final CommandContext<?> context, final String name,
			Function<String, E> unformatter) {
		String[] out = getStringList(context, name);
		@SuppressWarnings("unchecked")
		E[] array = (E[]) Array.newInstance(cls, out.length);
		for (int i = 0; i < array.length; i++)
			array[i] = unformatter.apply(out[i]);
		return array;
	}

	public static String[] getStringList(final CommandContext<?> context, final String name) {
		return context.getArgument(name, String[].class);
	}

	public static StringListArgumentType stringList(Collection<String> suggestion, Collection<String> example,
			boolean wordOnly) {
		return new StringListArgumentType(suggestion, example, wordOnly);
	}

	public static StringListArgumentType stringList(String[] suggestion, String[] example, boolean wordOnly) {
		return new StringListArgumentType(Arrays.asList(suggestion), Arrays.asList(example), wordOnly);
	}

	private Collection<String> suggestion;

	private Collection<String> example;

	private boolean wordOnly;

	public StringListArgumentType(Collection<String> suggestion, Collection<String> example, boolean wordOnly) {
		this.suggestion = suggestion;
		this.example = example;
		this.wordOnly = wordOnly;
	}

	@Override
	public Collection<String> getExamples() {
		return example;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remaining = builder.getRemaining();
		int diff;
		if (wordOnly && !quoteStarted(remaining)) { // writing a word
			diff = remaining.lastIndexOf(" ") + 1;
		} else { // a quote is started
			diff = remaining.lastIndexOf("\"") + 1;
		}
		String lastElement = remaining.substring(diff).toLowerCase(Locale.ROOT);
		String current = remaining.substring(0, diff);
		for (String sugg : suggestion)
			if (sugg.toLowerCase(Locale.ROOT).startsWith(lastElement)) {
				builder.suggest(current + sugg);
			}
		return builder.buildFuture();
	}

	@Override
	public String[] parse(StringReader reader) throws CommandSyntaxException {
		Collection<String> element = new ArrayList<>();
		if (wordOnly) {
			String end = reader.getRemaining();
			reader.setCursor(reader.getTotalLength());
			return end.split(" ");
		} else {
			while (reader.canRead()) {
				element.add(reader.readString());
				reader.skipWhitespace();
			}
			return element.stream().toArray(String[]::new);
		}
	}

	private boolean quoteStarted(String s) {
		int number = 0;
		boolean escaped = false;
		for (char c : s.toCharArray())
			if (escaped)
				continue;
			else if (c == '\\')
				escaped = true;
			else if (c == '"')
				number++;
		return number % 2 == 1;
	}

}
