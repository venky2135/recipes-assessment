package com.securin.recipes.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securin.recipes.entity.Recipe;
import com.securin.recipes.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Component
public class RecipeDataLoader {

    @Autowired
    private RecipeRepository repo;

    @PostConstruct
    public void init() {
        loadData();
    }

    private void loadData() {
        try {
            // Create JsonFactory with ALLOW_NON_NUMERIC_NUMBERS enabled
            JsonFactory jsonFactory = JsonFactory.builder()
                    .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
                    .build();

            ObjectMapper mapper = new ObjectMapper(jsonFactory);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            File file = new ClassPathResource("data/US_recipes.json").getFile();
            JsonNode root = mapper.readTree(file);

            List<Recipe> recipes = new ArrayList<>();

            Iterator<JsonNode> nodes;
            if (root.isArray()) {
                nodes = root.elements();
            } else if (root.isObject()) {
                nodes = root.elements();
            } else {
                System.out.println("No valid recipes in input!");
                return;
            }

            while (nodes.hasNext()) {
                JsonNode node = nodes.next();
                Recipe r = new Recipe();
                r.setCuisine(asTextIfExists(node, "cuisine"));
                r.setTitle(asTextIfExists(node, "title"));
                r.setRating(asDoubleIfExists(node, "rating"));
                r.setPrepTime(asIntIfExists(node, "prep_time", "prepTime"));
                r.setCookTime(asIntIfExists(node, "cook_time", "cookTime"));
                r.setTotalTime(asIntIfExists(node, "total_time", "totalTime"));
                r.setDescription(asTextIfExists(node, "description"));
                r.setServes(asTextIfExists(node, "serves"));
                JsonNode nNode = node.get("nutrients");
                if (nNode != null) {
                    r.setNutrients(nNode.isObject() ? nNode.toString() : "{}");
                } else {
                    r.setNutrients("{}");
                }
                r.setIngredients(asStringListIfExists(node, "ingredients"));
                r.setInstructions(asStringListIfExists(node, "instructions"));
                recipes.add(r);
            }

            if (repo.count() == 0 && !recipes.isEmpty()) {
                repo.saveAll(recipes);
                System.out.println("✅ Loaded " + recipes.size() + " recipes into DB.");
            } else {
                System.out.println("ℹ️ Recipes already exist in DB. Skipping load.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String asTextIfExists(JsonNode node, String... keys) {
        for (String k : keys) {
            if (node.has(k) && !node.get(k).isNull()) {
                return node.get(k).asText();
            }
        }
        return null;
    }

    private Double asDoubleIfExists(JsonNode node, String... keys) {
        for (String k : keys) {
            if (node.has(k) && node.get(k).isNumber()) {
                double val = node.get(k).asDouble();
                if (!Double.isNaN(val) && !Double.isInfinite(val)) return val;
            }
        }
        return null;
    }

    private Integer asIntIfExists(JsonNode node, String... keys) {
        for (String k : keys) {
            if (node.has(k) && node.get(k).isInt()) {
                return node.get(k).asInt();
            }
        }
        return null;
    }

    private List<String> asStringListIfExists(JsonNode node, String key) {
        List<String> result = new ArrayList<>();
        if (node.has(key) && node.get(key).isArray()) {
            for (JsonNode item : node.get(key)) {
                result.add(item.asText());
            }
        }
        return result;
    }
}
