package com.example.library_management_api.controller;


import com.example.library_management_api.models.Category;
import com.example.library_management_api.request.CategoryRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    //Tạo thể loại
    @PostMapping("/create")
    public ResponseEntity<MyApiResponse<Category>> createCategory(@RequestBody CategoryRequest request) {
        MyApiResponse<Category> response = categoryService.addCategory(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    //Lấy ra tất cả thể loại
    @GetMapping("/all")
    public MyApiResponse<Map<String, Object>> getAllCategory(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return categoryService.getAllCategory(pageable);
    }

    @GetMapping("/getSearchAllCategory")
    public MyApiResponse<Map<String, Object>> getSearchAllCategory(@RequestParam String keyword,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return categoryService.getSearchAllCategory(keyword, pageable);
    }

    @GetMapping("/allByStatus")
    public MyApiResponse<Map<String, Object>> getAllCategoryByStatus(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return categoryService.getAllCategoryByStatus(pageable);
    }

    //Cập nhật thể loại
    @PutMapping("/updateCategory")
    public ResponseEntity<MyApiResponse<Category>> updateCategory(@RequestBody CategoryRequest request, @RequestParam Long categoryId) {
        MyApiResponse<Category> response = categoryService.updateCategory(categoryId, request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }


    //Thay đổi trạng thái thể loại
    @PutMapping("/changeStatusCategory")
    public MyApiResponse<Category> changeStatusCategory(Long categoryId) {
        return categoryService.updateStatusCategory(categoryId);
    }

}
