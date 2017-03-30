package JavaObjects;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 *
 * @author Nicholas Clemmons
 */
@Named(value = "collectionsBean")
@SessionScoped
public class CollectionsBean implements Serializable {

    //resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private List<Collection> collections;
    private int numberOfCollections;

    @PostConstruct
    public void init() {

    }

    public List<Collection> getCollections() {
        return collections;
    }

    public List<Album> loadAlbums() throws SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        List<Album> list = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select ALBUM_ID, TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE from ALBUMTABLE"
            );

            // retrieve book data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Album a = new Album();
                a.setAlbumID(result.getInt("ALBUM_ID"));
                a.setTitle(result.getString("TITLE"));
                a.setArtist(result.getString("ARTIST"));
                a.setReleaseYear(result.getInt("YEAR"));
                a.setNumberOfTracks(result.getInt("NUMBER_OF_TRACKS"));
                a.setNumberOfDiscs(result.getInt("NUMBER_OF_DISCS"));
                a.setGenre(result.getString("GENRE"));
                list.add(a);
            }

        } finally {
            conn.close();
        }

        return list;
    }

    public int getNumberOfCollections() {
        return numberOfCollections;
    }

    public void setNumberOfCollections(int numberOfCollections) {
        this.numberOfCollections = numberOfCollections;
    }

}
