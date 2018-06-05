package RestarantApp.model;

public class TableTreeItem {

    private String layerName;
    private boolean selected;

    public TableTreeItem() {

    }

    public TableTreeItem(String layerName, boolean selected) {
        this.layerName = layerName;
        this.selected = selected;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return this.layerName;
    }
}
