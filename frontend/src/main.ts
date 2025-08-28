import { bootstrapApplication } from '@angular/platform-browser';
import { importProvidersFrom } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { RecipeTableComponent } from './app/recipe-table/recipe-table';
import { RecipeService } from './app/services/recipe.service';

bootstrapApplication(RecipeTableComponent, {
  providers: [
    importProvidersFrom(
      HttpClientModule,
      MatPaginatorModule,
      MatTableModule,
      MatFormFieldModule,
      MatInputModule,
      MatSelectModule,
      MatIconModule,
      MatButtonModule,
      MatSidenavModule,
      MatDividerModule,
      MatProgressSpinnerModule,
      FormsModule,
      CommonModule
    )
  ]
})
.catch(err => console.error(err));