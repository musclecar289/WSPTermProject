
package JavaObjects;

import JavaObjects.Collection;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;

/**
 *
 * @author Nicholas Clemmons
 */

@Named(value = "collectionsBean")
@SessionScoped
public class CollectionsBean implements Serializable {

private List<Collection> collections;
private int numberOfCollections;
    
    @PostConstruct
    public void init() {

    }
}
