export interface Recipe {
    id: number;
    title: string;
    cuisine: string;
    rating: number;
    prep_time: number;
    cook_time: number;
    total_time: number;
    description: string;
    nutrients: { [key: string]: string };
    serves: string;
}