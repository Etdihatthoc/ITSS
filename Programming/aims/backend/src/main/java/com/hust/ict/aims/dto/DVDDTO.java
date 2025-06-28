package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.DVD;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class DVDDTO extends ProductDTO {
    private LocalDate releaseDate;
    private String subtitle;
    private String language;
    private String studio;
    private String runtime;
    private String discType;
    private String director;
    private String genre;

    public static @NotNull DVDDTO fromEntity(DVD dvd) {
        DVDDTO dto = new DVDDTO();
        ProductDTO.mapCommonFields(dvd, dto);
        dto.setReleaseDate(dvd.getReleaseDate());
        dto.setSubtitle(dvd.getSubtitle());
        dto.setLanguage(dvd.getLanguage());
        dto.setStudio(dvd.getStudio());
        dto.setRuntime(dvd.getRuntime());
        dto.setDiscType(dvd.getDiscType());
        dto.setDirector(dvd.getDirector());
        dto.setGenre(dvd.getGenre());
        return dto;
    }
}