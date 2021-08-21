package fr.atesab.act.gui.components.grid;

import fr.atesab.act.gui.components.GuiComponent;
import fr.atesab.act.gui.components.grid.FlexGridPolicy.FlexGridType;

public class GridPolicies {

    private static final GridPolicy ABSOLUTE = (cc, components) -> components.forEach(l -> {
        var c = l.getComponent();
        l.x = c.getX();
        l.y = c.getY();
    });

    /**
     * 
     * @return a grid using the {@link GuiComponent#getX()} and
     *         {@link GuiComponent#getY()} functions to get the location
     */
    public static GridPolicy absoluteGrid() {
        return ABSOLUTE;
    }

    /**
     * @return a grid puting all the components verticaly
     */
    public static GridPolicy verticalGrid(GridAlignmentX alignment) {
        return new VerticalGridPolicy(alignment);
    }

    /**
     * @return a grid puting all the components horizontaly
     */
    public static GridPolicy horizontalGrid(GridAlignmentY alignment) {
        return new HorizontalGridPolicy(alignment);
    }

    /**
     * 
     * @param maxElementY
     * @return
     */
    public static GridPolicy horizontalFlexGrid(int maxElementY) {
        return new FlexGridPolicy(maxElementY, FlexGridType.HORIZONTAL, GridAlignmentX.CENTER, GridAlignmentY.MIDDLE);
    }

    /**
     * 
     * @param maxElementX
     * @param alignmentX
     * @param alignmentY
     * @return
     */
    public static GridPolicy verticalFlexGrid(int maxElementX, GridAlignmentX alignmentX, GridAlignmentY alignmentY) {
        return new FlexGridPolicy(maxElementX, FlexGridType.VERTICAL, alignmentX, alignmentY);
    }

    /**
     * @param maxElementX
     * @return
     */
    public static GridPolicy verticalFlexGrid(int maxElementX) {
        return new FlexGridPolicy(maxElementX, FlexGridType.VERTICAL, GridAlignmentX.CENTER, GridAlignmentY.MIDDLE);
    }

    /**
     * 
     * @param maxElementY
     * @param alignmentX
     * @param alignmentY
     * @return
     */
    public static GridPolicy horizontalFlexGrid(int maxElementY, GridAlignmentX alignmentX, GridAlignmentY alignmentY) {
        return new FlexGridPolicy(maxElementY, FlexGridType.HORIZONTAL, alignmentX, alignmentY);
    }
}
