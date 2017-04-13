
package JavaObjects;

import java.util.List;
import javax.inject.Named;

@Named(value = "collection2")


public class Collection {
    private String collectionName;
    private List<Album> records;
    private int numberOfRecords;
    private int collectionID;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<Album> getRecords() {
        return records;
    }

    public void setRecords(List<Album> records) {
        this.records = records;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }
    
    
}
