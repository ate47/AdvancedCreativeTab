package fr.atesab.act.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import fr.atesab.act.ACTMod;

public class Configuration {
	private CommentedFileConfig config;

	private List<String> customItems = new ArrayList<>(Arrays.asList(ACTMod.DEFAULT_CUSTOM_ITEMS));

	public List<String> getCustomitems() {
		return customItems;
	}

	public void sync(File path) {
		// load config
		config = CommentedFileConfig.builder(path).sync().writingMode(WritingMode.REPLACE).build();
		// load file
		config.load();
		// bypassing the non mutable (or please help me because I can't find how) ForgeSpec
		config.setComment("general.items", "the custom items");
		customItems = config.getOrElse("general.items", customItems);
		config.save();
	}

	public void save() {
		config.set("general.items", customItems);
		config.save();
	}
}
