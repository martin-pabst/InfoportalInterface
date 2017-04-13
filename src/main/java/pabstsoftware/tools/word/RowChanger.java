package pabstsoftware.tools.word;

import java.util.ArrayList;

/**
 * Created by Martin on 10.04.2017.
 */
public class RowChanger {

    private ArrayList<CellData> cellDataList = new ArrayList<>();
    
    private String hint; // Der erste der Platzhalter in der Zeile

    public RowChanger(String hint) {
        this.hint = hint;
    }
    
    public void set(String placeholder, String newValue){
        
        for(CellData cellData: cellDataList){
            if(cellData.getPlaceholder().equals(placeholder)){
                cellData.setNewValue(newValue);
                return;
            }
        }
        
        cellDataList.add(new CellData(placeholder, newValue));
        
    }

    public ArrayList<CellData> getCellDataList() {
        return cellDataList;
    }

    public String getHint() {
        return hint;
    }
}
