package com.example.library_management_api.controller;


import com.example.library_management_api.models.Publisher;
import com.example.library_management_api.request.PublisherRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publisher")
public class PublisherController {
    @Autowired
    PublisherService publisherService;

    //Tạo nhà xuất bản
    @PostMapping("/create")
    public ResponseEntity<MyApiResponse<Publisher>> createPublisher(@RequestBody PublisherRequest request){
        MyApiResponse<Publisher> response = publisherService.createPublisher(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    //Lấy danh sách tất cả nhà xuất bản
    @GetMapping("/getAllPublisher")
    public MyApiResponse<Map<String, Object>> getAllPublisher(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return publisherService.getAllPublishers(pageable);
    }

    @GetMapping("/getSearchAllPublisher")
    public MyApiResponse<Map<String, Object>> getSearchAllPublisher(@RequestParam String keyword,
                                                                @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return publisherService.getSearchAllPublishers(keyword,pageable);
    }

    @GetMapping("/getAllPublisherByStatus")
    public MyApiResponse<Map<String, Object>> getAllPublisherByStatus(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "12") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return publisherService.getAllPublishersByStatus(pageable);
    }

    //Cập nhật thông tin nhà xuất bản
    @PutMapping("/updatePublisher")
    public ResponseEntity<MyApiResponse<Publisher>> updatePublisher(@RequestParam Long publisherId,@RequestBody PublisherRequest request){
        MyApiResponse<Publisher> response = publisherService.updatePublisher(publisherId,request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    //Thay đổi trạng thái
    @PutMapping("/changeStatusPublisher")
    public MyApiResponse<Publisher> changeStatusPublisher(@RequestParam Long publisherId){
        return publisherService.changeStatusPublisher(publisherId);
    }

}
