import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from '../services/models/page-response';
import { Recipe } from '../services/models/recipe';

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getAll(page = 1, limit = 15): Observable<PageResponse> {
    const params = new HttpParams().set('page', page).set('limit', limit);
    return this.http.get<PageResponse>(`${this.base}/recipes`, { params });
  }

  search(filters: any): Observable<{data: Recipe[]}> {
    let params = new HttpParams();
    Object.keys(filters).forEach(k => {
      const v = filters[k];
      if (v !== null && v !== undefined && `${v}`.trim() !== '') {
        params = params.set(k, v);
      }
    });
    return this.http.get<{data: Recipe[]}>(`${this.base}/recipes/search`, { params });
  }
}