package com.example.library_management_api.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
public class BorrowedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quantity;
    private Long productId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private Long readerId;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long status;
    private Long isLost;
}
