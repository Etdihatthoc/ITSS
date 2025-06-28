import type {Order} from './order';

export interface RushOrder extends Order {
    deliveryTime: string;
    deliveryInstructions: string;
}