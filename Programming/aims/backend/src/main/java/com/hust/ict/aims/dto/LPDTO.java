package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.LP;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class LPDTO extends ProductDTO {
    private String album;
    private String artist;
    private String recordLabel;
    private LocalDate releaseDate;
    private String tracklist;

    @org.jetbrains.annotations.NotNull
    public static LPDTO fromEntity(LP lp) {
        LPDTO dto = new LPDTO();
        ProductDTO.mapCommonFields(lp, dto);
        dto.setAlbum(lp.getAlbum());
        dto.setArtist(lp.getArtist());
        dto.setRecordLabel(lp.getRecordLabel());
        dto.setReleaseDate(lp.getReleaseDate());
        dto.setTracklist(lp.getTracklist());
        return dto;
    }
}