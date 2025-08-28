package com.securin.recipes.service;

import com.securin.recipes.dto.PageResponse;
import com.securin.recipes.dto.RecipeDto;
import com.securin.recipes.entity.Recipe;
import com.securin.recipes.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Path;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository repo;

    // Paginated listing
    public PageResponse<RecipeDto> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        Page<Recipe> recipePage = repo.findAll(pageable);
        List<RecipeDto> dtos = recipePage.map(RecipeDto::fromEntity).toList();
        return new PageResponse<>(dtos, recipePage.getTotalElements(), recipePage.getTotalPages(), page, size);
    }

    // Search with multiple filters using Specification
    public List<RecipeDto> search(Map<String, String> params) {
        Specification<Recipe> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Title filter (partial match)
            if (params.containsKey("title")) {
                String title = params.get("title");
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            // Cuisine filter (exact match)
            if (params.containsKey("cuisine")) {
                String cuisine = params.get("cuisine");
                predicates.add(cb.equal(root.get("cuisine"), cuisine));
            }
            
            // Numeric filters for rating, total_time, and calories
            if (params.containsKey("rating")) {
                addNumericFilter(cb, predicates, root.get("rating"), params.get("rating"));
            }
            if (params.containsKey("total_time")) {
                addNumericFilter(cb, predicates, root.get("total_time"), params.get("total_time"));
            }
            if (params.containsKey("calories")) {
                addCaloriesFilter(cb, predicates, root, params.get("calories"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Return the first 500 results for the search endpoint, as per the frontend logic
        return repo.findAll(spec, PageRequest.of(0, 500)).getContent().stream()
                .map(RecipeDto::fromEntity)
                .toList();
    }
    
    // Generic method to handle numeric filters
    private void addNumericFilter(CriteriaBuilder cb,
                                  List<Predicate> predicates,
                                  Path<Number> path,
                                  String filterStr) {
        filterStr = filterStr.trim();
        try {
            Expression<Double> doublePath = path.as(Double.class);
            if (filterStr.startsWith(">=")) {
                Double val = Double.valueOf(filterStr.substring(2).trim());
                predicates.add(cb.greaterThanOrEqualTo(doublePath, val));
            } else if (filterStr.startsWith("<=")) {
                Double val = Double.valueOf(filterStr.substring(2).trim());
                predicates.add(cb.lessThanOrEqualTo(doublePath, val));
            } else if (filterStr.startsWith("=")) {
                Double val = Double.valueOf(filterStr.substring(1).trim());
                predicates.add(cb.equal(doublePath, val));
            } else if (filterStr.startsWith(">")) {
                Double val = Double.valueOf(filterStr.substring(1).trim());
                predicates.add(cb.greaterThan(doublePath, val));
            } else if (filterStr.startsWith("<")) {
                Double val = Double.valueOf(filterStr.substring(1).trim());
                predicates.add(cb.lessThan(doublePath, val));
            } else {
                Double val = Double.valueOf(filterStr);
                predicates.add(cb.equal(doublePath, val));
            }
        } catch (NumberFormatException e) {
            // Ignore invalid numbers
        }
    }

    // Method to specifically handle the JSONB calories filter
    private void addCaloriesFilter(CriteriaBuilder cb,
            List<Predicate> predicates,
            Root<Recipe> root,
            String filterStr) {
filterStr = filterStr.trim();
try {
String op;
double val;

if (filterStr.startsWith(">=")) {
op = ">=";
val = Double.parseDouble(filterStr.substring(2).trim());
} else if (filterStr.startsWith("<=")) {
op = "<=";
val = Double.parseDouble(filterStr.substring(2).trim());
} else if (filterStr.startsWith(">")) {
op = ">";
val = Double.parseDouble(filterStr.substring(1).trim());
} else if (filterStr.startsWith("<")) {
op = "<";
val = Double.parseDouble(filterStr.substring(1).trim());
} else if (filterStr.startsWith("=")) {
op = "=";
val = Double.parseDouble(filterStr.substring(1).trim());
} else {
op = "=";
val = Double.parseDouble(filterStr);
}

// Extract calories from JSON, unquote, then cast to double
Expression<Double> caloriesExpression = cb.function(
"CAST",
Double.class,
cb.function(
"JSON_UNQUOTE",
String.class,
cb.function("JSON_EXTRACT", String.class, root.get("nutrients"), cb.literal("$.calories"))
),
cb.literal("AS DECIMAL")
);

switch (op) {
case ">=" -> predicates.add(cb.greaterThanOrEqualTo(caloriesExpression, val));
case "<=" -> predicates.add(cb.lessThanOrEqualTo(caloriesExpression, val));
case "=" -> predicates.add(cb.equal(caloriesExpression, val));
case ">" -> predicates.add(cb.greaterThan(caloriesExpression, val));
case "<" -> predicates.add(cb.lessThan(caloriesExpression, val));
}
} catch (NumberFormatException e) {
// Ignore invalid numbers
}
}}