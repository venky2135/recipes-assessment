package com.securin.recipes.controller;

import com.securin.recipes.dto.PageResponse;
import com.securin.recipes.dto.RecipeDto;
import com.securin.recipes.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService service;

    // Paginated list
    @GetMapping
    public PageResponse<RecipeDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.list(page, size);
    }

    // Search
    @GetMapping("/search")
    public List<RecipeDto> search(
            @RequestParam Map<String, String> params) {
        // You'll need to update your service.search method to accept a Map
        // and handle the parsing logic there.
        return service.search(params);
    }
}