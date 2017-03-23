/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaObjects;

/**
 *
 * @author Nicholas Clemmons
 */
public class VinylRecord {
    
    private String albumTitle;
    private String artistName;
    private String genre;
    private int releaseYear;
    private int numberOfTracks;
    private int numberOfDiscs;
    
    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
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
    
}