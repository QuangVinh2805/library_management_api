package com.example.library_management_api.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String image;
    private Long quantity;
    private Long publisherId;
    // Năm xuất bản
    private String publicationDate;
    private Long status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String hashId;
    private String description;
    private Long price;
    private Long isNew;
    private String location;
    private String authorName;
    private String categoryName;

}
