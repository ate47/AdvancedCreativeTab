package fr.atesab.act.gui.act;

public class ACTDevInfo {
    private final String title;
    private final String[] elements;

    public ACTDevInfo(String title, String... elements) {
        this.title = title;
        this.elements = elements;
    }

    public String[] getElements() {
        return elements;
    }

    public String getTitle() {
        return title;
    }
}
