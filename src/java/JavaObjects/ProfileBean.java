package JavaObjects;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.models.Album;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.SimpleTrack;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.Principal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import org.primefaces.event.SelectEvent;
import org.xhtmlrenderer.pdf.ITextRenderer;

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
    Album currentlyLoadedAlbum;
    List<SimpleTrack> selectedTracks;
    private List<Collection> collections;
    private int numberOfCollections;
    private Collection selectedCollection;
    private Record selectedRecord;
    private String username;
    private String oldCollectionName;
    
     private Part part;
    private List<UserBean> list;

    @PostConstruct
    public void init() {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            Principal p = fc.getExternalContext().getUserPrincipal();
            username = p.getName();
            collections = loadCollections();
            numberOfCollections = collections.size();
            //selectedCollection = collections.get(0);
            //selectedRecord = selectedCollection.getRecords().get(0);
        } catch (SQLException ex) {
            Logger.getLogger(ProfileBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh() {
        try {
            collections = loadCollections();
            numberOfCollections = collections.size();
            //selectedCollection = collections.get(0);
            //selectedRecord = selectedCollection.getRecords().get(0);
        } catch (SQLException ex) {
            Logger.getLogger(ProfileBean.class.getName()).log(Level.SEVERE, null, ex);
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
                c.setOwnerName(username);
                c.setNumberOfRecords(result.getInt("COUNT(*)"));
                c.setRecords(this.loadAlbums(c.getCollectionName()));
                list.add(c);
            }
        } finally {
            conn.close();
        }
        return list;
    }

    public void createCollection() throws IOException, SQLException {
        
        refresh();
        String newCollectionName = username + "'s Collection #" + (numberOfCollections+1);
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        PreparedStatement ps = conn.prepareStatement(
                "insert into COLLECTION (COLLECTION_NAME, OWNER) "
                + "values (?,?)"
        );

        
        try {
            ps.setString(1, newCollectionName );
            ps.setString(2, username);
            
            int result = ps.executeUpdate();
            
            if (result == 1) {
                FacesMessage msg = new FacesMessage("Collection Added", newCollectionName);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } finally {
            conn.close();

        }
        
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

    public void deleteSelectedRecordFromCollection() throws IOException, SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        PreparedStatement deleteQuery = conn.prepareStatement(
                "DELETE FROM collection_items WHERE COLLECTION_NAME= ? AND OWNER= ? AND ALBUM_ID= ?;"
        );

        try {
            deleteQuery.setString(1, selectedCollection.getCollectionName());
            deleteQuery.setString(2, this.getUsername());
            deleteQuery.setString(3, selectedRecord.getAlbumID());
            int result = deleteQuery.executeUpdate();
            if (result == 0) {
                FacesMessage msg = new FacesMessage("Record Deleted", this.selectedRecord.getTitle());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage("Record Deletion Failed", this.selectedRecord.getTitle());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

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

        c.setEditable(false);
    }

    //Use for getting tracks.
    //Album object has reference to list of tracks
    public void albumSearchById(String idToFind) {
        //String albumId = "6fRqzJT070Kp9RWlSXmKcY";
        AlbumRequest request = api.getAlbum(idToFind).build();
        try {
            currentlyLoadedAlbum = request.get();
            Page<SimpleTrack> trackList = currentlyLoadedAlbum.getTracks();
            selectedTracks = trackList.getItems();
        } catch (IOException | WebApiException ex) {
            Logger.getLogger(AlbumSearchBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String convertTrackLength(int trackInMs) {

        int minutes = (int) ((trackInMs / (1000 * 60)) % 60);
        int seconds = (int) (trackInMs / 1000) % 60;
        //long minutes = TimeUnit.MILLISECONDS.toMinutes(trackInMs) % 60;
        //long seconds = TimeUnit.MILLISECONDS.toSeconds(trackInMs) % 60;
        String resultTime = minutes + ":" + seconds;
        return resultTime;

    }

    public void onCollectionSelect(SelectEvent event) {
        Collection collect = (Collection) event.getObject();
        //albumSearchById(record.getId());
        FacesMessage msg = new FacesMessage("Collection Selected", collect.getCollectionName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        refresh();
    }

    public void onRecordSelect(SelectEvent event) {
        Record currentRecord = (Record) event.getObject();
        albumSearchById(currentRecord.getAlbumID());
        FacesMessage msg = new FacesMessage("Record Selected", currentRecord.getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        refresh();
    }

    public void addAlbumToCollection(Collection c) throws IOException, SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
//        PreparedStatement insertQuery = conn.prepareStatement(
//                "INSERT INTO ALBUMTABLE(ALBUM_ID, TITLE, ARTIST, RELEASE_DATE, GENRE) "
//                + "VALUES(?,?,?,?,?)"
//        );
        PreparedStatement insertQuery = conn.prepareStatement(
                "INSERT INTO ALBUMTABLE(ALBUM_ID, TITLE, ARTIST, RELEASE_DATE) "
                + "VALUES(?,?,?,?)"
        );

        //insertQuery.setInt(5, numberOfTracks);
        //insertQuery.setInt(6, numberOfDiscs);
        //insertQuery.setInt(8, albumCount);
        PreparedStatement insertQuery2 = conn.prepareStatement(
                "INSERT INTO COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME, OWNER) "
                + "VALUES(?,?,?)"
        );

        try {
            insertQuery.setString(1, this.currentlyLoadedAlbum.getId());
            insertQuery.setString(2, this.currentlyLoadedAlbum.getName());
            insertQuery.setString(3, this.currentlyLoadedAlbum.getArtists().get(0).getName());
            insertQuery.setString(4, this.currentlyLoadedAlbum.getReleaseDate());
            //insertQuery.setString(5, this.currentlyLoadedAlbum.getGenres().get(0));

            insertQuery2.setString(1, this.currentlyLoadedAlbum.getId());
            insertQuery2.setString(2, c.getCollectionName());
            insertQuery2.setString(3, c.getOwnerName());

            int result = insertQuery.executeUpdate();
            result = insertQuery2.executeUpdate();
            if (result == 1) {
                FacesMessage msg = new FacesMessage("Record Added", this.currentlyLoadedAlbum.getName());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } finally {
            conn.close();
        }
    }

    public Album getTest() {
        return currentlyLoadedAlbum;
    }

    public void setTest(Album currentlyLoadedAlbum) {
        this.currentlyLoadedAlbum = currentlyLoadedAlbum;
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

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////// 
    /////////////////////////////////////////////////////////////////////////
//  public void upload2() throws MessagingException {
//    try {
//      fileContent = new Scanner(file2.getInputStream())
//          .useDelimiter("\\A").next();
//    } catch (IOException e) {
//      // Error handling
//    }
//  }
// 
//  
//    
//    
    public String saveAction() throws SQLException {
        //get all existing value but set "editable" to false 
        for (Collection book : loadCollections()) {
            book.setEditable(false);
        }

        return null;
    }

    public String editAction(Collection book) {
        book.setEditable(true);
        return null;
    }

    public void exportPdf() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpSession session = (HttpSession) externalContext.getSession(true);
        String url = "http://localhost:8080/WSPTermProject/faces/customerFolder/profile.xhtml;jsessionid=" + session.getId() + "?pdf=true";

        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(new URL(url).toString());
            renderer.layout();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"print-file.pdf\"");
            OutputStream outputStream = response.getOutputStream();
            renderer.createPDF(outputStream);

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        facesContext.responseComplete();

    }

//    
    public void edit(Collection todo) {
        for (Collection existing : getCollections()) {
            existing.setEditable(false);
        }
        todo.setEditable(true);
        oldCollectionName = todo.getCollectionName();
        selectedCollection = todo;
    }
//

    public void cancelEdit(Collection todo) {
        todo.setEditable(false);

    }

    public void save(Collection todo) {
        collections.set(collections.indexOf(todo), todo);
        cancelEdit(todo);
    }
    
    private List<UserBean> loadFileList() throws SQLException {
        
        List<UserBean> files = new ArrayList<>();
        Connection conn = ds.getConnection();

        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(
                    "SELECT FILE_ID, FILE_NAME, FILE_TYPE, FILE_SIZE FROM FILESTORAGE"
            );
            while (result.next()) {
                UserBean file = new UserBean();
                file.setId(result.getLong("FILE_ID"));
                file.setName(result.getString("FILE_NAME"));
                file.setType(result.getString("FILE_TYPE"));
                file.setSize(result.getLong("FILE_SIZE"));
                files.add(file);
            }
        } finally {
            conn.close();
        }
        return files;
        
    }

    public void uploadFile() throws IOException, SQLException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Connection conn = ds.getConnection();

        InputStream inputStream;
        inputStream = null;
        try {
            inputStream = part.getInputStream();
            PreparedStatement insertQuery = conn.prepareStatement(
                    "UPDATE USERTABLE SET FILE_CONTENTS = ? where username ='" + username + "'");
                    
            
            insertQuery.setBinaryStream(1, inputStream);

            int result = insertQuery.executeUpdate();
            if (result == 1) {
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                part.getSubmittedFileName()
                                + ": uploaded successfuly !!", null));
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

    public void validateFile(FacesContext ctx, UIComponent comp, Object value) {
        if (value == null) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Select a file to upload", null));
        }
        Part file = (Part) value;
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

    
//    
//    
//    
//     
//     private List<Collection> loadFileList() throws SQLException {
//        
//        List<Collection> files = new ArrayList<>();
//        Connection conn = ds.getConnection();
//
//        try {
//            Statement stmt = conn.createStatement();
//            ResultSet result = stmt.executeQuery(
//                    "SELECT FILE_ID, FILE_NAME, FILE_TYPE, FILE_SIZE FROM FILESTORAGE"
//            );
//            while (result.next()) {
//                Collection file = new Collection();
//                
//            }
//        } finally {
//            conn.close();
//        }
//        return files;
//    }
//
//    public void uploadFile() throws IOException, SQLException, MessagingException {
//
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//
//        Connection conn = ds.getConnection();
//
//        InputStream inputStream;
//        inputStream = null;
//        try {
//            inputStream = part.getInputStream();
//            PreparedStatement insertQuery = conn.prepareStatement(
//                    "INSERT INTO FILESTORAGE (FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS) "
//                    + "VALUES (?,?,?,?)");
//           
//            insertQuery.setString(2, part.getContentType());
//            insertQuery.setLong(3, part.getSize());
//            insertQuery.setBinaryStream(4, inputStream);
//
//            int result = insertQuery.executeUpdate();
//            
//            
//        
//        } finally {
//            if (inputStream != null) {
//                inputStream.close();
//            }
//            if (conn != null) {
//                conn.close();
//            }
//        }
//    }
//
//    public void validateFile(FacesContext ctx, UIComponent comp, Object value) {
//        if (value == null) {
//            throw new ValidatorException(
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                            "Select a file to upload", null));
//        }
//        javax.servlet.http.Part file = (javax.servlet.http.Part) value;
//        long size = file.getSize();
//        if (size <= 0) {
//            throw new ValidatorException(
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                            "the file is empty", null));
//        }
//        if (size > 1024 * 1024 * 10) { // 10 MB limit
//            throw new ValidatorException(
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                            size + "bytes: file too big (limit 10MB)", null));
//        }
//    }
//
//    public Part getPart() {
//        return part;
//    }
//
//    public void setPart(Part part) {
//        this.part = part;
//    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int fileID = Integer.parseInt(request.getParameter("fileid"));
        String user = request.getParameter("username");
        String inLineParam = request.getParameter("inline");
        boolean inLine = false;
        if (inLineParam != null && inLineParam.equals("true")) {
            inLine = true;
        }

        try {
            Connection conn = ds.getConnection();
            PreparedStatement selectQuery = conn.prepareStatement(
                    "SELECT * FROM USERTABLE WHERE USERNAME='" + username + "'");
            

            ResultSet result = selectQuery.executeQuery();
            if (!result.next()) {
                System.out.println("***** SELECT query failed for ImageServlet");
            }

            String fileType = result.getString("FILE_TYPE");
            String fileName = result.getString("FILE_NAME");
            long fileSize = result.getLong("FILE_SIZE");
            Blob fileBlob = result.getBlob("FILE_CONTENTS");

            response.setContentType(fileType);
            if (inLine) {
                response.setHeader("Content-Disposition", "inline; filename=\""
                        + fileName + "\"");
            } else {
                response.setHeader("Content-Disposition", "attachment; filename=\""
                        + fileName + "\"");
            }

            final int BYTES = 1024;
            int length = 0;
            InputStream in = fileBlob.getBinaryStream();
            OutputStream out = response.getOutputStream();
            byte[] bbuf = new byte[BYTES];

            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                out.write(bbuf, 0, length);
            }

            out.flush();
            out.close();
            conn.close();

        } catch (SQLException e) {

        }
    }
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        int fileID = Integer.parseInt(request.getParameter("fileid"));
//        String inLineParam = request.getParameter("inline");
//        boolean inLine = false;
//        if (inLineParam != null && inLineParam.equals("true")) {
//            inLine = true;
//        }
//
//        try {
//            Connection conn = ds.getConnection();
//            PreparedStatement selectQuery = conn.prepareStatement(
//                    "SELECT * FROM FILESTORAGE WHERE FILE_ID=?");
//            selectQuery.setInt(1, fileID);
//
//            ResultSet result = selectQuery.executeQuery();
//            if (!result.next()) {
//                System.out.println("***** SELECT query failed for ImageServlet");
//            }
//
//            String fileType = result.getString("FILE_TYPE");
//            String fileName = result.getString("FILE_NAME");
//            long fileSize = result.getLong("FILE_SIZE");
//            Blob fileBlob = result.getBlob("FILE_CONTENTS");
//
//            response.setContentType(fileType);
//            if (inLine) {
//                response.setHeader("Content-Disposition", "inline; filename=\""
//                        + fileName + "\"");
//            } else {
//                response.setHeader("Content-Disposition", "attachment; filename=\""
//                        + fileName + "\"");
//            }
//
//            final int BYTES = 1024;
//            int length = 0;
//            InputStream in = fileBlob.getBinaryStream();
//            OutputStream out = response.getOutputStream();
//            byte[] bbuf = new byte[BYTES];
//
//            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
//                out.write(bbuf, 0, length);
//            }
//
//            out.flush();
//            out.close();
//            conn.close();
//
//        } catch (SQLException e) {
//
//        }
//    }
}
