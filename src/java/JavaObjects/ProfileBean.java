package JavaObjects;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.sql.DataSource;
import org.primefaces.model.UploadedFile;


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
    private String username;
    private UploadedFile file;
    boolean editable;
    private Part part;
    
     private Part file2;
  private String fileContent;

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

    public List<Collection> getCollections() {
        return collections;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
                "SELECT collection_name, COUNT(*) FROM collection_items WHERE OWNER = '"+username+"' GROUP BY collection_name "
            );
            // retrieve book data from database
            ResultSet result = ps.executeQuery();
            
            while (result.next()) {
                Collection c = new Collection();
                UserBean s = new UserBean();
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
           "DELETE FROM collection WHERE COLLECTION_NAME='"+c.getCollectionName()+"' AND OWNER='"+username+"'"
       );
       PreparedStatement ps2 = conn.prepareStatement(
           "DELETE FROM collection_items WHERE COLLECTION_NAME='"+c.getCollectionName()+"' AND OWNER='"+username+"'"
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
           "Update collection Set COLLECTION_NAME= ? Where OWNER=?"
       );
       PreparedStatement ps2 = conn.prepareStatement(
           "Update collection_items set COLLECTION_NAME= ? Where OWNER = ?"
       );
       
       PreparedStatement ps3 = conn.prepareStatement("SET foreign_key_checks = 0");
       PreparedStatement ps4 = conn.prepareStatement("SET foreign_key_checks = 1");
       // retrieve book data from database
       try {
           ps2.setString(1, c.getCollectionName());
            ps2.setString(2, username);
             ps.setString(1, c.getCollectionName());
            ps.setString(2, username);
           ps3.executeUpdate();
            ps2.executeUpdate();
           ps.executeUpdate();
           ps4.executeUpdate();
       } finally {
           conn.close();
           
           
       }
       
       
   }


 
   
   
     
    
     

   
 
  public void upload2() throws MessagingException {
    try {
      fileContent = new Scanner(file2.getInputStream())
          .useDelimiter("\\A").next();
    } catch (IOException e) {
      // Error handling
    }
  }
 
  
    
    
     public String saveAction() {
    //get all existing value but set "editable" to false 
    for (Collection book : collections){
      book.setEditable(false);
    }
    //return to current page
    return null;
  }
  public String editAction(Collection book) {
    book.setEditable(true);
    return null;
  }
    
    public void edit(Collection todo){
        for (Collection existing : getCollections()){
            existing.setEditable(false);
        }
        todo.setEditable(true);
    }

    public void cancelEdit(Collection todo){
        todo.setEditable(false);
        
    }
    
    public void save(Collection todo){
        collections.set(collections.indexOf(todo), todo);
        cancelEdit(todo);
    }
    
    
    
     
     private List<Collection> loadFileList() throws SQLException {
        
        List<Collection> files = new ArrayList<>();
        Connection conn = ds.getConnection();

        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(
                    "SELECT FILE_ID, FILE_NAME, FILE_TYPE, FILE_SIZE FROM FILESTORAGE"
            );
            while (result.next()) {
                Collection file = new Collection();
                
            }
        } finally {
            conn.close();
        }
        return files;
    }

    public void uploadFile() throws IOException, SQLException, MessagingException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Connection conn = ds.getConnection();

        InputStream inputStream;
        inputStream = null;
        try {
            inputStream = part.getInputStream();
            PreparedStatement insertQuery = conn.prepareStatement(
                    "INSERT INTO FILESTORAGE (FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS) "
                    + "VALUES (?,?,?,?)");
           
            insertQuery.setString(2, part.getContentType());
            insertQuery.setLong(3, part.getSize());
            insertQuery.setBinaryStream(4, inputStream);

            int result = insertQuery.executeUpdate();
            
            
        
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void validateFile(FacesContext ctx, UIComponent comp, Object value) {
        if (value == null) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Select a file to upload", null));
        }
        javax.servlet.http.Part file = (javax.servlet.http.Part) value;
        long size = file.getSize();
        if (size <= 0) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "the file is empty", null));
        }
        if (size > 1024 * 1024 * 10) { // 10 MB limit
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            size + "bytes: file too big (limit 10MB)", null));
        }
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }


}