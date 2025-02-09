package dbbrowser;

import javafx.beans.property.SimpleStringProperty;

public class RowData {
    private SimpleStringProperty[] values;

    public RowData(int columnCount) {
        values = new SimpleStringProperty[columnCount];
        for (int i = 0; i < columnCount; i++) {
            values[i] = new SimpleStringProperty("");
        }
    }

    public void set(int index, String value) {
        values[index].set(value != null ? value : "");
    }

    public SimpleStringProperty get(int index) {
        return values[index];
    }

    public String getValue(int index) {
        return values[index].get();
    }

    public void setValue(int index, String value) {
        values[index].set(value);
    }
}
