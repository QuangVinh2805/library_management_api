package com.example.library_management_api.service;

import com.example.library_management_api.models.Author;
import com.example.library_management_api.models.Category;
import com.example.library_management_api.repository.CategoryRepository;
import com.example.library_management_api.request.CategoryRequest;
import com.example.library_management_api.response.MyApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }


    public MyApiResponse<Category> addCategory(CategoryRequest request) {
        if (isBlank(request.getCategoryName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống");
        }
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setStatus(1L);
        category.setCreatedAt(LocalDate.now());
        categoryRepository.save(category);
        return MyApiResponse.success("Tạo danh mục thành công", category);
    }

    public MyApiResponse<Map<String, Object>> getAllCategory(Pageable pageable){
        Page<Category> page = categoryRepository.findAllCategories(pageable);
        List<Category> category = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", category);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách danh mục thành công",result);
    }

    public MyApiResponse<Map<String, Object>> getSearchAllCategory(String keyword,Pageable pageable){
        Page<Category> page = categoryRepository.searchAllCategoriesByName(keyword,pageable);
        List<Category> authors = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", authors);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách danh mục thành công",result);
    }

    public MyApiResponse<Map<String, Object>> getAllCategoryByStatus(Pageable pageable){
        Page<Category> page = categoryRepository.findAllCategoriesByStatus(pageable);
        List<Category> category = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", category);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách danh mục thành công",result);
    }

    public MyApiResponse<Category> updateCategory(Long categoryId,CategoryRequest request){
        Category category = categoryRepository.findCategoryById(categoryId);
        if (categoryId == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Danh mục không tồn tại");
        }

        if (isBlank(request.getCategoryName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống");
        }


        category.setCategoryName(request.getCategoryName());
        category.setUpdatedAt(LocalDate.now());
        categoryRepository.save(category);
        return MyApiResponse.success("Cập nhật danh mục thành công",category);
    }

    public MyApiResponse<Category> updateStatusCategory(Long categoryId){
        Category category = categoryRepository.findCategoryById(categoryId);
        if (categoryId == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Danh mục không tồn tại");
        }
        Long currentStatus = category.getStatus();
        Long newStatus = (currentStatus != null && currentStatus == 1) ? 0L : 1L;

        category.setStatus(newStatus);
        category.setUpdatedAt(LocalDate.now());
        categoryRepository.save(category);
        return MyApiResponse.success("Cập nhật trạng thái danh mục thành công",category);
    }


}
