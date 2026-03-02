package com.example.library_management_api.service;

import com.example.library_management_api.models.*;
import com.example.library_management_api.repository.*;
import com.example.library_management_api.request.FavouriteProductRequest;
import com.example.library_management_api.request.ProductRequest;
import com.example.library_management_api.request.ProductReservationRequest;
import com.example.library_management_api.response.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.temporal.ChronoUnit;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductAuthorRepository productAuthorRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PublisherRepository publisherRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    FavouriteProductRepository favouriteProductRepository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    ProductReservationRepository productReservationRepository;

    @Autowired
    BorrowedProductRepository borrowedProductRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    ReaderRepository readerRepository;

    @Autowired
    BorrowedProductRepository borrowProductRepository;

    @Autowired
    BorrowedFeeRepository borrowedFeeRepository;


    private String generateHashId() {
        return UUID.randomUUID().toString();
    }
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }



    public ResponseEntity<MyApiResponse<ProductResponse>> createProduct(ProductRequest request, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(MyApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Vui lòng chọn ảnh"
                    ));
        }

        if (isBlank(request.getProductName())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(MyApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Vui lòng nhập tên sách"
                    ));
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(MyApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Số lượng phải > 0"
                    ));
        }

        if (isBlank(request.getPublisherName())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(MyApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Vui lòng nhập tên nhà xuất bản"
                    ));
        }

        if (request.getPrice() == null || request.getPrice() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(MyApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Giá phải lớn hơn 0"
                    ));
        }

        if (isBlank(request.getLocation())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(MyApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Vui lòng nhập vị trí sách"
                    ));
        }

        if (isBlank(request.getCategoryName())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(MyApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Vui lòng nhập danh mục sách"
                    ));
        }

        if (isBlank(request.getAuthorName())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(MyApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Vui lòng nhập tên tác giả"
                    ));
        }


        Publisher publisher = publisherRepository.findByPublisherName(request.getPublisherName());
        if (publisher == null) {
            publisher = new Publisher();
            publisher.setPublisherName(request.getPublisherName());
            publisher.setStatus(1L);
            publisher.setCreatedAt(LocalDate.now());
            publisherRepository.save(publisher);
        }


        String[] categoryNames = request.getCategoryName().split(","); //split tách list categoryNames đc gửi lên bằng dấu ,
        List<Category> categories = new ArrayList<>();//Khởi tạo 1 list categories

        for (String catName : categoryNames) { //duyệt qua từng categoryNames
            String trimmedName = catName.trim(); // Loại bỏ khoảng trống đầu và cuối trong ""
            if (trimmedName.isEmpty()) continue; // Nếu không có khoảng trắng thì chạy tiếp

            Category category = categoryRepository.findByCategoryName(trimmedName);
            if (category == null) {
                category = new Category();
                category.setCategoryName(trimmedName);
                category.setStatus(1L);
                category.setCreatedAt(LocalDate.now());
                categoryRepository.save(category);
            }
            categories.add(category);
        }

        String[] authorNames = request.getAuthorName().split(",");
        List<Author> authors = new ArrayList<>();

        for (String auName : authorNames) {
            String trimmedName = auName.trim();
            if (trimmedName.isEmpty()) continue;

            Author author = authorRepository.findByAuthorName(trimmedName);
            if (author == null) {
                author = new Author();
                author.setAuthorName(trimmedName);
                author.setStatus(1L);
                author.setCreatedAt(LocalDate.now());
                authorRepository.save(author);
            }

            authors.add(author);
        }


        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setQuantity(request.getQuantity());
        product.setPublisherId(publisher.getId());
        product.setPublicationDate(request.getPublicationDate());
        product.setStatus(1L);
        product.setHashId(generateHashId());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setIsNew(1L);
        product.setLocation(request.getLocation());
        product.setCreatedAt(LocalDate.now());
        product.setAuthorName(
                authors.stream()
                        .map(Author::getAuthorName)
                        .collect(Collectors.joining(", "))
        );

        product.setCategoryName(
                categories.stream()
                        .map(Category::getCategoryName)
                        .collect(Collectors.joining(", "))
        );


        if (image != null && !image.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/image/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File filePath = new File(dir, fileName);
                image.transferTo(filePath);

                // Nếu  đã có anh cũ → có thể xoá nếu muốn
                if (product.getImage() != null) {
                    File oldFile = new File(System.getProperty("user.dir") + product.getImage());
                    if (oldFile.exists()) oldFile.delete();
                }

                product.setImage("/uploads/image/" + fileName);

            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu ảnh", e);
            }
        }

        productRepository.save(product);

        for (Author author : authors) {
            ProductAuthor productAuthor = new ProductAuthor();
            productAuthor.setAuthorId(author.getId());
            productAuthor.setProductId(product.getId());
            productAuthor.setCreatedAt(LocalDate.now());
            productAuthorRepository.save(productAuthor);
        }

        for (Category category : categories) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setCategoryId(category.getId());
            productCategory.setProductId(product.getId());
            productCategory.setCreatedAt(LocalDate.now());
            productCategoryRepository.save(productCategory);
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductName(product.getProductName());
        productResponse.setQuantity(product.getQuantity());
        productResponse.setDescription(product.getDescription());
        productResponse.setStatus(product.getStatus());
        productResponse.setPublicationDate(product.getPublicationDate());
        productResponse.setHashId(product.getHashId());
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setPublisherName(publisher.getPublisherName());
        productResponse.setCategoryName(
                categories.stream()
                        .map(Category::getCategoryName)//Lấy ra các category
                        .collect(Collectors.joining(", ")) // nối chúng bằng dấu , để trả về response
        );
        productResponse.setAuthorName(
                authors.stream()
                        .map(Author::getAuthorName)
                        .collect(Collectors.joining(", "))
        );
        productResponse.setImage(product.getImage());
        productResponse.setPrice(product.getPrice());
        productResponse.setIsNew(product.getIsNew());
        productResponse.setLocation(product.getLocation());

        return ResponseEntity.status(HttpStatus.CREATED).body(MyApiResponse.success("Tạo sách thành công", productResponse));

    }

    @Transactional
    public MyApiResponse<Map<String, Object>> getAllProduct(Pageable pageable) {
        productRepository.updateExpiredNewProducts();
        Page<Product> page = productRepository.findAllProducts(pageable);
        List<Product> products = page.getContent();

        List<ProductResponse> responses = products.stream().map(product -> {
            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();
            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setPublisherName(publisher.getPublisherName());
            productResponse.setCategoryName(product.getCategoryName());
            productResponse.setAuthorName(product.getAuthorName());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            return productResponse;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách sách thành công", result);
    }

    public MyApiResponse<Map<String, Object>> getAllProductByStatus(Pageable pageable) {
        Page<Product> page = productRepository.findAllProductsByStatus(pageable);
        List<Product> products = page.getContent();

        List<ProductResponse> responses = products.stream().map(product -> {
            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());
            List<Category> categories = categoryRepository.findCategoriesByProductId(product.getId());

            ProductResponse productResponse = new ProductResponse();
            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setPublisherName(publisher.getPublisherName());
            productResponse.setCategoryName(product.getCategoryName());
            productResponse.setAuthorName(product.getAuthorName());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            return productResponse;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách sách thành công", result);
    }



    public MyApiResponse<ProductResponse> getProduct(String hashId) {
        Product product = productRepository.findByHashId(hashId);

        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách.");
        }

        // Lấy thông tin publisher, category, author
        Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());
        List<Author> authors = authorRepository.findAuthorsByProductId(product.getId());
        List<Category> categories = categoryRepository.findCategoriesByProductId(product.getId());

        // Tạo response
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductName(product.getProductName());
        productResponse.setQuantity(product.getQuantity());
        productResponse.setDescription(product.getDescription());
        productResponse.setStatus(product.getStatus());
        productResponse.setPublicationDate(product.getPublicationDate());
        productResponse.setHashId(product.getHashId());
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setImage(product.getImage());
        productResponse.setPrice(product.getPrice());
        productResponse.setIsNew(product.getIsNew());
        productResponse.setLocation(product.getLocation());


        // Gán tên từ các quan hệ
        productResponse.setPublisherName(
                publisher != null ? publisher.getPublisherName() : null //Nếu publisher khác null thì lấy ra publisherName,không thì lây ra null
        );
        productResponse.setCategoryName(product.getCategoryName());
        productResponse.setAuthorName(product.getAuthorName());
        productResponse.setUpdatedAt(product.getUpdatedAt());

        return MyApiResponse.success("Lấy sách thành công", productResponse);
    }


    @Transactional
    public MyApiResponse<ProductResponse> updateProduct(String hashId, ProductRequest request, MultipartFile image) {
        // Tìm product
        Product product = productRepository.findByHashId(hashId);
        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        // --- VALIDATE ---
        if (isBlank(request.getProductName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên sách");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số lượng phải > 0");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Giá phải > 0");
        }
        if (isBlank(request.getPublisherName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên nhà xuất bản");
        }
        if (isBlank(request.getLocation())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Vui lòng nhập vị trí sách");
        }
        if (isBlank(request.getCategoryName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Vui lòng nhập danh mục sách");
        }
        if (isBlank(request.getAuthorName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên tác giả");
        }

        // --- Xử lý publisher ---
        Publisher publisher = publisherRepository.findByPublisherName(request.getPublisherName());
        if (publisher == null) {
            publisher = new Publisher();
            publisher.setPublisherName(request.getPublisherName());
            publisher.setStatus(1L);
            publisher.setCreatedAt(LocalDate.now());
            publisherRepository.save(publisher);
        }

        // --- Xử lý category ---
        String[] categoryNames = request.getCategoryName().split(",");
        List<Category> categories = new ArrayList<>();
        for (String catName : categoryNames) {
            String trimmedName = catName.trim();
            if (trimmedName.isEmpty()) continue;
            Category category = categoryRepository.findByCategoryName(trimmedName);
            if (category == null) {
                category = new Category();
                category.setCategoryName(trimmedName);
                category.setStatus(1L);
                category.setCreatedAt(LocalDate.now());
                categoryRepository.save(category);
            }
            categories.add(category);
        }

        // --- Xử lý author ---
        String[] authorNames = request.getAuthorName().split(",");
        List<Author> authors = new ArrayList<>();
        for (String auName : authorNames) {
            String trimmedName = auName.trim();
            if (trimmedName.isEmpty()) continue;
            Author author = authorRepository.findByAuthorName(trimmedName);
            if (author == null) {
                author = new Author();
                author.setAuthorName(trimmedName);
                author.setStatus(1L);
                author.setCreatedAt(LocalDate.now());
                authorRepository.save(author);
            }
            authors.add(author);
        }

        // --- Cập nhật product ---
        product.setProductName(request.getProductName());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());
        product.setPublisherId(publisher.getId());
        product.setPublicationDate(request.getPublicationDate());
        product.setDescription(request.getDescription());
        product.setLocation(request.getLocation());
        product.setUpdatedAt(LocalDate.now());

        // Lưu luôn author_name trong product
        product.setAuthorName(
                authors.stream().map(Author::getAuthorName).collect(Collectors.joining(", "))
        );

        product.setCategoryName(
                categories.stream().map(Category::getCategoryName).collect(Collectors.joining(", "))
        );

        // --- Cập nhật ảnh nếu có ---
        if (image != null && !image.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/image/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                File filePath = new File(dir, fileName);
                image.transferTo(filePath);

                // Xóa ảnh cũ nếu có
                if (product.getImage() != null) {
                    File oldFile = new File(System.getProperty("user.dir") + product.getImage());
                    if (oldFile.exists()) oldFile.delete();
                }

                product.setImage("/uploads/image/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu ảnh mới", e);
            }
        }

        productRepository.save(product);

        // --- Xóa quan hệ cũ và tạo mới ---
        productAuthorRepository.deleteByProductId(product.getId());
        productCategoryRepository.deleteByProductId(product.getId());

        for (Author author : authors) {
            ProductAuthor pa = new ProductAuthor();
            pa.setAuthorId(author.getId());
            pa.setProductId(product.getId());
            pa.setCreatedAt(LocalDate.now());
            productAuthorRepository.save(pa);
        }

        for (Category category : categories) {
            ProductCategory pc = new ProductCategory();
            pc.setCategoryId(category.getId());
            pc.setProductId(product.getId());
            pc.setCreatedAt(LocalDate.now());
            productCategoryRepository.save(pc);
        }

        // --- Build response ---
        ProductResponse response = new ProductResponse();
        response.setProductName(product.getProductName());
        response.setQuantity(product.getQuantity());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        response.setPublisherName(publisher.getPublisherName());
        response.setCategoryName(
                product.getCategoryName()
        );
        response.setAuthorName(product.getAuthorName()); // từ field mới
        response.setLocation(product.getLocation());
        response.setImage(product.getImage());
        response.setHashId(product.getHashId());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setStatus(product.getStatus());

        return MyApiResponse.success("Cập nhật sách thành công", response);
    }



    public MyApiResponse<ProductStatusResponse> changeStatusProduct(String hashId) {
        Product product = productRepository.findByHashId(hashId);
        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }


        Long currentStatus = product.getStatus();
        Long newStatus = (currentStatus != null && currentStatus == 1L) ? 0L : 1L;

        product.setStatus(newStatus);
        product.setUpdatedAt(LocalDate.now());
        productRepository.save(product);

        ProductStatusResponse productStatusResponse = new ProductStatusResponse();
        productStatusResponse.setProductName(product.getProductName());
        productStatusResponse.setStatus(product.getStatus());
        productStatusResponse.setUpdatedAt(product.getUpdatedAt());

        return MyApiResponse.success("Thay đổi trạng thái thành công", productStatusResponse);
    }


    public MyApiResponse<ProductIsNewResponse> changeIsNewProduct(String hashId) {
        Product product = productRepository.findByHashId(hashId);
        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        Long currentIsNew = product.getIsNew();
        Long newIsNew = (currentIsNew != null && currentIsNew == 1L) ? 0L : 1L;

        product.setIsNew(newIsNew);
        product.setUpdatedAt(LocalDate.now());
        productRepository.save(product);

        ProductIsNewResponse productIsNewResponse = new ProductIsNewResponse();
        productIsNewResponse.setProductName(product.getProductName());
        productIsNewResponse.setIsNew(product.getIsNew());
        productIsNewResponse.setUpdatedAt(product.getUpdatedAt());

        return MyApiResponse.success("Thay đổi trạng thái thành công", productIsNewResponse);
    }


    @Transactional
    public MyApiResponse<List<ProductResponse>> getNewProduct( Pageable pageable) {
        productRepository.updateExpiredNewProducts();
        Page<Product> page = productRepository.findNewProduct(pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách mới nào cả");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());


            ProductResponse productResponse = new ProductResponse();
            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setPublisherName(publisher.getPublisherName());
            productResponse.setCategoryName(product.getCategoryName());
            productResponse.setAuthorName(product.getAuthorName());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());
            productResponse.setUpdatedAt(product.getUpdatedAt());

            return productResponse;
        }).collect(Collectors.toList());

        return MyApiResponse.success("Lấy danh sách sách thành công", responses);


    }

    public MyApiResponse<Map<String, Object>> getProductByPublisherId(Long publisherId, Pageable pageable) {
        Page<Product> page = productRepository.findByPublisherId(publisherId, pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách ");
        }

        List<ProductResponse> responses = products.stream().map(product -> {
            //Lấy ra publisher theo id
            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());


            ProductResponse productResponse = new ProductResponse();
            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setPublisherName(publisher.getPublisherName());
            productResponse.setCategoryName(product.getCategoryName());

            productResponse.setAuthorName(product.getAuthorName());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());
            productResponse.setUpdatedAt(product.getUpdatedAt());

            return productResponse;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách sách thành công", result);

    }


    public MyApiResponse<List<ProductResponse>> getProductCarouselByCategoryId(Long categoryId, Pageable pageable) {

        Page<Product> page = productRepository.findProductsByCategoryId(categoryId, pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());


            productResponse.setAuthorName(product.getAuthorName());

            return productResponse;

        }).collect(Collectors.toList());

        return MyApiResponse.success("Lấy danh sách sách thành công", responses);
    }


    public MyApiResponse<Map<String, Object>> getProductByAuthorId(Long authorId, Pageable pageable) {

        Page<Product> page = productRepository.findProductsByAuthorId(authorId, pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());

            productResponse.setAuthorName(product.getAuthorName());


            return productResponse;

        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách sách thành công", result);
    }



    public MyApiResponse<Map<String, Object>> getProductByCategoryId(Long categoryId, Pageable pageable) {

        Page<Product> page = productRepository.findProductsByCategoryId(categoryId, pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());


            productResponse.setAuthorName(product.getAuthorName());

            return productResponse;

        }).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách sách thành công", result);
    }




    public MyApiResponse<String> getProductCountByCategory(Long categoryId) {
        if (categoryId == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục");
        }

        int count = productCategoryRepository.countProductsByCategoryId(categoryId);
        return MyApiResponse.success("Số lượng sách: " + count, String.valueOf(count));
    }


    public MyApiResponse<String> getProductCountByAuthor(Long authorId) {
        if (authorId == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy tác giả");
        }

        int count = productAuthorRepository.countProductsByAuthorId(authorId);
        return MyApiResponse.success("Số lượng sách: " + count, String.valueOf(count));
    }


    public MyApiResponse<String> getProductCountByPublisher(Long publisherId) {
        if (publisherId == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy nhà xuất bản");
        }

        Long count = productRepository.countBooksByPublisherId(publisherId);
        return MyApiResponse.success("Số lượng sách: " + count, String.valueOf(count));

    }


    public MyApiResponse<List<ProductResponse>> getSearchProductByStatus(String keyword, Pageable pageable) {
        Page<Product> page = productRepository.searchProductsByNameAndStatus(keyword, pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());


            productResponse.setAuthorName(product.getAuthorName());


            return productResponse;

        }).collect(Collectors.toList());

        return MyApiResponse.success("Lấy danh sách sách thành công", responses);
    }


    public MyApiResponse<Map<String, Object>> getSearchAllProductByStatus(String keyword, Pageable pageable) {
        Page<Product> page = productRepository.searchProductsByNameAndStatus(keyword, pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());

            productResponse.setAuthorName(product.getAuthorName());


            return productResponse;

        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách sách thành công", result);
    }

    public MyApiResponse<Map<String, Object>> getSearchAllProduct(String keyword, Pageable pageable) {
        Page<Product> page = productRepository.searchAllProductsByName(keyword, pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());


            productResponse.setAuthorName(product.getAuthorName());


            return productResponse;

        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách sách thành công", result);
    }


    @Transactional
    public MyApiResponse<FavouriteProductResponse> createFavourite(FavouriteProductRequest request) {

        Reader user = readerRepository.findByToken(request.getToken());
        Product product = productRepository.findByHashId(request.getHashId());

        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy độc giả");
        }

        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        FavouriteProduct favourite = favouriteProductRepository.findFavourite(user.getId(), product.getId());

        if (favourite != null) {
            favourite.setStatus(1L);
            favourite.setUpdatedAt(LocalDate.now());
            favouriteProductRepository.save(favourite);
        } else {
            favourite = new FavouriteProduct();
            favourite.setReaderId(user.getId());
            favourite.setProductId(product.getId());
            favourite.setStatus(1L);
            favourite.setCreatedAt(LocalDate.now());
            favouriteProductRepository.save(favourite);
        }
        FavouriteProductResponse response = new FavouriteProductResponse();
        response.setId(favourite.getId());
        response.setHashId(product.getHashId());
        response.setToken(user.getToken());
        response.setProductName(product.getProductName());
        response.setName(user.getName());
        response.setImage(product.getImage());
        response.setStatus(1L);

        return MyApiResponse.success("Yêu thích thành công", response);
    }

    public MyApiResponse<Long> countFavourite(String hashId) {
        Product product = productRepository.findByHashId(hashId);
        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }
        Long count = favouriteProductRepository.countByProductId(product.getId());
        return MyApiResponse.success("Lấy số lượt thích của sách thành công:", count);
    }

    public MyApiResponse<FavouriteProductResponse> updateStatusFavouriteProduct(Long favouriteProductId) {
        FavouriteProduct favouriteProduct = favouriteProductRepository.findFavouriteProductById(favouriteProductId);
        if (favouriteProduct == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách được yêu thích");
        }
        Reader user = readerRepository.findByReaderId(favouriteProduct.getReaderId());
        Product product = productRepository.findByProductId(favouriteProduct.getProductId());

        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy độc giả");
        }

        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        Long currentStatus = favouriteProduct.getStatus();
        Long newStatus = (currentStatus != null && currentStatus == 1L) ? 0L : 1L;
        favouriteProduct.setStatus(newStatus);
        favouriteProductRepository.save(favouriteProduct);

        FavouriteProductResponse response = new FavouriteProductResponse();
        response.setId(favouriteProductId);
        response.setHashId(product.getHashId());
        response.setToken(user.getToken());
        response.setProductName(product.getProductName());
        response.setName(user.getName());
        response.setImage(product.getImage());
        response.setStatus(favouriteProduct.getStatus());

        return MyApiResponse.success("Câp nhật thành công", response);
    }


    public MyApiResponse<Map<String, Object>> getFavouriteProductByUser(String token, Pageable pageable) {
        Reader user = readerRepository.findByToken(token);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "");
        }
        Page<Product> page = productRepository.findFavouriteProductsByUserToken(token, pageable);
        List<Product> products = page.getContent();
        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());


            productResponse.setAuthorName(product.getAuthorName());


            return productResponse;

        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách sách thành công", result);
    }


    public MyApiResponse<UserFavouriteProductResponse> checkUserFavourite(String token, String hashId) {
        Product product = productRepository.findByHashId(hashId);
        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        UserFavouriteProductResponse response = new UserFavouriteProductResponse();
        response.setFavourite(false);
        response.setId(null);

        if (token == null || token.isEmpty()) {
            return MyApiResponse.success("Người dùng chưa đăng nhập", response);
        }

        Reader user = readerRepository.findByToken(token);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy độc giả");
        }

        FavouriteProduct favourite = favouriteProductRepository.findActiveByUserAndProduct(user.getId(), product.getId());
        if (favourite != null) {
            response.setFavourite(true);
            response.setId(favourite.getId());
        }

        return MyApiResponse.success("Kiểm tra trạng thái yêu thích thành công", response);
    }


    public MyApiResponse<ProductReservationResponse> createProductReservation(ProductReservationRequest request) {

        Reader user = readerRepository.findByToken(request.getToken());
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy độc giả");
        }

        if (isReaderBlockedByOverdue(user.getId())) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Bạn có sách quá hạn chưa trả quá 7 ngày – không thể đăng ký mượn thêm"
            );
        }

        if (isReaderBorrowingLimit(user.getId())) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Bạn đang mượn tối đa 3 sách – không thể đăng ký thêm"
            );
        }

        if (borrowedProductRepository.countPendingReservation(user.getId()) >= 3) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Bạn đã đăng ký tối đa 3 đơn mượn sách – không thể đăng ký thêm"
            );
        }

        Product product = productRepository.findByHashId(request.getHashId());
        if (product == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        LocalDate today = LocalDate.now();

        if (request.getBorrowDay() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ngày mượn không được để trống");
        }

        if (request.getBorrowDay().isBefore(today)) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ngày mượn không được là ngày trong quá khứ");
        }

        if (request.getQuantity() > product.getQuantity()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST,
                    "Số lượng sách mượn vượt quá số lượng còn lại. Hiện chỉ còn " + product.getQuantity());
        }

        // 🔥 TẠO borrowed_product với status = 1 (đã gửi yêu cầu)
        BorrowedProduct bp = new BorrowedProduct();
        bp.setReaderId(user.getId());
        bp.setProductId(product.getId());
        bp.setQuantity(request.getQuantity());
        bp.setBorrowDate(request.getBorrowDay());
        bp.setDueDate(request.getDueDay());
        bp.setStatus(1L); // đã gửi yêu cầu
        bp.setIsLost(0L);
        bp.setCreatedAt(today);

        borrowedProductRepository.save(bp);

        // ❗ CHƯA trừ số lượng sách

        ProductReservationResponse res = new ProductReservationResponse();
        res.setId(bp.getId());
        res.setName(user.getName());
        res.setProductName(product.getProductName());
        res.setEmail(user.getEmail());
        res.setImage(product.getImage());
        res.setQuantity(bp.getQuantity());
        res.setStatus(bp.getStatus());
        res.setBorrowDay(bp.getBorrowDate());
        res.setDueDay(bp.getDueDate());
        res.setCreatedAt(bp.getCreatedAt());
        res.setUpdatedAt(bp.getUpdatedAt());

        return MyApiResponse.success("Tạo yêu cầu mượn sách thành công", res);
    }

    private boolean isReaderBlockedByOverdue(Long readerId) {
        return borrowedProductRepository.countOverdueMoreThan7Days(readerId) > 0;
    }

    private boolean isReaderBorrowingLimit(Long readerId) {
        return borrowedProductRepository.countBorrowingBooks(readerId) >= 3;
    }

    public MyApiResponse<Map<String, Object>> getAllProductReservationByUser(String token, Pageable pageable) {

        Reader user = readerRepository.findByToken(token);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy độc giả");
        }

        Page<BorrowedProduct> page =
                borrowedProductRepository.findAllByReaderId(user.getId(), pageable);

        List<ProductReservationResponse> responseList = new ArrayList<>();

        for (BorrowedProduct bp : page.getContent()) {

            Product product = productRepository.findByProductId(bp.getProductId());

            LocalDate dueDay = bp.getDueDate();
            boolean overdue = false;

            if (dueDay != null && bp.getReturnDate() == null) {
                overdue = LocalDate.now().isAfter(dueDay);
            }

            ProductReservationResponse res = new ProductReservationResponse();

            res.setId(bp.getId());
            res.setName(user.getName());
            res.setProductName(product.getProductName());
            res.setImage(product.getImage());
            res.setQuantity(bp.getQuantity());
            res.setStatus(bp.getStatus());
            res.setBorrowDay(bp.getBorrowDate());
            res.setDueDay(dueDay);
            res.setOverdue(overdue);
            res.setCreatedAt(bp.getCreatedAt());
            res.setUpdatedAt(bp.getUpdatedAt());

            responseList.add(res);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", responseList);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách mượn sách thành công", result);
    }


    public MyApiResponse<List<ProductResponse>> getTopBorrowedProducts(Pageable pageable) {
        Page<Product> page = productRepository.findTopBorrowedProducts(pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());


            productResponse.setAuthorName(product.getAuthorName());


            return productResponse;

        }).collect(Collectors.toList());

        return MyApiResponse.success("Lấy danh sách sách thành công", responses);
    }


    public MyApiResponse<List<ProductResponse>> getTopFavouriteProducts(Pageable pageable) {
        Page<Product> page = productRepository.findTopFavouriteProducts(pageable);

        List<Product> products = page.getContent();

        if (products.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy sách");
        }

        List<ProductResponse> responses = products.stream().map(product -> {

            Publisher publisher = publisherRepository.findPublisherById(product.getPublisherId());

            ProductResponse productResponse = new ProductResponse();

            productResponse.setProductName(product.getProductName());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setDescription(product.getDescription());
            productResponse.setStatus(product.getStatus());
            productResponse.setPublicationDate(product.getPublicationDate());
            productResponse.setHashId(product.getHashId());
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            productResponse.setImage(product.getImage());
            productResponse.setPrice(product.getPrice());
            productResponse.setIsNew(product.getIsNew());
            productResponse.setLocation(product.getLocation());

            productResponse.setPublisherName(
                    publisher != null ? publisher.getPublisherName() : null
            );

            productResponse.setCategoryName(product.getCategoryName());


            productResponse.setAuthorName(product.getAuthorName());


            return productResponse;

        }).collect(Collectors.toList());

        return MyApiResponse.success("Lấy danh sách sách thành công", responses);
    }

    public MyApiResponse<Map<String, Object>> getAllProductReservation(Pageable pageable) {

        Page<BorrowedProduct> page = borrowedProductRepository.findAllBorrowedProduct(pageable);
        List<BorrowedProduct> borrowedList = page.getContent();

        List<ProductReservationResponse> responseList = new ArrayList<>();

        for (BorrowedProduct bp : borrowedList) {

            Product product = productRepository.findByProductId(bp.getProductId());
            Reader user = readerRepository.findByReaderId(bp.getReaderId());

            ProductReservationResponse res = new ProductReservationResponse();
            res.setId(bp.getId());
            res.setName(user.getName());
            res.setEmail(user.getEmail());
            res.setProductName(product.getProductName());
            res.setImage(product.getImage());
            res.setQuantity(bp.getQuantity());
            res.setStatus(bp.getStatus());
            res.setBorrowDay(bp.getBorrowDate());
            res.setDueDay(bp.getDueDate());
            res.setCreatedAt(bp.getCreatedAt());
            res.setUpdatedAt(bp.getUpdatedAt());

            responseList.add(res);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", responseList);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách mượn sách thành công", result);
    }


    public MyApiResponse<Map<String, Object>> getSearchAllProductReservation(String keyword,Pageable pageable) {
        Page<ProductReservation> page =  productReservationRepository.searchProductReservation(keyword,pageable);
        List<ProductReservation> reservations = page.getContent();

        List<ProductReservationResponse> responseList = new ArrayList<>();

        for (ProductReservation reservation : reservations) {

            Product product = productRepository.findByProductId(reservation.getProductId());

            Reader user = readerRepository.findByReaderId(reservation.getReaderId());

            ProductReservationResponse res = new ProductReservationResponse();
            res.setId(reservation.getId());
            res.setName(user.getName());
            res.setEmail(user.getEmail());
            res.setProductName(product.getProductName());
            res.setImage(product.getImage());
            res.setQuantity(reservation.getQuantity());
            res.setStatus(reservation.getStatus());
            res.setBorrowDay(reservation.getBorrowDay());
            res.setCreatedAt(reservation.getCreatedAt());
            res.setUpdatedAt(reservation.getUpdatedAt());

            responseList.add(res);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", responseList);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách đơn đăng ký thành công", result);
    }



    @Transactional
    public MyApiResponse<ProductReservationResponse> updateProductReservation(Long borrowedProductId, Long newStatus) {

        BorrowedProduct bp = borrowedProductRepository.findById(borrowedProductId).orElse(null);
        if (bp == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu mượn sách");
        }

        Reader user = readerRepository.findByReaderId(bp.getReaderId());
        Product product = productRepository.findByProductId(bp.getProductId());

        Long currentStatus = bp.getStatus();

        // ❌ đã kết thúc
        if (currentStatus == 0L || currentStatus == 4L) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST,
                    "Yêu cầu đã kết thúc và không thể thay đổi trạng thái");
        }

        boolean isValid = false;

        switch (currentStatus.intValue()) {

            case 1:
                if (newStatus == 2L || newStatus == 0L) isValid = true;
                break;

            case 2:
                if (newStatus == 3L || newStatus == 0L) isValid = true;
                break;

            case 3:
                return MyApiResponse.error(HttpStatus.BAD_REQUEST,
                        "Đang mượn – hãy dùng chức năng trả sách");
        }

        if (!isValid) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST,
                    "Không thể chuyển trạng thái từ " + currentStatus + " sang " + newStatus);
        }

        Long qty = bp.getQuantity();
        LocalDate now = LocalDate.now();

        // ✅ Trừ kho khi duyệt
        if (currentStatus == 1 && newStatus == 2) {

            if (product.getQuantity() < qty) {
                return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số lượng sách trong kho không đủ");
            }

            product.setQuantity(product.getQuantity() - qty);
        }

        // ✅ Hủy sau khi duyệt → cộng kho
        if (currentStatus == 2 && newStatus == 0) {
            product.setQuantity(product.getQuantity() + qty);
        }

        // ================= NHẬN SÁCH =================
        if (currentStatus == 2 && newStatus == 3) {

            bp.setBorrowDate(now); // ngày mượn thực tế

            // 🔥 tạo fee nếu chưa có
            BorrowedFee fee = borrowedFeeRepository.findByBorrowedProductId(bp.getId());

            if (fee == null) {
                fee = new BorrowedFee();
                fee.setBorrowedProductId(bp.getId());
                fee.setLateFee(0L);
                fee.setCompensationFee(0L);
                fee.setTotalFee(0L);
                fee.setHasLostItemFeePaid(0L);
                fee.setHasOverdueFeePaid(0L);
                fee.setCreatedAt(now);

                borrowedFeeRepository.save(fee);
            }
        }

        product.setUpdatedAt(now);
        productRepository.save(product);

        bp.setStatus(newStatus);
        bp.setUpdatedAt(now);
        borrowedProductRepository.save(bp);

        // ================= RESPONSE =================
        ProductReservationResponse res = new ProductReservationResponse();
        res.setId(bp.getId());
        res.setName(user.getName());
        res.setProductName(product.getProductName());
        res.setImage(product.getImage());
        res.setQuantity(bp.getQuantity());
        res.setStatus(bp.getStatus());
        res.setBorrowDay(bp.getBorrowDate());
        res.setDueDay(bp.getDueDate());
        res.setCreatedAt(bp.getCreatedAt());
        res.setUpdatedAt(bp.getUpdatedAt());

        String statusText = switch (newStatus.intValue()) {
            case 1 -> "Chờ duyệt";
            case 2 -> "Đã duyệt – Chờ nhận sách";
            case 3 -> "Đã nhận sách";
            case 4 -> "Đã trả sách";
            case 0 -> "Đã hủy";
            default -> "";
        };

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vinhdaumoi2805@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Cập nhật trạng thái mượn sách");
        message.setText(
                "Xin chào " + user.getName() + ",\n\n" +
                        "Yêu cầu mượn sách của bạn đối với cuốn sách: \"" + product.getProductName() + "\"\n" +
                        "Đã được cập nhật sang trạng thái: " + statusText + ".\n\n" +
                        "Trân trọng,\nThư viện Sao Việt"
        );

        emailSender.send(message);

        return MyApiResponse.success("Cập nhật trạng thái thành công", res);
    }



    public MyApiResponse<List<BorrowStatisticResponse>> getBorrowStatisticByYear(int year) {

        List<Object[]> result = borrowedProductRepository.getBorrowCountByMonth(year);

        List<BorrowStatisticResponse> response = new ArrayList<>();

        for (Object[] row : result) {
            int month = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();

            response.add(new BorrowStatisticResponse(month, count));
        }

        return MyApiResponse.success("",response);
    }

    public MyApiResponse<List<Map<String, Object>>> getProductCountByMonth() {
        List<Map<String, Object>> result = productRepository.countProductsByMonth();
        return MyApiResponse.success("Thống kê số lượng sách theo tháng", result);
    }

    public MyApiResponse<Map<String, Object>> searchByDateRange(
            String type, LocalDate start, LocalDate end, Pageable pageable
    ) {

        Page<ProductReservation> page =
                productReservationRepository.searchByDateRange(type, start, end, pageable);

        List<ProductReservation> listProductReservationResponse = page.getContent();

        if (listProductReservationResponse.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không có danh sách mượn trả nào.");
        }

        List<ProductReservationResponse> productReservationResponse = listProductReservationResponse.stream()
                .map(pr -> {

                    Product product = productRepository.findByProductId(pr.getProductId());
                    Reader user = readerRepository.findByReaderId(pr.getReaderId());

                    ProductReservationResponse res = new ProductReservationResponse();
                    res.setId(pr.getId());
                    res.setName(user.getName());
                    res.setProductName(product.getProductName());
                    res.setImage(product.getImage());
                    res.setQuantity(pr.getQuantity());
                    res.setStatus(pr.getStatus());
                    res.setBorrowDay(pr.getBorrowDay());
                    res.setCreatedAt(pr.getCreatedAt());
                    res.setUpdatedAt(pr.getUpdatedAt());

                    return res;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", productReservationResponse);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy dữ liệu thành công",result);
    }


}
