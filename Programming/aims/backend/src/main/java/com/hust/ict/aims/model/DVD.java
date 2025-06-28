package com.hust.ict.aims.model;

import jakarta.persistence.*;
import java.time.LocalDate;
/*
* Cohesion Level: Functional Cohesion
* Similarly demonstrates functional cohesion, with all members focused on representing a DVD. The release date, subtitle, language, studio, runtime, disc type, and director all support this single purpose.
 */
@Entity
@Table(name = "dvd")
@DiscriminatorValue("DVD")

public class DVD extends Product {
    @Column(name = "releaseDate", nullable = true)
    private LocalDate releaseDate;

    @Column(name = "subtitle", length = 500)
    private String subtitle;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "studio", nullable = false)
    private String studio;

    @Column(name = "runtime", nullable = false)
    private String runtime; // INTERVAL not supported directly in JPA

    @Column(name = "discType", nullable = false)
    private String discType;

    @Column(name = "director", nullable = false)
    private String director;

    @Column(name = "genre", nullable = true)
    private String genre;

    // Getters and setters
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getStudio() {
        return studio;
    }
    public void setStudio(String studio) {
        this.studio = studio;
    }
    public String getRuntime() {
        return runtime;
    }
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
    public String getDiscType() {
        return discType;
    }
    public void setDiscType(String discType) {
        this.discType = discType;
    }
    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
}
