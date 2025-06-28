import axios from "axios";
import { RushOrder } from "../types/rushorder";

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
};

export default rushOrderService;