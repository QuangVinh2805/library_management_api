package com.example.library_management_api.controller;


import com.example.library_management_api.models.Banner;
import com.example.library_management_api.models.Notification;
import com.example.library_management_api.request.NotificationRequest;
import com.example.library_management_api.request.ProductRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping("/getAll")
    public MyApiResponse<Map<String, Object>> getAllNotification(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return notificationService.getAllNotifications(pageable);
    }

    @GetMapping("/getOne")
    public MyApiResponse<Notification> getNotification(Long notificationId){
        return notificationService.getNotification(notificationId);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Notification>> createNotification(@ModelAttribute NotificationRequest req,
                                                          @RequestPart(value = "image", required = false) MultipartFile image){
        MyApiResponse<Notification> response = notificationService.createNotification(req,image);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Notification>> updateNotification(@RequestParam Long notificationId,
                                                          @ModelAttribute NotificationRequest req,
                                                          @RequestPart(value = "image", required = false) MultipartFile image){
        MyApiResponse<Notification> response = notificationService.updateNotification(notificationId,req,image);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @DeleteMapping("/delete")
    public MyApiResponse<String> deleteNotification(@RequestParam Long notificationId){
        return notificationService.deleteNotification(notificationId);
    }

    @PostMapping("/sendAll")
    public MyApiResponse<String> sendAllNotifications(@RequestParam Long notificationId) {
        return notificationService.sendNotificationToAll(notificationId);
    }

}
