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
import javax.sql.DataSource;

/**
 *
 * @author Nicholas Clemmons
 */
@Named(value = "viewCollectionBean")
@SessionScoped
public class ViewCollectionBean implements Serializable {

    //resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private List<Record> albums;
    private int numberOfAlbums;

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
                a.setAlbumID(result.getString("ALBUM_ID"));
                a.setTitle(result.getString("TITLE"));
                a.setArtist(result.getString("ARTIST"));
                a.setReleaseDate(result.getString("RELEASE_DATE"));
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

    public String refresh() {
        try {
            albums = loadAlbums();
        } catch (SQLException ex) {
            Logger.getLogger(ViewCollectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
