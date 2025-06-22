// src/types/product.ts
export enum MediaType {
  BOOK = "BOOK",
  CD = "CD",
  LP = "LP",
  DVD = "DVD",
}

export enum CoverType {
  PAPERBACK = "PAPERBACK",
  HARDCOVER = "HARDCOVER",
}

export enum DiscType {
  BLURAY = "BLURAY",
  HDDVD = "HDDVD",
  DVD = "DVD",
}

export type Product = Book | CD | LP | DVD;

export interface BaseProduct {
  id: string;
  title: string;
  category: string;
  value: number;
  currentPrice: number;
  productDescription: string;
  barcode: string;
  quantity: number;
  warehouseEntryDate: string;
  productDimensions: string;
  weight: number;
  imageURL?: string;
  mediaType: MediaType;
  rushOrderEligible?: boolean;
}

export interface Book extends BaseProduct {
  authors: string[];
  coverType: CoverType;
  publisher: string;
  publicationDate: string;
  pages?: number;
  language?: string;
  genre?: string;
  currentPrice: number;
}

export interface CD extends BaseProduct {
  artists: string[];
  recordLabel: string;
  tracklist: string[];
  genre: string;
  releaseDate?: string;
}

export interface LP extends BaseProduct {
  artists: string[];
  recordLabel: string;
  tracklist: string[];
  genre: string;
  releaseDate?: string;
}

export interface DVD extends BaseProduct {
  discType: DiscType;
  director: string;
  runtime: number;
  studio: string;
  language: string[];
  subtitles: string[];
  releaseDate?: string;
  genre?: string;
}

// Add a ProductFilter interface for filtering and sorting products
export interface ProductFilter {
  mediaType?: MediaType;
  minPrice?: number;
  maxPrice?: number;
  sortBy?: "price" | "title" | "releaseDate";
  sortDirection?: "asc" | "desc";
}

export interface ProductCreate {
  title: string;
  category: string;
  value: number;
  currentPrice: number;
  productDescription: string;
  barcode: string;
  quantity: number;
  warehouseEntryDate: string;
  productDimensions: string;
  weight: number;
  imageURL: string;
  rushOrderEligible?: boolean;
  mediaType: MediaType;
  genre?: string;

  // Book specific properties - NOW REQUIRED for books
  author?: string;
  coverType?: CoverType;
  publisher?: string;
  publicationDate?: string;
  numberOfPage?: number;
  language?: string;

  // CD/LP specific properties - NOW REQUIRED for CDs/LPs
  artist?: string;
  recordLabel?: string;
  tracklist?: string;
  album?: string;
  releaseDate?: string;

  // DVD specific properties - NOW REQUIRED for DVDs
  discType?: DiscType;
  director?: string;
  runtime?: string;
  studio?: string;
  subtitle?: string;
}
// export interface ProductCreate {
//   title: string;
//   category: string;
//   value: number;
//   price: number;
//   description: string;
//   barcode: string;
//   quantity: number;
//   warehouseEntryDate: string;
//   dimensions: string;
//   weight: number;
//   imageUrl?: string;
//   mediaType: MediaType;

//   // Book specific properties
//   authors?: string[];
//   coverType?: CoverType;
//   publisher?: string;
//   publicationDate?: string;
//   pages?: number;
//   language?: string | string[];
//   genre?: string;

//   // CD/LP specific properties
//   artists?: string[];
//   recordLabel?: string;
//   tracklist?: string[];
//   releaseDate?: string;

//   // DVD specific properties
//   discType?: DiscType;
//   director?: string;
//   runtime?: number;
//   studio?: string;
//   subtitles?: string[];
// }

// Interface for updating an existing product
export interface ProductUpdate extends Partial<ProductCreate> {
  id?: string; // ID is optional for updates
}

// Interface for product query parameters
export interface ProductQueryParams extends ProductFilter {
  page?: number;
  limit?: number;
  search?: string;
  category?: string;
}
