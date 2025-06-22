package com.hust.ict.aims.factory;

import com.hust.ict.aims.dto.ProductCreateRequest;
import com.hust.ict.aims.model.*;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory {
    
    public Product createProduct(ProductCreateRequest request) {
        return switch (request.getMediaType()) {
            case BOOK -> createBook(request);
            case CD -> createCD(request);
            case LP -> createLP(request);
            case DVD -> createDVD(request);
        };
    }
    
    private Book createBook(ProductCreateRequest request) {
        Book book = new Book();
        setCommonFields(book, request);
        book.setAuthor(request.getAuthor());
        book.setCoverType(request.getCoverType());
        book.setPublisher(request.getPublisher());
        book.setPublicationDate(request.getPublicationDate());
        book.setLanguage(request.getLanguage());
        book.setNumberOfPage(request.getNumberOfPage());
        return book;
    }
    
    private CD createCD(ProductCreateRequest request) {
        CD cd = new CD();
        setCommonFields(cd, request);
        cd.setAlbum(request.getAlbum());
        cd.setArtist(request.getArtist());
        cd.setRecordLabel(request.getRecordLabel());
        cd.setReleaseDate(request.getReleaseDate());
        cd.setTracklist(request.getTracklist());
        return cd;
    }
    
    private LP createLP(ProductCreateRequest request) {
        LP lp = new LP();
        setCommonFields(lp, request);
        lp.setAlbum(request.getAlbum());
        lp.setArtist(request.getArtist());
        lp.setRecordLabel(request.getRecordLabel());
        lp.setReleaseDate(request.getReleaseDate());
        lp.setTracklist(request.getTracklist());
        return lp;
    }
    
    private DVD createDVD(ProductCreateRequest request) {
        DVD dvd = new DVD();
        setCommonFields(dvd, request);
        dvd.setDirector(request.getDirector());
        dvd.setStudio(request.getStudio());
        dvd.setRuntime(request.getRuntime());
        dvd.setDiscType(request.getDiscType());
        dvd.setSubtitle(request.getSubtitle());
        dvd.setLanguage(request.getLanguage());
        dvd.setReleaseDate(request.getReleaseDate());
        return dvd;
    }
    
    private void setCommonFields(Product product, ProductCreateRequest request) {
        product.setTitle(request.getTitle());
        product.setCategory(request.getCategory());
        product.setValue(request.getValue());
        product.setCurrentPrice(request.getCurrentPrice());
        product.setProductDescription(request.getProductDescription());
        product.setBarcode(request.getBarcode());
        product.setQuantity(request.getQuantity());
        product.setWarehouseEntryDate(request.getWarehouseEntryDate());
        product.setProductDimensions(request.getProductDimensions());
        product.setWeight(request.getWeight());
        product.setImageURL(request.getImageURL());
        product.setRushOrderEligible(request.isRushOrderEligible());
        product.setGenre(request.getGenre());
    }
}