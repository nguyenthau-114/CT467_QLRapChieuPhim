package dulieu;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TheLoaiItem {
    private final String name;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public TheLoaiItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean v) {
        selected.set(v);
    }

    @Override
    public String toString() {
        return name;
    }
}
