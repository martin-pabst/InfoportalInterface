package tools.word;

/**
 * Created by Martin on 10.04.2017.
 */
public class CellData {

    private String placeholder;
    private String newValue;

    public CellData(String placeholder, String newValue) {
        this.placeholder = placeholder;
        this.newValue = newValue;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
