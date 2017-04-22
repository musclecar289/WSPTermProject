
package JavaObjects;

import java.util.List;
import javax.inject.Named;

@Named(value = "collection2")


public class Collection {
    private String collectionName;
    private String ownerName;
    private List<Record> records;
    private int numberOfRecords;
    private int collectionID;
    boolean editable;
    
    
    public boolean isEditable() {
		return editable;
	}
    public void setEditable(boolean editable) {
    this.editable = editable;
	}

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }
    
    
}
