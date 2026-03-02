package com.example.library_management_api.controller;


import com.example.library_management_api.request.BorrowRequest;
import com.example.library_management_api.response.BorrowResponse;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.ReturnResponse;
import com.example.library_management_api.service.BorrowedProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrow")
public class BorrowedProductController {
    @Autowired
    BorrowedProductService borrowedProductService;


    @PostMapping("/product")
    public MyApiResponse<List<BorrowResponse>> createBorrowedProduct(@RequestBody BorrowRequest request) {
        return borrowedProductService.createBorrow(request);
    }

    @GetMapping("/product/all")
    public MyApiResponse<Map<String, Object>> getAllBorrowedProduct(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return borrowedProductService.getAllBorrow(pageable);
    }

    @GetMapping("/product/getSearchAll")
    public MyApiResponse<Map<String, Object>> getSearchAllBorrowedProduct(@RequestParam String keyword,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrow_date").descending());
        return borrowedProductService.getSearchAllBorrow(keyword, pageable);
    }

    @GetMapping("/product/getOne")
    public ResponseEntity<MyApiResponse<ReturnResponse>> getBorrowProduct(@RequestParam Long borrowedProductId) {
        MyApiResponse<ReturnResponse> response = borrowedProductService.getBorrowedProductById(borrowedProductId);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping("/search")
    public MyApiResponse<Map<String, Object>> searchByDateRange(
            @RequestParam String type,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return borrowedProductService.searchByDateRange(type, start, end, pageable);
    }



}
