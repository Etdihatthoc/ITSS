package com.hust.ict.aims.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import java.time.LocalDate;

/*
* Cohesion Level: Functional Cohesion
* Similarly demonstrates functional cohesion, with all members focused on representing a book. The author, cover type, publisher, language, number of pages, and publication date all support this single purpose.
 */
@Entity
@Table(name = "book")
@DiscriminatorValue("BOOK")
@JsonTypeName("BOOK")
public class Book extends Product {

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "covertype", nullable = false)
    private String coverType;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "language", nullable = true)
    private String language;

    @Column(name = "numberOfPage", nullable = true)
    private Integer numberOfPage;

    @Column(name = "publicationDate", nullable = false)
    private LocalDate publicationDate;

    @Column(name = "genre", nullable = true)
    private String genre;

    // Getters and setters
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCoverType() {
        return coverType;
    }
    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public int getNumberOfPage() {
        return this.numberOfPage != null ? this.numberOfPage.intValue() : 0;
    }
    public void setNumberOfPage(int numberOfPage) {
        this.numberOfPage = numberOfPage;
    }
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
}