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

@Named(value = "albumsBean")
@SessionScoped
public class AlbumsBean implements Serializable {

    //resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private List<Record> albums;
    private String fromCollection;

    @PostConstruct
    public void init() {
        try {
            albums = loadAlbums(fromCollection);
        } catch (SQLException ex) {
            Logger.getLogger(AlbumsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getCollection() {
        return fromCollection;
    }

    public void setCollection(String collection) {
        this.fromCollection = collection;
        init();
    }

    public List<Record> getAlbums() {
        return albums;
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
                "SELECT a.* FROM collection_items AS c JOIN albumtable AS a WHERE a.ALBUM_ID=c.ALBUM_ID and collection_name='"+collection_name+"'"
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
}
