import React from 'react';
import { TextField, Select, MenuItem, FormControl, InputLabel, Box } from '@mui/material';
import { MediaType, CoverType, DiscType } from '../../types/product';

interface MediaSpecificFieldsProps {
  mediaType: MediaType;
  formData: any;
  handleInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleSelectChange: (e: any) => void;
  formErrors: Record<string, string>;
}

export const MediaSpecificFields: React.FC<MediaSpecificFieldsProps> = ({
  mediaType,
  formData,
  handleInputChange,
  handleSelectChange,
  formErrors
}) => {
  const renderBookFields = () => (
    <>
      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          name="author"
          label="Author"
          required
          value={formData.author || ''}
          onChange={handleInputChange}
          error={!!formErrors.author}
          helperText={formErrors.author}
          sx={{ flex: 1 }}
        />
        <FormControl sx={{ flex: 1 }} error={!!formErrors.coverType}>
          <InputLabel>Cover Type</InputLabel>
          <Select
            name="coverType"
            value={formData.coverType || ''}
            onChange={handleSelectChange}
            label="Cover Type"
          >
            <MenuItem value={CoverType.PAPERBACK}>Paperback</MenuItem>
            <MenuItem value={CoverType.HARDCOVER}>Hardcover</MenuItem>
          </Select>
        </FormControl>
      </Box>
      
      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          name="publisher"
          label="Publisher"
          required
          value={formData.publisher || ''}
          onChange={handleInputChange}
          error={!!formErrors.publisher}
          helperText={formErrors.publisher}
          sx={{ flex: 1 }}
        />
        <TextField
          name="publicationDate"
          label="Publication Date"
          type="date"
          required
          value={formData.publicationDate || ''}
          onChange={handleInputChange}
          error={!!formErrors.publicationDate}
          helperText={formErrors.publicationDate}
          InputLabelProps={{ shrink: true }}
          sx={{ flex: 1 }}
        />
      </Box>
      
      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          name="language"
          label="Language"
          required
          value={formData.language || ''}
          onChange={handleInputChange}
          error={!!formErrors.language}
          helperText={formErrors.language}
          sx={{ flex: 1 }}
        />
        <TextField
          name="pages"
          label="Number of Pages"
          type="number"
          value={formData.pages || ''}
          onChange={handleInputChange}
          error={!!formErrors.pages}
          helperText={formErrors.pages}
          InputProps={{ inputProps: { min: 1 } }}
          sx={{ flex: 1 }}
        />
      </Box>
    </>
  );

  const renderCDLPFields = () => (
    <>
      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          name="artist"
          label="Artist"
          required
          value={formData.artist || ''}
          onChange={handleInputChange}
          error={!!formErrors.artist}
          helperText={formErrors.artist}
          sx={{ flex: 1 }}
        />
        <TextField
          name="album"
          label="Album"
          required
          value={formData.album || ''}
          onChange={handleInputChange}
          error={!!formErrors.album}
          helperText={formErrors.album}
          sx={{ flex: 1 }}
        />
      </Box>
      
      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          name="recordLabel"
          label="Record Label"
          required
          value={formData.recordLabel || ''}
          onChange={handleInputChange}
          error={!!formErrors.recordLabel}
          helperText={formErrors.recordLabel}
          sx={{ flex: 1 }}
        />
        <TextField
          name="releaseDate"
          label="Release Date"
          type="date"
          value={formData.releaseDate || ''}
          onChange={handleInputChange}
          error={!!formErrors.releaseDate}
          helperText={formErrors.releaseDate}
          InputLabelProps={{ shrink: true }}
          sx={{ flex: 1 }}
        />
      </Box>
      
      <Box sx={{ mb: 2 }}>
        <TextField
          name="tracklist"
          label="Tracklist"
          fullWidth
          multiline
          rows={3}
          value={formData.tracklist || ''}
          onChange={handleInputChange}
          error={!!formErrors.tracklist}
          helperText={formErrors.tracklist || "Enter tracks separated by commas"}
        />
      </Box>
    </>
  );

  const renderDVDFields = () => (
    <>
      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          name="director"
          label="Director"
          required
          value={formData.director || ''}
          onChange={handleInputChange}
          error={!!formErrors.director}
          helperText={formErrors.director}
          sx={{ flex: 1 }}
        />
        <TextField
          name="studio"
          label="Studio"
          required
          value={formData.studio || ''}
          onChange={handleInputChange}
          error={!!formErrors.studio}
          helperText={formErrors.studio}
          sx={{ flex: 1 }}
        />
      </Box>
      
      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          name="runtime"
          label="Runtime"
          required
          value={formData.runtime || ''}
          onChange={handleInputChange}
          error={!!formErrors.runtime}
          helperText={formErrors.runtime || "e.g., 2h 30m"}
          sx={{ flex: 1 }}
        />
        <FormControl sx={{ flex: 1 }} error={!!formErrors.discType}>
          <InputLabel>Disc Type</InputLabel>
          <Select
            name="discType"
            value={formData.discType || ''}
            onChange={handleSelectChange}
            label="Disc Type"
          >
            <MenuItem value={DiscType.DVD}>DVD</MenuItem>
            <MenuItem value={DiscType.BLURAY}>Blu-ray</MenuItem>
            <MenuItem value={DiscType.HDDVD}>HD-DVD</MenuItem>
          </Select>
        </FormControl>
      </Box>
      
      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          name="language"
          label="Language"
          required
          value={formData.language || ''}
          onChange={handleInputChange}
          error={!!formErrors.language}
          helperText={formErrors.language}
          sx={{ flex: 1 }}
        />
        <TextField
          name="subtitle"
          label="Subtitles"
          value={formData.subtitle || ''}
          onChange={handleInputChange}
          error={!!formErrors.subtitle}
          helperText={formErrors.subtitle || "Available subtitle languages"}
          sx={{ flex: 1 }}
        />
      </Box>
      
      <Box sx={{ mb: 2 }}>
        <TextField
          name="releaseDate"
          label="Release Date"
          type="date"
          fullWidth
          value={formData.releaseDate || ''}
          onChange={handleInputChange}
          error={!!formErrors.releaseDate}
          helperText={formErrors.releaseDate}
          InputLabelProps={{ shrink: true }}
        />
      </Box>
    </>
  );

  return (
    <>
      {mediaType === MediaType.BOOK && renderBookFields()}
      {(mediaType === MediaType.CD || mediaType === MediaType.LP) && renderCDLPFields()}
      {mediaType === MediaType.DVD && renderDVDFields()}
    </>
  );
};