package JavaObjects;

import java.io.IOException;
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
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.sql.DataSource;

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

    private List<Collection> collections;
    private int numberOfCollections;
    private Collection selectedCollection;
    private Album selectedRecord;

    @PostConstruct
    public void init() {
        try {
            collections = loadCollections();
            numberOfCollections = collections.size();
        } catch (SQLException ex) {
            Logger.getLogger(ViewCollectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public List<Album> loadAlbums(String collection_name) throws SQLException {

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
                "SELECT a.* FROM collection_items AS c JOIN albumtable AS a WHERE a.ALBUM_ID=c.ALBUM_ID and collection_name='"+collection_name+"'"
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
                a.setAlbumCount(result.getInt("ALBUMCOUNT"));
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

    public Collection getSelectedCollection() {
        return selectedCollection;
    }

    public void setSelectedCollection(Collection selectedCollection) {
        this.selectedCollection = selectedCollection;
    }

    public Album getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(Album selectedRecord) {
        this.selectedRecord = selectedRecord;
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
            PreparedStatement ps = conn.prepareStatement(
                "SELECT collection_name, COUNT(*) FROM collection_items GROUP BY collection_name;"
            );
            // retrieve book data from database
            ResultSet result = ps.executeQuery();
            
            while (result.next()) {
                Collection c = new Collection();
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
           "DELETE FROM collection WHERE COLLECTION_NAME='"+c.getCollectionName()+"' AND OWNER='john';"
       );
       PreparedStatement ps2 = conn.prepareStatement(
           "DELETE FROM collection_items WHERE COLLECTION_NAME='"+c.getCollectionName()+"' AND OWNER='john';"
       );
       
       // retrieve book data from database
       try {
           ps2.executeQuery();
           ps.executeQuery();
       } finally {
           conn.close();
       }
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
           "UPDATE COLLECTION SET COLLECTION_NAME = ? WHERE OWNER='john';"
       );
        try {
            ps.setString(1, c.getCollectionName());
           
            ps.executeUpdate();
        

      
       
      
         
          
       } finally {
           conn.close();
       }
   }


     

}
