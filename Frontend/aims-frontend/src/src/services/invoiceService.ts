// src/services/invoiceService.ts
import api from "./api";

interface InvoiceResponse {
  id: number;
  cart: {
    cartId: number;
    totalProductPriceBeforeVAT: number;
    items: Array<{
      id: number;
      quantity: number;
      product: {
        id: number;
        imageURL: string;
        rushOrderEligible: boolean;
        weight: number;
        productDimensions: string;
        warehouseEntryDate: string;
        title: string;
        category: string;
        value: number;
        currentPrice: number;
        barcode: string;
        productDescription: string;
        quantity: number;
        genre: string;
      };
    }>;
  };
  totalProductPriceAfterVAT: number;
  totalAmount: number;
  deliveryFee: number;
}

const invoiceService = {
  // Create an invoice from cart and delivery info
  createInvoice: async (cartId: number, deliveryId: number) => {
    return await api.post<InvoiceResponse>(
      `/invoices/cart/${cartId}/delivery/${deliveryId}`
    );
  },

  // Get invoice by ID
  getInvoice: async (invoiceId: number) => {
    return await api.get<InvoiceResponse>(`/invoices/${invoiceId}`);
  },
};

export default invoiceService;
