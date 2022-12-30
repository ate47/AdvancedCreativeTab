package fr.atesab.act.gui.components;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * class to avoid builder
 *
 * @author ATE47
 */
public class ACTButton extends Button {
    public ACTButton(int x, int y, int w, int h, Component message, OnPress pressAction) {
        super(x, y, w, h, message, pressAction);
    }

    public ACTButton(int x, int y, int w, int h, Component message, OnPress pressAction, OnTooltip tooltip) {
        super(x, y, w, h, message, pressAction, tooltip);
    }
}
