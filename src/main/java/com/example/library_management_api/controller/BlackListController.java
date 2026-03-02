package com.example.library_management_api.controller;


import com.example.library_management_api.request.BlackListRequest;
import com.example.library_management_api.response.BlackListResponse;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.service.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blacklist")
public class BlackListController {
    @Autowired
    BlackListService blackListService;


    @GetMapping("/getAll")
    public MyApiResponse<Map<String, Object>> getAllBlackList(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return blackListService.getAllBlackList(pageable);
    }

    @GetMapping("/getSearchAllBlackList")
    public MyApiResponse<Map<String, Object>> getSearchAllBlackList(@RequestParam String keyword,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return blackListService.getSearchAllBlackList(keyword,pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<MyApiResponse<BlackListResponse>> createBlackList(@RequestBody BlackListRequest request) {
        MyApiResponse<BlackListResponse> response =  blackListService.createBlackList(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<BlackListResponse>> updateBlackList(@RequestBody BlackListRequest request) {
        MyApiResponse<BlackListResponse> response = blackListService.updateBlackList(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MyApiResponse<String>> deleteBlackList(@RequestParam String email) {
        MyApiResponse<String> response = blackListService.deleteBlackList(email);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }
}
