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
  id: number;
  productType: string;
  title: string;
  category: string;
  value: number;
  currentPrice: number;
  productDescription?: string;
  barcode: string;
  quantity: number;
  warehouseEntryDate: string;
  productDimensions: string;
  weight: number;
  imageURL: string;
  rushOrderEligible?: boolean;
}

export interface Book extends BaseProduct {
  genre?: string;
  author: string;
  coverType: string;
  publisher: string;
  language: string;
  numberOfPage: number;
  publicationDate: string;
}

export interface CD extends BaseProduct {
  genre: string;
  album: string;
  artist: string;
  recordLabel: string;
  releaseDate: string;
  tracklist?: string;
}
export interface LP extends BaseProduct {
  genre: string;
  album: string;
  artist: string;
  recordLabel: string;
  releaseDate: string;
  tracklist?: string;
}

export interface DVD extends BaseProduct {
  genre?: string;
  releaseDate: string;
  subtitle: string;
  language: string;
  studio: string;
  runtime: string;
  discType: string;
  director: string;
}

// Updated ProductFilter - only includes fields that backend can support
export interface ProductFilter {
  // Basic pagination (backend supports this)
  page?: number;
  size?: number;
  
  // Search by title 
  search?: string;
  
  // Category filter 
  category?: string;
  
  // Media type filter 
  productType?: string;
  
  // Price range 
  minPrice?: number;
  maxPrice?: number;
  
  // Sorting 
  sortBy?: "title" | "currentPrice" | "warehouseEntryDate";
  sortDirection?: "asc" | "desc";
}

export interface ProductCreate {
  productType: string;
  title: string;
  category: string;
  value: number;
  currentPrice: number;
  productDescription?: string;
  barcode: string;
  quantity: number;
  warehouseEntryDate: string;
  productDimensions: string;
  weight: number;
  imageURL: string;
  rushOrderEligible?: boolean;
  genre: string;

  // Book specific properties - NOW REQUIRED for books
  author?: string;
  coverType?: string;
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
  discType?: string;
  director?: string;
  runtime?: string;
  studio?: string;
  subtitle?: string;
}

// Interface for updating an existing product
export interface ProductUpdate extends Partial<ProductCreate> {
  id?: string; // ID is optional for updates
}


export interface ProductQueryParams {
  page?: number;
  size?: number;
  search?: string;
  category?: string;
  productType?: string;
  minPrice?: number;
  maxPrice?: number;
  sortBy?: "title" | "currentPrice" | "warehouseEntryDate";
  sortDirection?: "asc" | "desc";
}
