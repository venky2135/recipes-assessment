import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent, MatPaginator } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule, MatDrawer } from '@angular/material/sidenav';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs/operators';

import { RecipeService } from '../services/recipe.service';
import { Recipe } from '../services/models/recipe';

@Component({
  selector: 'recipe-table',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSidenavModule,
    MatDividerModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './recipe-table.html',
  styleUrls: ['./recipe-table.css']
})
export class RecipeTableComponent implements OnInit {
  displayedColumns = ['title', 'cuisine', 'rating', 'total_time', 'serves'];
  data: Recipe[] = [];
  total = 0;
  page = 0; // zero-based
  limit = 15;
  limits = [15, 20, 30, 50];
  loading = false;

  // ✅ Simplified filters (no operators)
  f_title = '';
  f_cuisine = '';
  f_ratingVal: number | null = null;
  f_totalVal: number | null = null;
  f_calVal: number | null = null;

  selected: Recipe | null = null;
  @ViewChild('drawer') drawer!: MatDrawer;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private api: RecipeService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.api.getAll(this.page + 1, this.limit).pipe(
      finalize(() => (this.loading = false))
    ).subscribe({
      next: res => {
        this.data = res?.data ?? [];
        this.total = res?.total ?? this.data.length ?? 0;
      },
      error: err => {
        console.error('❌ Failed to load recipes:', err);
        this.data = [];
        this.total = 0;
      }
    });
  }

  search() {
    const filters: any = {
      title: this.f_title || undefined,
      cuisine: this.f_cuisine || undefined,
      rating: this.f_ratingVal != null ? this.f_ratingVal : undefined,
      total_time: this.f_totalVal != null ? this.f_totalVal : undefined,
      calories: this.f_calVal != null ? this.f_calVal : undefined
    };

    this.loading = true;
    this.api.search(filters).pipe(
      finalize(() => (this.loading = false))
    ).subscribe({
      next: res => {
        this.data = res?.data ?? [];
        this.total = this.data.length;
        this.page = 0;
        if (this.paginator) {
          this.paginator.firstPage();
        }
      },
      error: err => {
        console.error('❌ Failed to search recipes:', err);
        this.data = [];
        this.total = 0;
      }
    });
  }

  clearFilters() {
    this.f_title = '';
    this.f_cuisine = '';
    this.f_ratingVal = null;
    this.f_totalVal = null;
    this.f_calVal = null;
    this.load();
  }

  onPage(ev: PageEvent) {
    this.page = ev.pageIndex;
    this.limit = ev.pageSize;
    this.load();
  }

  open(r: Recipe) {
    this.selected = r;
    this.drawer.open();
  }
}
