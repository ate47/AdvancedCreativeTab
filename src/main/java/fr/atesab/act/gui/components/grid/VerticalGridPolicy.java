package fr.atesab.act.gui.components.grid;

import fr.atesab.act.gui.components.GuiComponent;
import fr.atesab.act.gui.components.GuiComponentContainer;
import fr.atesab.act.gui.components.GuiComponentLocation;

import java.util.List;

public class VerticalGridPolicy implements GridPolicy {
    private final GridAlignmentX.AlignFunction alignFunction;

    public VerticalGridPolicy(GridAlignmentX alignment) {
        this.alignFunction = alignment.alignFunction;
    }

    @Override
    public void computeLocations(GuiComponentContainer container, List<GuiComponentLocation> components) {
        var globalWidth = components.stream().map(GuiComponentLocation::getComponent).mapToInt(GuiComponent::getWidth)
                .max().orElse(0);
        var y = 0;
        for (var l : components) {
            var c = l.getComponent();
            l.x = alignFunction.apply(globalWidth, c.getWidth());
            l.y = y;
            y += c.getHeight();
        }
    }
}
