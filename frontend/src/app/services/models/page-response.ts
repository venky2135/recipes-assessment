import { Recipe } from './recipe';

export interface PageResponse {
    page: number;
    limit: number;
    total: number;
    data: Recipe[];
}