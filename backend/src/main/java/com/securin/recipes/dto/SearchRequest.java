package com.securin.recipes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest {
    private String title;
    private String cuisine;
    private String rating;
    private String calories;

    @JsonProperty("total_time")
    private String totalTime;
}
