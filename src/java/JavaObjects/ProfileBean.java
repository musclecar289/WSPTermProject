package JavaObjects;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.models.Album;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.SimpleAlbum;
import com.wrapper.spotify.models.SimpleTrack;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Nicholas Clemmons
 */
@Named(value = "profileBean")
@SessionScoped
public class ProfileBean implements Serializable {

    //resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    Api api = Api.DEFAULT_API;
    String spotifyEndPoint = "https://api.spotify.com/v1/search";
    Album test;
    List<SimpleTrack> selectedTracks;
    private List<Collection> collections;
    private int numberOfCollections;
    private Collection selectedCollection;
    private Record selectedRecord;
    private String username;
    private Part part;
    private String oldCollectionName;

    @PostConstruct
    public void init() {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            Principal p = fc.getExternalContext().getUserPrincipal();
            username = p.getName();
            collections = loadCollections();
            numberOfCollections = collections.size();
        } catch (SQLException ex) {
            Logger.getLogger(ViewCollectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Record> loadAlbums(String collection_name) throws SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        List<Record> list = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT a.* FROM collection_items AS c JOIN albumtable AS a WHERE a.ALBUM_ID=c.ALBUM_ID and collection_name='" + collection_name + "'"
            );

            // retrieve book data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Record a = new Record();
                a.setAlbumID(result.getInt("ALBUM_ID"));
                a.setTitle(result.getString("TITLE"));
                a.setArtist(result.getString("ARTIST"));
                a.setReleaseYear(result.getInt("YEAR"));
                a.setNumberOfTracks(result.getInt("NUMBER_OF_TRACKS"));
                a.setNumberOfDiscs(result.getInt("NUMBER_OF_DISCS"));
                a.setGenre(result.getString("GENRE"));
                a.setAlbumCount(result.getInt("ALBUMCOUNT"));
                list.add(a);
            }

        } finally {
            conn.close();
        }

        return list;
    }

    private List<Collection> loadCollections() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        List<Collection> list = new ArrayList<>();

        try {
            //Different from collectionsBean line 103
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT collection_name, COUNT(*) FROM collection_items WHERE OWNER = '" + username + "' GROUP BY collection_name "
            );

            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Collection c = new Collection();
                //Different from collectionsBean line 110-111
                //UserBean s = new UserBean();
                c.setCollectionName(result.getString("collection_name"));
                c.setNumberOfRecords(result.getInt("COUNT(*)"));
                c.setRecords(this.loadAlbums(c.getCollectionName()));
                list.add(c);
            }
        } finally {
            conn.close();
        }
        return list;
    }

    public void deleteCollect(Collection c) throws IOException, SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM collection WHERE COLLECTION_NAME='" + c.getCollectionName() + "' AND OWNER='" + username + "'"
        );
        PreparedStatement ps2 = conn.prepareStatement(
                "DELETE FROM collection_items WHERE COLLECTION_NAME='" + c.getCollectionName() + "' AND OWNER='" + username + "'"
        );

        // retrieve book data from database
        try {
            ps2.executeUpdate();
            ps.executeUpdate();
        } finally {
            conn.close();
        }

        collections = loadCollections();
    }

    public void updateCollect(Collection c) throws IOException, SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        PreparedStatement ps = conn.prepareStatement(
                "insert into COLLECTION (COLLECTION_NAME, OWNER) "
                + "values ('" + selectedCollection.getCollectionName() + "','" + username + "')"
        );

        PreparedStatement ps2 = conn.prepareStatement(
                "Update COLLECTION_ITEMS set COLLECTION_NAME='" + selectedCollection.getCollectionName() + "'"
                + " WHERE COLLECTION_NAME='" + oldCollectionName + "' AND OWNER='" + username + "'"
        );

        PreparedStatement ps3 = conn.prepareStatement(
                "DELETE FROM collection WHERE COLLECTION_NAME='" + oldCollectionName + "' AND OWNER='" + username + "'"
        );

        // retrieve book data from database
        try {

            ps.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
            //ps4.executeUpdate();
        } finally {
            conn.close();

        }
    }
    

    public void addCollect() throws IOException, SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO COLLECTION (COLLECTION_NAME, OWNER) VALUES (?, ?)"
        );

        try {
            ps.setString(1, "[Click to change name]");
            ps.setString(2, this.getUsername());
            ps.executeUpdate();
            
        } finally {
            conn.close();
        }
        collections = loadCollections();
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

    public void onCollectionSelect(SelectEvent event) {
        Collection collect = (Collection) event.getObject();
        //albumSearchById(record.getId());
        FacesMessage msg = new FacesMessage("Collection Selected", collect.getCollectionName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        edit(collect);
    }

    public void onRecordSelect(SelectEvent event) {
        Record currentRecord = (Record) event.getObject();
        albumSearchById(currentRecord.getSpotifyId());
        FacesMessage msg = new FacesMessage("Record Selected", currentRecord.getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, SQLException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Connection conn = ds.getConnection();

        InputStream inputStream;
        inputStream = null;
        try {
            inputStream = part.getInputStream();
            PreparedStatement insertQuery = conn.prepareStatement(
                    "UPDATE USERTABLE SET PICTURE=? WHERE USERNAME=? "
                    + "VALUES (?,?)");
            insertQuery.setBinaryStream(1, inputStream);
            insertQuery.setString(2, username);

            int result = insertQuery.executeUpdate();
            if (result == 1) {
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Upload successful!!", null));
            } else {
                // if not 1, it must be an error.
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                result + " file uploaded", null));
            }
        } catch (IOException e) {
            facesContext.addMessage("uploadForm:upload",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "File upload failed !!", null));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void uploadFile() throws IOException, SQLException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Connection conn = ds.getConnection();

        InputStream inputStream;
        inputStream = null;
        try {
            inputStream = part.getInputStream();
            PreparedStatement insertQuery = conn.prepareStatement(
                    "UPDATE USERTABLE SET PICTURE=? WHERE USERNAME=? "
                    + "VALUES (?,?)");
            insertQuery.setBinaryStream(1, inputStream);
            insertQuery.setString(2, username);

            int result = insertQuery.executeUpdate();
            if (result == 1) {
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Upload successful!!", null));
            } else {
                // if not 1, it must be an error.
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                result + " file uploaded", null));
            }
        } catch (IOException e) {
            facesContext.addMessage("uploadForm:upload",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "File upload failed !!", null));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public Album getTest() {
        return test;
    }

    public void setTest(Album test) {
        this.test = test;
    }

    public List<SimpleTrack> getSelectedTracks() {
        return selectedTracks;
    }

    public void setSelectedTracks(List<SimpleTrack> selectedTracks) {
        this.selectedTracks = selectedTracks;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumberOfCollections() {
        return numberOfCollections;
    }

    public void setNumberOfCollections(int numberOfCollections) {
        this.numberOfCollections = numberOfCollections;
    }

    public Collection getSelectedCollection() {
        return selectedCollection;
    }

    public void setSelectedCollection(Collection selectedCollection) {
        this.selectedCollection = selectedCollection;
    }

    public Record getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(Record selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public void edit(Collection todo){
       for (Collection existing : getCollections()){
           existing.setEditable(false);
       }
       todo.setEditable(true);
       oldCollectionName = todo.getCollectionName();
       selectedCollection= todo;
   }
    
    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }
}
