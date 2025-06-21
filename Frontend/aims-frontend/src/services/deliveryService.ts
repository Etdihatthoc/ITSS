import api from "./api";

interface CreateDeliveryInfoRequest {
  deliveryAddress: string;
  province: string;
  phoneNumber: string;
  recipientName: string;
  email: string;
}

interface DeliveryInfoResponse {
  id: string;
  deliveryAddress: string;
  province: string;
  phoneNumber: string;
  recipientName: string;
  email: string;
  createdAt: string;
}

const deliveryService = {
  createDeliveryInfo: async (data: CreateDeliveryInfoRequest) => {
    return await api.post<DeliveryInfoResponse>("/delivery-infos", data);
  },
};

export default deliveryService;
