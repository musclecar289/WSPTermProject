package JavaObjects;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Nicholas Clemmons
 */
@Named(value = "editCollectionBean")
@SessionScoped
public class EditCollectionBean implements Serializable {

//resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private List<Record> albums;
    private int numberOfAlbums;

    private long albumID;
    private String title;
    private String artist;

    private int releaseYear;
    private int numberOfTracks;
    private int numberOfDiscs;
    private String genre;
    private int albumCount;
    

    @PostConstruct
    public void init() {
        try {
            albums = loadAlbums();
        } catch (SQLException ex) {
            Logger.getLogger(ViewCollectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Record> getAlbums() {
        return albums;
    }

    public List<Record> loadAlbums() throws SQLException {

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
                    "select ALBUM_ID, TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT from ALBUMTABLE"
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
                a.setEditable(true);
                list.add(a);
            }

        } finally {
            conn.close();
        }

        return list;
    }

    public String refresh() {

        try {
            albums = loadAlbums();
        } catch (SQLException ex) {
            Logger.getLogger(EditCollectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void insertAlbum() throws SQLException {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement insertQuery = conn.prepareStatement(
                    "Insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT) "
                    + "VALUES (?,?,?,?,?,?,?)");

            insertQuery.setString(1, getTitle());
            insertQuery.setString(2, getArtist());
            insertQuery.setInt(3, getReleaseYear());
            insertQuery.setInt(4, getNumberOfTracks());
            insertQuery.setInt(5, getNumberOfDiscs());
            insertQuery.setString(6, getGenre());
            insertQuery.setInt(7, getAlbumCount());

            int result = insertQuery.executeUpdate();
            if (result == 1) {
                FacesMessage insertionErrorMessage01 = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        getTitle() + " added successfully!", null);
                FacesContext.getCurrentInstance().addMessage("editAlbumForm:addBook", insertionErrorMessage01);

            } else {
                // if not 1, it must be an error.
                FacesMessage insertionErrorMessage02 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        getTitle() + " could not be added!", null);
                FacesContext.getCurrentInstance().addMessage("bookForm:addBook", insertionErrorMessage02);
            }
        } finally {
            conn.close();
        }

        albums = loadAlbums(); // reload the updated info

    }

    public String updateAlbum(Record a) throws SQLException {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        
         PreparedStatement updateQuery = conn.prepareStatement(
                    "update ALBUMTABLE set TITLE = ?, ARTIST = ?, YEAR = ?, NUMBER_OF_TRACKS = ?, NUMBER_OF_DISCS = ?, GENRE = ?, ALBUMCOUNT = ? where ALBUM_ID=?");
        try {
            
            updateQuery.setString(1, a.getTitle());
            updateQuery.setString(2, a.getArtist());
            updateQuery.setInt(3, a.getReleaseYear());
            updateQuery.setInt(4, a.getNumberOfTracks());
            updateQuery.setInt(5, a.getNumberOfDiscs());
            updateQuery.setString(6, a.getGenre());
            updateQuery.setInt(7, a.getAlbumCount());
            updateQuery.setInt(8, a.getAlbumID());

            int result = updateQuery.executeUpdate();
            if (result == 1) {
                FacesMessage insertionErrorMessage03 = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        title + " updated successfully!", null);
                FacesContext.getCurrentInstance().addMessage("databaseTable:save", insertionErrorMessage03);

            } else {
                // if not 1, it must be an error.
                FacesMessage insertionErrorMessage04 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        title + " could not be updated!", null);
                FacesContext.getCurrentInstance().addMessage("databaseTable:save", insertionErrorMessage04);
            }
        } finally {
            conn.close();
        }

        albums = loadAlbums(); // reload the updated info
        return null; // re-display index.xhtml
    }

    public String deleteAlbum(Integer id, String title) throws SQLException {


        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement deleteQuery = conn.prepareStatement(
                    "delete from ALBUMTABLE where ALBUM_ID=?");
            deleteQuery.setInt(1, id);

            int result = deleteQuery.executeUpdate();
            if (result == 1) {
                FacesMessage insertionErrorMessage05 = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        title + " deleted successfully!", null);
                FacesContext.getCurrentInstance().addMessage("databaseTable:delete", insertionErrorMessage05);

            } else {
                // if not 1, it must be an error.
                FacesMessage insertionErrorMessage06 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        title + "could not be deleted!", null);
                FacesContext.getCurrentInstance().addMessage("databaseTable:delete", insertionErrorMessage06);
            }
        } finally {
            conn.close();
        }

        refresh(); // reload the updated info
        return null; // re-display index.xhtml
    }

    public void makeEditable(Record album) {

        this.albumID = album.getAlbumID();
        this.title = album.getTitle();
        this.artist = album.getArtist();
        this.releaseYear = album.getReleaseYear();
        this.numberOfTracks = album.getNumberOfTracks();
        this.numberOfDiscs = album.getNumberOfDiscs();
        this.albumCount = album.getAlbumCount();
        this.genre = album.getGenre();
        album.toggleEditable();
    }

//    public void onRowEdit(RowEditEvent event) throws SQLException {
//        Album albumToEdit = (Album) event.getObject();
//        this.updateAlbum(albumToEdit);
//        
//        FacesMessage msg = new FacesMessage("Album Edited", ((Album) event.getObject()).getTitle());
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//    }
//     
//    public void onRowCancel(RowEditEvent event) {
//        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Album) event.getObject()).getTitle());
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//    }
//    
//    public void onCellEdit(CellEditEvent event) {
//        Object oldValue = event.getOldValue();
//        Object newValue = event.getNewValue();
//         
//        if(newValue != null && !newValue.equals(oldValue)) {
//            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        }
//    }
    
    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public void setNumberOfAlbums(int numberOfAlbums) {
        this.numberOfAlbums = numberOfAlbums;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getNumberOfTracks() {
        return numberOfTracks;
    }

    public void setNumberOfTracks(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
    }

    public int getNumberOfDiscs() {
        return numberOfDiscs;
    }

    public void setNumberOfDiscs(int numberOfDiscs) {
        this.numberOfDiscs = numberOfDiscs;
    }

    public long getAlbumID() {
        return albumID;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(int albumCount) {
        this.albumCount = albumCount;
    }

}
