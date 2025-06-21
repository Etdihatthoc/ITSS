package com.hust.ict.aims.model;

import jakarta.persistence.*;
import java.time.LocalDate;
/*
* Cohesion Level: Functional Cohesion
* Similarly demonstrates functional cohesion, with all members focused on representing an LP. The album, artist, record label, release date, and tracklist all support this single purpose.
 */
@Entity
@Table (name = "lp")
@DiscriminatorValue("LP")

public class LP extends Product {
    @Column(name = "album", nullable = false)
    private String album;

    @Column(name = "artist", nullable = false)
    private String artist;

    @Column(name = "recordlabel", nullable = false)
    private String recordLabel;

    @Column(name = "releasedate", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "tracklist", length = 500)
    private String tracklist;
    // Getters and setters
    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getRecordLabel() {
        return recordLabel;
    }
    public void setRecordLabel(String recordLabel) {
        this.recordLabel = recordLabel;
    }
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    public String getTracklist() {
        return tracklist;
    }
    public void setTracklist(String tracklist) {
        this.tracklist = tracklist;
    }
}
