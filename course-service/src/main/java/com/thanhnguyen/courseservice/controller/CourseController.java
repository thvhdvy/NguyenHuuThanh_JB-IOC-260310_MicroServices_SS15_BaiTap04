package com.thanhnguyen.courseservice.controller;

import com.thanhnguyen.courseservice.dto.request.CreateCourseRequest;
import com.thanhnguyen.courseservice.exception.CourseNotFoundException;
import com.thanhnguyen.courseservice.model.Course;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final List<Course> courseList;
    private final AtomicLong idGenerator = new AtomicLong(0);
    public CourseController()
    {
        this.courseList = new CopyOnWriteArrayList<>();
        this.courseList.add(new Course(idGenerator.incrementAndGet(), "Khoa so 1"));
        this.courseList.add(new Course(idGenerator.incrementAndGet(), "Khoa so 2"));
        this.courseList.add(new Course(idGenerator.incrementAndGet(), "Khoa so 3"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<List<Course>> getAllCourses()
    {
        return new ResponseEntity<>(this.courseList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<?> getCourseById(@PathVariable Long id)
    {
        Course course = this.courseList.stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst()
                .orElseThrow(() -> new CourseNotFoundException("Course Not Found!"));
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Course> addCourse(@Valid @RequestBody CreateCourseRequest request)
    {
        Course newCourse = new Course(idGenerator.incrementAndGet(), request.getTitle());
        this.courseList.add(newCourse);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }
}
