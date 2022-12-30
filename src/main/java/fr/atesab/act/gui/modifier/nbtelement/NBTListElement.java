package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.components.ACTButton;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTListModifier;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

public class NBTListElement extends NBTElement {
    private ListTag value;

    public NBTListElement(GuiListModifier<?> parent, String key, ListTag value) {
        super(parent, key, 200, 21);
        this.value = value;
        buttonList.add(new ACTButton(0, 0, 200, 20, Component.translatable("gui.act.modifier.tag.editor.list"), b -> mc.setScreen(new GuiNBTListModifier(Component.literal(parent.getStringTitle() + key + "/"), parent,
                tag -> NBTListElement.this.value = tag, value.copy()))));
    }

    @Override
    public NBTElement clone() {
        return new NBTListElement(parent, key, value.copy());
    }

    @Override
    public Tag get() {
        return value.copy();
    }

    @Override
    public String getType() {
        return value.getType().getName().toLowerCase() + " " + I18n.get("gui.act.modifier.tag.editor.list") + "["
                + value.size() + "]";
    }

}