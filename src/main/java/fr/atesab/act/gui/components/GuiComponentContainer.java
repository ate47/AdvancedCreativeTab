package fr.atesab.act.gui.components;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.gui.components.grid.GridPolicies;
import fr.atesab.act.gui.components.grid.GridPolicy;

public abstract class GuiComponentContainer {
    private List<GuiComponentLocation> components = new ArrayList<>();
    private boolean inInit = false;
    private int globalWidth, globalHeight;
    private GridPolicy gridPolicy;

    public GuiComponentContainer() {
        gridPolicy = GridPolicies.absoluteGrid();
    }

    /**
     * set the component location policy
     * 
     * @param gridPolicy the policy
     */
    public void setGridPolicy(GridPolicy gridPolicy) {
        this.gridPolicy = gridPolicy;
        recomputeSizes();
    }

    /**
     * recompute the locations of the components
     */
    public void recomputeSizes() {
        if (inInit)
            return;
        gridPolicy.computeLocations(this, components);
        globalWidth = components.stream().mapToInt(l -> l.x + l.getComponent().getWidth()).max().orElse(0);
        globalHeight = components.stream().mapToInt(l -> l.y + l.getComponent().getHeight()).max().orElse(0);
    }

    public int getGlobalWidth() {
        return globalWidth;
    }

    public int getGlobalHeight() {
        return globalHeight;
    }

    /**
     * add components to the container, this method is better than calling
     * {@link #addComponent(GuiComponent)} multiple time.
     * 
     * @param components the components to add
     */
    public void addComponents(GuiComponent... components) {
        for (var component : components) {
            this.components.add(new GuiComponentLocation(component));
            component.setContainer(this);
        }
        recomputeSizes();
    }

    /**
     * add a component and return it, use {@link #addComponents(GuiComponent...)} to
     * add multiple components.
     * 
     * @param <T>       the component type
     * @param component the component
     * @return the component
     */
    public <T extends GuiComponent> T addComponent(T component) {
        components.add(new GuiComponentLocation(component));
        component.setContainer(this);
        recomputeSizes();
        return component;
    }

    public void init() {
        inInit = true;
        components.stream().map(GuiComponentLocation::getComponent).forEach(GuiComponent::init);
        inInit = false;
        recomputeSizes();
    }

}
