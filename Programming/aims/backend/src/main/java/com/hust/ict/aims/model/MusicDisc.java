package com.hust.ict.aims.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "musicdisc_type")
public abstract class MusicDisc extends Product {
    @Column(name = "album", nullable = false)
    private String album;

    @Column(name = "artist", nullable = false)
    private String artist;

    @Column(name = "recordlabel", nullable = false)
    private String recordLabel;

    @Column(name = "releasedate", nullable = true)
    private LocalDate releaseDate;

    @Column(name = "tracklist", length = 500)
    private String tracklist;

    @Column(name = "genre", nullable = false)
    private String genre;

    // Getters and setters
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getRecordLabel() { return recordLabel; }
    public void setRecordLabel(String recordLabel) { this.recordLabel = recordLabel; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getTracklist() { return tracklist; }
    public void setTracklist(String tracklist) { this.tracklist = tracklist; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
} 