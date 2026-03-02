package com.example.library_management_api.service;


import com.example.library_management_api.models.Publisher;
import com.example.library_management_api.repository.PublisherRepository;
import com.example.library_management_api.request.PublisherRequest;
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
public class PublisherService {
    @Autowired
    PublisherRepository publisherRepository;

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public MyApiResponse<Publisher> createPublisher(PublisherRequest request){
        if (isBlank(request.getPublisherName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên nhà xuất bản không được để trống");
        }

        if (!isBlank(request.getEmail())) {
            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
            }
        }


        Publisher publisher = new Publisher();
        publisher.setPublisherName(request.getPublisherName());
        publisher.setEmail(request.getEmail());
        publisher.setPhone(request.getPhone());
        publisher.setStatus(1L);
        publisher.setCreatedAt(LocalDate.now());
        publisherRepository.save(publisher);
        return MyApiResponse.success("Tạo nhà xuất bản thành công", publisher);
    }


    public MyApiResponse<Map<String, Object>> getAllPublishers(Pageable pageable){
        Page<Publisher> page = publisherRepository.findAllPublishers(pageable);
        List<Publisher> publisher = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", publisher);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách nhà xuất bản thành công", result);
    }
    public MyApiResponse<Map<String, Object>> getSearchAllPublishers(String keyword, Pageable pageable){
        Page<Publisher> page = publisherRepository.searchAllPublishersByName(keyword,pageable);
        List<Publisher> publisher = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", publisher);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách nhà xuất bản thành công", result);
    }

    public MyApiResponse<Map<String, Object>> getAllPublishersByStatus(Pageable pageable){
        Page<Publisher> page = publisherRepository.findAllPublishersByStatus(pageable);
        List<Publisher> publisher = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", publisher);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách nhà xuất bản thành công", result);
    }


    public MyApiResponse<Publisher> updatePublisher(Long publisherId, PublisherRequest request){
        Publisher publisher = publisherRepository.findPublisherById(publisherId);
        if(publisherId == null){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Nhà xuất bản không tồn tại");
        }

        if (isBlank(request.getPublisherName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên nhà xuất bản không được để trống");
        }

        if (!isBlank(request.getEmail())) {
            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
            }
        }

        publisher.setPublisherName(request.getPublisherName());
        publisher.setEmail(request.getEmail());
        publisher.setPhone(request.getPhone());
        publisher.setUpdatedAt(LocalDate.now());
        publisherRepository.save(publisher);
        return MyApiResponse.success("Cập nhật thông tin nhà xuất bản thành công", publisher);

    }

    public MyApiResponse<Publisher> changeStatusPublisher(Long publisherId){
        Publisher publisher = publisherRepository.findPublisherById(publisherId);
        if(publisherId == null){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Nhà xuất bản không tồn tại");
        }
        Long currentStatus = publisher.getStatus();
        Long newStatus = (currentStatus != null && currentStatus == 1L) ? 0L : 1L;
        publisher.setStatus(newStatus);
        publisherRepository.save(publisher);
        return MyApiResponse.success("Thay đổi trạng thái thành công", publisher);
    }
}
