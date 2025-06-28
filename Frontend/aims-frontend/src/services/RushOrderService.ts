import axios from "axios";
import { RushOrder } from "../types/RushOrder";

const API_URL = "http://localhost:8080/api/rush-orders";

// RushOrder service methods
const rushOrderService = {
    
    getRushOrders: async () => {
        return axios.get<RushOrder[]>(API_URL, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
        });
    },

    /**
     * Get a single rush order by ID
     * @param id Rush Order ID
     * @returns Promise with rush order data
     */
    getRushOrderById: async (id: string) => {
        return axios.get<RushOrder>(`${API_URL}/${id}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
        });
    },

    /**
     * Create a new rush order
     * @param rushOrder Rush order data
     * @returns Promise with created rush order
     */
    createRushOrder: async (rushOrder: RushOrder) => {
        return axios.post<RushOrder>(API_URL, rushOrder, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
                "Content-Type": "application/json",
            },
        });
    },

    /**
     * Update a rush order
     * @param id Rush Order ID
     * @param rushOrder Updated rush order data
     * @returns Promise with updated rush order
     */
    updateRushOrder: async (id: string, rushOrder: Partial<RushOrder>) => {
        return axios.put<RushOrder>(`${API_URL}/${id}`, rushOrder, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
                "Content-Type": "application/json",
            },
        });
    },

    /**
     * Delete a rush order
     * @param id Rush Order ID
     * @returns Promise with void response
     */
    deleteRushOrder: async (id: string) => {
        return axios.delete(`${API_URL}/${id}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
        });
    },

    /**
     * Check rush order eligibility
     * @param data Thông tin cần thiết để kiểm tra eligibility (ví dụ: address, province, cartId...)
     * @returns Promise với response từ API
     */
    checkEligibility: async (data: any) => {
        return axios.post(`${API_URL}/check-eligibility`, data, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
                "Content-Type": "application/json",
            },
        });
    },

    /**
     * Complete rush order checkout
     * @param checkoutRequest Thông tin checkout cho rush order
     * @returns Promise với rush order đã tạo
     */
    completeRushCheckout: async (checkoutRequest: {
        deliveryInfo: any;
        invoiceData: {
            cart: any;
            totalProductPriceBeforeVAT: number;
            totalProductPriceAfterVAT: number;
            deliveryFee: number;
            rushDeliveryFee: number;
            totalAmount: number;
        };
        transactionData: {
            transactionId: string;
            bankCode: string;
            amount: number;
            cardType: string;
            payDate: string;
            errorMessage: string;
        };
        status: string;
    }) => {
        try {
            // Format request theo RushCheckoutRequestDTO
            const rushCheckoutRequest = {
                deliveryInfo: {
                    ...checkoutRequest.deliveryInfo,
                    // Xóa các ID tạm thời
                    deliveryId: undefined,
                    id: undefined,
                },
                invoiceRequest: {
                    cart: {
                        totalProductPriceBeforeVAT: checkoutRequest.invoiceData.cart.totalProductPriceBeforeVAT,
                        items: checkoutRequest.invoiceData.cart.items.map((item: any) => ({
                            quantity: item.quantity,
                            productId: item.product.id,
                        })),
                    },
                    totalProductPriceBeforeVAT: checkoutRequest.invoiceData.totalProductPriceBeforeVAT,
                    totalProductPriceAfterVAT: checkoutRequest.invoiceData.totalProductPriceAfterVAT,
                    deliveryFee: checkoutRequest.invoiceData.deliveryFee,
                    totalAmount: checkoutRequest.invoiceData.totalAmount,
                },
                transactionRequest: {
                    transactionId: checkoutRequest.transactionData.transactionId,
                    transactionNo: checkoutRequest.transactionData.transactionId, // Sử dụng transactionId làm transactionNo
                    amount: checkoutRequest.transactionData.amount,
                    payDate: checkoutRequest.transactionData.payDate ? 
                        new Date(checkoutRequest.transactionData.payDate).toISOString().slice(0, 19) : 
                        new Date().toISOString().slice(0, 19),
                    gateway: checkoutRequest.transactionData.bankCode,
                    transactionStatus: "SUCCESS",
                    additionalParams: {
                        bankCode: checkoutRequest.transactionData.bankCode,
                        cardType: checkoutRequest.transactionData.cardType,
                        errorMessage: checkoutRequest.transactionData.errorMessage,
                    },
                },
                status: checkoutRequest.status,
                deliveryTime: (() => {
                    if (checkoutRequest.deliveryInfo.rushDeliveryTime) {
                        // Tạo datetime từ time string và ngày hiện tại
                        const today = new Date();
                        const [hours, minutes] = checkoutRequest.deliveryInfo.rushDeliveryTime.split(':');
                        today.setHours(parseInt(hours), parseInt(minutes), 0, 0);
                        return today.toISOString().slice(0, 19);
                    } else {
                        // Nếu không có thời gian, sử dụng thời gian hiện tại + 2 giờ
                        const futureTime = new Date();
                        futureTime.setHours(futureTime.getHours() + 2);
                        return futureTime.toISOString().slice(0, 19);
                    }
                })(),
                deliveryInstruction: checkoutRequest.deliveryInfo.rushDeliveryInstructions || "",
            };

            console.log("Sending rush checkout request:", rushCheckoutRequest);

            const response = await axios.post(`${API_URL}`, rushCheckoutRequest, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                    "Content-Type": "application/json",
                },
            });

            console.log("Rush checkout response:", response.data);
            return response;
        } catch (error: any) {
            console.error("Rush checkout error:", error);
            console.error("Error response:", error.response?.data);
            throw error;
        }
    },
};

export default rushOrderService;