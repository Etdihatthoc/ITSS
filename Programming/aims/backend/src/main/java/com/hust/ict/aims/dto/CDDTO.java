package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.CD;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class CDDTO extends ProductDTO {
    private String album;
    private String artist;
    private String recordLabel;
    private LocalDate releaseDate;
    private String tracklist;

    public static CDDTO fromEntity(CD cd) {
        CDDTO dto = new CDDTO();
        ProductDTO.mapCommonFields(cd, dto);
        dto.setAlbum(cd.getAlbum());
        dto.setArtist(cd.getArtist());
        dto.setRecordLabel(cd.getRecordLabel());
        dto.setReleaseDate(cd.getReleaseDate());
        dto.setTracklist(cd.getTracklist());
        return dto;
    }
}
