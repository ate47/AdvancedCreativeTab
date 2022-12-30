package fr.atesab.act.gui.components.grid;

import fr.atesab.act.gui.components.GuiComponentContainer;
import fr.atesab.act.gui.components.GuiComponentLocation;

import java.util.List;

@FunctionalInterface
public interface GridPolicy {
    /**
     * compute the x,y location of the {@link GuiComponentLocation} list
     *
     * @param container the component container
     * @param locations the component locations
     */
    void computeLocations(GuiComponentContainer container, List<GuiComponentLocation> locations);
}
