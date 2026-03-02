package com.example.library_management_api.service;

import com.example.library_management_api.models.Author;
import com.example.library_management_api.repository.AuthorRepository;
import com.example.library_management_api.request.AuthorRequest;
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
public class AuthorService {
    @Autowired
    AuthorRepository authorRepository;

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public MyApiResponse<Author> createAuthor(AuthorRequest request){
        if (isBlank(request.getAuthorName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên tác giả không được để trống");
        }
        Author author = new Author();
        author.setAuthorName(request.getAuthorName());
        author.setAddress(request.getAddress());
        author.setBirthday(request.getBirthday());
        author.setStatus(1L);
        author.setCreatedAt(LocalDate.now());
        authorRepository.save(author);
        return MyApiResponse.success("Tạo tác giả thành công", author);
    }

    public MyApiResponse<Map<String, Object>> getAllAuthor(Pageable pageable){
        Page<Author> page = authorRepository.findAllAuthors(pageable);
        List<Author> authors = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", authors);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách tác giả thành công",result);
    }

    public MyApiResponse<Map<String, Object>> getSearchAllAuthor(String keyword,Pageable pageable){
        Page<Author> page = authorRepository.searchAllAuthorsByName(keyword,pageable);
        List<Author> authors = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", authors);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách tác giả thành công",result);
    }

    public MyApiResponse<Map<String, Object>> getAllAuthorByStatus(Pageable pageable){
        Page<Author> page = authorRepository.findAllAuthorsByStatus(pageable);
        List<Author> authors = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", authors);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách tác giả thành công",result);
    }


    public MyApiResponse<Author> updateAuthor(Long authorId, AuthorRequest request){
        Author author = authorRepository.findAuthorById(authorId);
        if(author == null){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Không tìm thấy tác giả");
        }

        if (isBlank(request.getAuthorName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên tác giả không được để trống");
        }

        author.setAuthorName(request.getAuthorName());
        author.setAddress(request.getAddress());
        author.setBirthday(request.getBirthday());
        author.setUpdatedAt(LocalDate.now());
        authorRepository.save(author);
        return MyApiResponse.success("Cập nhật tác giả thành công",author);
    }

    public MyApiResponse<Author> updateStatusAuthor(Long authorId){
        Author author = authorRepository.findAuthorById(authorId);
        if(author == null){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Không tìm thấy tác giả");
        }
        Long currentStatus = author.getStatus();
        Long newStatus = (currentStatus != null && currentStatus == 1L) ? 0L : 1L;

        author.setStatus(newStatus);
        author.setUpdatedAt(LocalDate.now());
        authorRepository.save(author);
        return MyApiResponse.success("Thay đổi trạng thái thành công",author);
    }
}
