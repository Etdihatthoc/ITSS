//package com.hust.ict.aims.dto;
//
//import com.hust.ict.aims.model.*;
//import org.junit.jupiter.api.Test;
//import java.time.LocalDate;
//import static org.junit.jupiter.api.Assertions.*;
//
//class ProductDTOMappingTest {
//
//    @Test
//    void testMapBookToDTO() {
//        // Tạo entity Book
//        Book book = new Book();
//        book.setId(1L);
//        book.setTitle("Clean Code");
//        book.setAuthor("Robert C. Martin");
//        book.setCoverType("Hardcover");
//        book.setPublisher("Prentice Hall");
//        book.setLanguage("English");
//        book.setNumberOfPage(464);
//        book.setPublicationDate(LocalDate.of(2008, 8, 1));
//
//        // Map sang DTO
//        BookDTO dto = BookDTO.fromEntity(book);
//
//        // Kiểm tra các trường
//        assertEquals(book.getId(), dto.getId());
//        assertEquals(book.getTitle(), dto.getTitle());
//        assertEquals(book.getAuthor(), dto.getAuthor());
//        assertEquals(book.getCoverType(), dto.getCoverType());
//        assertEquals(book.getPublisher(), dto.getPublisher());
//        assertEquals(book.getLanguage(), dto.getLanguage());
//        assertEquals(book.getNumberOfPage(), dto.getNumberOfPage());
//        assertEquals(book.getPublicationDate(), dto.getPublicationDate());
//        assertEquals("BOOK", dto.getProductType()); // Kiểm tra productType
//    }
//
//    @Test
//    void testMapCDToDTO() {
//        CD cd = new CD();
//        cd.setId(2L);
//        cd.setTitle("Thriller");
//        cd.setAlbum("Thriller");
//        cd.setArtist("Michael Jackson");
//        cd.setRecordLabel("Epic");
//        cd.setReleaseDate(LocalDate.of(1982, 11, 30));
//        cd.setTracklist("Track1, Track2, ...");
//
//        CDDTO dto = CDDTO.fromEntity(cd);
//
//        assertEquals(cd.getId(), dto.getId());
//        assertEquals(cd.getAlbum(), dto.getAlbum());
//        assertEquals(cd.getArtist(), dto.getArtist());
//        assertEquals("CD", dto.getProductType());
//    }
//
//    @Test
//    void testMapDVDToDTO() {
//        DVD dvd = new DVD();
//        dvd.setId(3L);
//        dvd.setTitle("Inception");
//        dvd.setDirector("Christopher Nolan");
//        dvd.setStudio("Warner Bros");
//        dvd.setRuntime("2h 30m");
//        dvd.setReleaseDate(LocalDate.of(2010, 7, 16));
//
//        DVDDTO dto = DVDDTO.fromEntity(dvd);
//
//        assertEquals(dvd.getId(), dto.getId());
//        assertEquals(dvd.getTitle(), dto.getTitle());
//        assertEquals(dvd.getDirector(), dto.getDirector());
//        assertEquals(dvd.getStudio(), dto.getStudio());
//        assertEquals(dvd.getRuntime(), dto.getRuntime());
//        assertEquals(dvd.getReleaseDate(), dto.getReleaseDate());
//        assertEquals("DVD", dto.getProductType());
//    }
//
//    @Test
//    void testMapLPToDTO() {
//        LP lp = new LP();
//        lp.setId(4L);
//        lp.setTitle("Abbey Road");
//        lp.setArtist("The Beatles");
//        lp.setRecordLabel("Apple Records");
//        lp.setReleaseDate(LocalDate.of(1969, 9, 26));
//        lp.setTracklist("Come Together, Something, ...");
//
//        LPDTO dto = LPDTO.fromEntity(lp);
//
//        assertEquals(lp.getId(), dto.getId());
//        assertEquals(lp.getTitle(), dto.getTitle());
//        assertEquals(lp.getArtist(), dto.getArtist());
//        assertEquals(lp.getRecordLabel(), dto.getRecordLabel());
//        assertEquals(lp.getReleaseDate(), dto.getReleaseDate());
//        assertEquals(lp.getTracklist(), dto.getTracklist());
//        assertEquals("LP", dto.getProductType());
//    }
//}
