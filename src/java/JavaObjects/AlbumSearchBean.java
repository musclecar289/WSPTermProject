package JavaObjects;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AlbumSearchRequest;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.SimpleAlbum;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

/**
 *
 * @author Nicholas Clemmons
 */
@Named(value = "albumSearchBean")
@SessionScoped
public class AlbumSearchBean implements Serializable {
//String titleToFind
// Create an API instance. The default instance connects to https://api.spotify.com/.

    Api api = Api.DEFAULT_API;
    String spotifyEndPoint = "https://api.spotify.com/v1/search";
    List<SimpleAlbum> albums;
    private String title;

    @PostConstruct
    public void init() {
        AlbumSearchRequest request = api.searchAlbums("led zeppelin").offset(0).limit(5).build();
        try {
            Page<SimpleAlbum> albumSearchResult = request.get();
            albums = albumSearchResult.getItems();
        } catch (IOException ex) {
            Logger.getLogger(AlbumSearchBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebApiException ex) {
            Logger.getLogger(AlbumSearchBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<SimpleAlbum> getAlbums() {
        return albums;
    }

    public void setAlbums(List<SimpleAlbum> albums) {
        this.albums = albums;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void searchForAlbum() {

    }

}
