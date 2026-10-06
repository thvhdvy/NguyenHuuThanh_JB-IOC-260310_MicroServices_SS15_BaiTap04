package com.thanhnguyen.courseservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateCourseRequest {
    @NotBlank
    private String title;

    public String getTitle() {
        return title;
    }
}
