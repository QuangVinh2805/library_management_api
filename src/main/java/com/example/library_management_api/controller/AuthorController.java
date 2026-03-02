package com.example.library_management_api.controller;


import com.example.library_management_api.models.Author;
import com.example.library_management_api.request.AuthorRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/author")
public class AuthorController {
    @Autowired
    AuthorService authorService;

    //Tạo tác giả
    @PostMapping("/create")
    public ResponseEntity<MyApiResponse<Author>> createAuthor(@RequestBody AuthorRequest request) {
        MyApiResponse<Author> response = authorService.createAuthor(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    //Lấy danh sách tác giả
    @GetMapping("/getAllAuthor")
    public MyApiResponse<Map<String, Object>> getAllAuthor(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return authorService.getAllAuthor(pageable);
    }

    //Tìm kiếm tác giả
    @GetMapping("/getSearchAllAuthor")
    public MyApiResponse<Map<String, Object>> getSearchAllAuthor(@RequestParam String keyword,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return authorService.getSearchAllAuthor(keyword, pageable);
    }

    //Lấy tất cả tác giả đang active
    @GetMapping("/getAllAuthorByStatus")
    public MyApiResponse<Map<String, Object>> getAllAuthorByStatus(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return authorService.getAllAuthorByStatus(pageable);
    }

    //Cập nhật thông tin tác giả
    @PutMapping("/updateAuthor")
    public ResponseEntity<MyApiResponse<Author>> updateAuthor(@RequestBody AuthorRequest request, @RequestParam Long authorId) {
        MyApiResponse<Author> response = authorService.updateAuthor(authorId, request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }


    //Thay đổi trạng thái
    @PutMapping("/changeStatusAuthor")
    public MyApiResponse<Author> changeStatusAuthor(@RequestParam Long authorId) {
        return authorService.updateStatusAuthor(authorId);
    }
}
