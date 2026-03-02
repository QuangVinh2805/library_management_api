package com.example.library_management_api.service;


import com.example.library_management_api.models.Notification;
import com.example.library_management_api.models.User;
import com.example.library_management_api.repository.NotificationRepository;
import com.example.library_management_api.repository.UserRepository;
import com.example.library_management_api.request.NotificationRequest;
import com.example.library_management_api.response.MyApiResponse;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JavaMailSender mailSender;

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }


    public MyApiResponse<Map<String, Object>> getAllNotifications(Pageable pageable) {
        Page<Notification> page = notificationRepository.findAllNotification(pageable);
        List<Notification> notifications = page.getContent();

        if (notifications.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không có thông báo nào cả");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", notifications);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy tất cả thông báo thành công", result);
    }


    public MyApiResponse<Notification> getNotification(Long notificationId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId);
        if (notification == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo này");
        }

        return MyApiResponse.success("Hiển thị thông báo thành công", notification);
    }

    public MyApiResponse<Notification> createNotification(
            NotificationRequest notificationRequest,
            MultipartFile image
    ) {

        if (notificationRequest == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ");
        }

        if (isBlank(notificationRequest.getTitle())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Chủ đề thông báo không được để trống");
        }

        if (isBlank(notificationRequest.getNotification())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Nội dung thông báo không được để trống");
        }

        Notification noti = new Notification();
        noti.setTitle(notificationRequest.getTitle().trim());
        noti.setNotification(notificationRequest.getNotification().trim());
        noti.setCreatedAt(LocalDate.now());

        if (image != null && !image.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/image/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File filePath = new File(dir, fileName);
                image.transferTo(filePath);

                noti.setImage("/uploads/image/" + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu ảnh", e);
            }
        }

        notificationRepository.save(noti);

        return MyApiResponse.success("Tạo thông báo thành công", noti);
    }


    public MyApiResponse<Notification> updateNotification(
            Long notificationId,
            NotificationRequest notificationRequest,
            MultipartFile image
    ) {

        if (notificationId == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Id thông báo không được null");
        }

        if (notificationRequest == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ");
        }

        if (isBlank(notificationRequest.getTitle())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Chủ đề thông báo không được để trống");
        }

        if (isBlank(notificationRequest.getNotification())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Nội dung thông báo không được để trống");
        }

        Notification noti = notificationRepository.findByNotificationId(notificationId);
        if (noti == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo này");
        }

        noti.setTitle(notificationRequest.getTitle().trim());
        noti.setNotification(notificationRequest.getNotification().trim());
        noti.setUpdatedAt(LocalDate.now());

        if (image != null && !image.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/image/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File filePath = new File(dir, fileName);
                image.transferTo(filePath);

                // Xóa ảnh cũ nếu có
                if (noti.getImage() != null) {
                    File oldFile = new File(System.getProperty("user.dir") + noti.getImage());
                    if (oldFile.exists()) oldFile.delete();
                }

                noti.setImage("/uploads/image/" + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu ảnh mới", e);
            }
        }

        notificationRepository.save(noti);

        return MyApiResponse.success("Cập nhật thông báo thành công", noti);
    }


    public MyApiResponse<String> deleteNotification(Long notificationId) {
        Notification noti = notificationRepository.findByNotificationId(notificationId);
        if (noti == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo này");
        }

        notificationRepository.delete(noti);
        return MyApiResponse.success("Xoá thông báo thành công", null);
    }

    @Async("mailExecutor")
    public void sendEmailToUser(User user, Notification noti) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("vinhdaumoi2805@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Thông báo: " + noti.getTitle());

            String html =
                    "<h2>" + noti.getTitle() + "</h2>" +
                            "<p>" + noti.getNotification() + "</p>" +
                            "<br>" +
                            "<img src='cid:notiImage' style='max-width:500px;'/>";

            helper.setText(html, true);

            if (noti.getImage() != null) {
                File file = new File(System.getProperty("user.dir") + noti.getImage());
                if (file.exists()) {
                    helper.addInline("notiImage", file);
                }
            }

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("Lỗi gửi email cho " + user.getEmail());
        }
    }


    public MyApiResponse<String> sendNotificationToAll(Long notificationId) {

        Notification noti = notificationRepository.findByNotificationId(notificationId);
        if (noti == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo để gửi");
        }

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getEmail() != null && !user.getEmail().isEmpty() && user.getStatus() == 1) {
                sendEmailToUser(user, noti);
            }
        }

        return MyApiResponse.success("Đã bắt đầu gửi email nền – không chờ", null);
    }

}
