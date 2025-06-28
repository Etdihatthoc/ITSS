package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.Book;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookDTO extends ProductDTO {
    private String author;
    private String coverType;
    private String publisher;
    private String language;
    private int numberOfPage;
    private LocalDate publicationDate;
    private String genre;

    public static BookDTO fromEntity(Book book) {
        BookDTO dto = new BookDTO();
        ProductDTO.mapCommonFields(book, dto); // Map trường chung
        // Map trường riêng
        dto.setAuthor(book.getAuthor());
        dto.setCoverType(book.getCoverType());
        dto.setPublisher(book.getPublisher());
        dto.setLanguage(book.getLanguage());
        dto.setNumberOfPage(book.getNumberOfPage());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setGenre(book.getGenre());
        return dto;
    }
}