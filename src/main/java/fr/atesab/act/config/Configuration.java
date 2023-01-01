package fr.atesab.act.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import fr.atesab.act.ACTMod;
import fr.atesab.act.utils.SyncList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Configuration {
    private CommentedFileConfig config;
    private Consumer<List<String>> customItemsCallback = (s) -> {
    };

    private final SyncList<String> customItems = new SyncList<>(new ArrayList<>()) {
        @Override
        protected void onUpdate(List<String> data) {
            customItemsCallback.accept(data);
        }
    };
    private boolean disableToolTip = false;

    public boolean doesDisableToolTip() {
        return disableToolTip;
    }

    public SyncList<String> getCustomitems() {
        return customItems;
    }

    public void addCustomItemsCallback(Consumer<List<String>> consumer) {
        this.customItemsCallback = customItemsCallback.andThen(consumer);
    }

    public void save() {
        config.set("general.items", customItems);
        config.set("general.disableToolTip", disableToolTip);
        config.save();
    }

    public void setDoesDisableToolTip(boolean doesDisableToolTip) {
        this.disableToolTip = doesDisableToolTip;
    }

    public void sync(File path) {
        sync(path.toPath());
    }

    public void sync(Path path) {
        // load config
        config = CommentedFileConfig.builder(path).sync().writingMode(WritingMode.REPLACE).build();
        // load file
        config.load();
        // bypassing the non mutable (or please help me because I can't find how) ForgeSpec
        config.setComment("general.items", "the custom items");
        customItems.applyUpdate(lst -> lst.addAll(config.getOrElse("general.items", List.of(ACTMod.DEFAULT_CUSTOM_ITEMS))));

        config.setComment("general.disableToolTip", "Disable the tool tip without F3+H");
        disableToolTip = config.getOrElse("general.disableToolTip", disableToolTip);

        config.save();
    }
}
