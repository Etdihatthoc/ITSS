package com.hust.ict.aims.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hust.ict.aims.model.*;
import lombok.Data;

import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "productType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BookDTO.class, name = "BOOK"),
        @JsonSubTypes.Type(value = CDDTO.class, name = "CD"),
        @JsonSubTypes.Type(value = DVDDTO.class, name = "DVD"),
        @JsonSubTypes.Type(value = LPDTO.class, name = "LP")
})
@Data
public class ProductDTO {
    private Long id;
    private String productType;
    private String imageURL;
    private boolean rushOrderEligible;
    private float weight;
    private String productDimensions;
    private LocalDate warehouseEntryDate;
    private String title;
    private String category;
    private float value;
    private float currentPrice;
    private String barcode;
    private String productDescription;
    private int quantity;

    /**
     * Map các trường chung từ thực thể Product sang DTO
     */
    protected static void mapCommonFields(Product product, ProductDTO dto) {
        dto.setId(product.getId());
        dto.setProductType(getProductType(product));
        dto.setImageURL(product.getImageURL());
        dto.setRushOrderEligible(product.isRushOrderEligible());
        dto.setWeight(product.getWeight());
        dto.setProductDimensions(product.getProductDimensions());
        dto.setWarehouseEntryDate(product.getWarehouseEntryDate());
        dto.setTitle(product.getTitle());
        dto.setCategory(product.getCategory());
        dto.setValue(product.getValue());
        dto.setCurrentPrice(product.getCurrentPrice());
        dto.setBarcode(product.getBarcode());
        dto.setProductDescription(product.getProductDescription());
        dto.setQuantity(product.getQuantity());
    }

    /**
     * Xác định productType dựa trên kiểu thực thể
     */
    private static String getProductType(Product product) {
        if (product instanceof Book) return "BOOK";
        if (product instanceof CD) return "CD";
        if (product instanceof DVD) return "DVD";
        if (product instanceof LP) return "LP";
        return "UNKNOWN";
    }

    /**
     * Chuyển đổi chung từ entity sang DTO, xử lý polymorphism
     */
    public static ProductDTO fromEntity(Product product) {
        if (product instanceof Book) {
            return BookDTO.fromEntity((Book) product);
        } else if (product instanceof CD) {
            return CDDTO.fromEntity((CD) product);
        } else if (product instanceof DVD) {
            return DVDDTO.fromEntity((DVD) product);
        } else if (product instanceof LP) {
            return LPDTO.fromEntity((LP) product);
        } else {
            return mapBaseProduct(product);
        }
    }

    /**
     * Map cho trường hợp Product không phải subclass cụ thể
     */
    private static ProductDTO mapBaseProduct(Product product) {
        ProductDTO dto = new ProductDTO();
        mapCommonFields(product, dto);
        return dto;
    }
}
