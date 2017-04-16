package JavaObjects;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.methods.AlbumSearchRequest;
import com.wrapper.spotify.models.Album;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.SimpleAlbum;
import com.wrapper.spotify.models.SimpleTrack;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Nicholas Clemmons
 */
@Named(value = "albumSearchBean")
@SessionScoped
public class AlbumSearchBean implements Serializable {

    //resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    // Create an API instance. The default instance connects to https://api.spotify.com/.
    Api api = Api.DEFAULT_API;
    String spotifyEndPoint = "https://api.spotify.com/v1/search";
    List<SimpleAlbum> albums;
    Album test;
    Album selectedAlbum;
    List<SimpleTrack> selectedTracks;
    private String title;

    @PostConstruct
    public void init() {
//        AlbumSearchRequest request = api.searchAlbums("led zeppelin").offset(0).limit(10).build();
//        try {
//            Page<SimpleAlbum> albumSearchResult = request.get();
//            albums = albumSearchResult.getItems();
//            System.out.println("test");
//        } catch (IOException ex) {
//            Logger.getLogger(AlbumSearchBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (WebApiException ex) {
//            Logger.getLogger(AlbumSearchBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void albumSearch() {
        AlbumSearchRequest request = api.searchAlbums(getTitle()).offset(0).limit(25).build();
        try {
            Page<SimpleAlbum> albumSearchResult = request.get();
            albums = albumSearchResult.getItems();

        } catch (IOException | WebApiException ex) {
            Logger.getLogger(AlbumSearchBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//Use for getting tracks. //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Album object has reference to list of tracks//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void albumSearchById(String idToFind) {
        //String albumId = "6fRqzJT070Kp9RWlSXmKcY";
        AlbumRequest request = api.getAlbum(idToFind).build();
        try {
            test = request.get();
            Page<SimpleTrack> trackList = test.getTracks();
            selectedTracks = trackList.getItems();
        } catch (IOException | WebApiException ex) {
            Logger.getLogger(AlbumSearchBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void onRowSelect(SelectEvent event) {
        SimpleAlbum record = (SimpleAlbum) event.getObject();
        albumSearchById(record.getId());
        FacesMessage msg = new FacesMessage("Album Selected", record.getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void addAlbumToCollection(Collection c) throws IOException, SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        PreparedStatement insertQuery = conn.prepareStatement(
                "INSERT INTO ALBUMTABLE(ALBUM_ID, TITLE, ARTIST, RELEASE_DATE, GENRE) "
                + "VALUES(?,?,?,?,?)"
        );

        //insertQuery.setInt(5, numberOfTracks);
        //insertQuery.setInt(6, numberOfDiscs);
        //insertQuery.setInt(8, albumCount);

        PreparedStatement insertQuery2 = conn.prepareStatement(
                "INSERT INTO COLLECTION_ITEMS (ALBUMID, COLLECTION_NAME, OWNER) "
                + "VALUES(?,?,?)"
        );

        try {
            insertQuery.setString(1, test.getId());
            insertQuery.setString(2, test.getName());
            insertQuery.setString(3, test.getArtists().get(0).getName());
            insertQuery.setString(4, test.getReleaseDate());
            insertQuery.setString(5, test.getGenres().get(0));
            
            insertQuery2.setString(1, test.getId());
            insertQuery2.setString(2, c.getCollectionName());
            insertQuery2.setString(3, c.getOwnerName());
            
            insertQuery.execute();                      
            insertQuery2.execute();
        } finally {
            conn.close();
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

    public Album getTest() {
        return test;
    }

    public void setTest(Album test) {
        this.test = test;
    }

    public Album getSelectedAlbum() {
        return selectedAlbum;
    }

    public void setSelectedAlbum(Album selectedAlbum) {
        this.selectedAlbum = selectedAlbum;
    }

    public List<SimpleTrack> getSelectedTracks() {
        return selectedTracks;
    }

    public void setSelectedTracks(List<SimpleTrack> selectedTracks) {
        this.selectedTracks = selectedTracks;
    }

}
