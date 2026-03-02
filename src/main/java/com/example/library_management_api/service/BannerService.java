package com.example.library_management_api.service;

import com.example.library_management_api.models.Banner;
import com.example.library_management_api.repository.BannerRepository;
import com.example.library_management_api.request.BannerRequest;
import com.example.library_management_api.response.MyApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class BannerService {
    @Autowired
    BannerRepository bannerRepository;


    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public MyApiResponse<List<Banner>> getAllBanners() {
        List<Banner> banners = bannerRepository.findAll();
        if (banners.isEmpty()){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Không tìm thấy danh sách quảng cáo");
        }
        return MyApiResponse.success("Lấy danh sách quảng cáo thành công",banners);
    }

    public MyApiResponse<Banner> createBanner(BannerRequest bannerRequest, MultipartFile image) {

        if (bannerRequest == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ");
        }

        if (isBlank(bannerRequest.getTitle())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Chủ đề quảng cáo không được để trống");
        }

        if (image == null || image.isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ảnh quảng cáo không được để trống");
        }

        Banner banner = new Banner();
        banner.setTitle(bannerRequest.getTitle().trim());
        banner.setDescription(bannerRequest.getDescription());
        banner.setStatus(1L);

        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/image/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            File filePath = new File(dir, fileName);
            image.transferTo(filePath);

            banner.setImage("/uploads/image/" + fileName);

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu ảnh", e);
        }

        bannerRepository.save(banner);
        return MyApiResponse.success("Tạo quảng cáo thành công", banner);
    }


    public MyApiResponse<Banner> updateBanner(
            Long bannerId,
            BannerRequest bannerRequest,
            MultipartFile image
    ) {

        if (bannerId == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Id quảng cáo không được null");
        }

        if (bannerRequest == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ");
        }

        if (isBlank(bannerRequest.getTitle())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Chủ đề quảng cáo không được để trống");
        }

        if (image == null || image.isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ảnh quảng cáo không được để trống");
        }

        Banner banner = bannerRepository.findByBannerId(bannerId);
        if (banner == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy quảng cáo");
        }

        banner.setTitle(bannerRequest.getTitle().trim());
        banner.setDescription(bannerRequest.getDescription());

        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/image/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            File filePath = new File(dir, fileName);
            image.transferTo(filePath);

            // Xóa ảnh cũ
            if (banner.getImage() != null) {
                File oldFile = new File(System.getProperty("user.dir") + banner.getImage());
                if (oldFile.exists()) oldFile.delete();
            }

            banner.setImage("/uploads/image/" + fileName);

        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu ảnh mới", e);
        }

        bannerRepository.save(banner);
        return MyApiResponse.success("Cập nhật quảng cáo thành công", banner);
    }


    public MyApiResponse<Banner> changeStatus(Long bannerId) {
        Banner banner = bannerRepository.findByBannerId(bannerId);
        if (banner == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Không tìm thấy quảng cáo");
        }

        Long currentStatus = banner.getStatus();
        Long newStatus = (currentStatus != null && currentStatus == 1L) ? 0L : 1L;

        banner.setStatus(newStatus);
        bannerRepository.save(banner);
        return MyApiResponse.success("Thay đổi trạng thái thành công",banner);
    }
}
