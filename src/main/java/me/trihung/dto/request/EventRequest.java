package me.trihung.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import me.trihung.enums.Shape;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
public class EventRequest {
	private String id;

    @NotBlank(message = "Tên sự kiện là bắt buộc")
    @Size(min = 5, message = "Tên sự kiện phải có ít nhất 5 ký tự")
    private String eventName;

    @NotNull(message = "Ảnh đại diện là bắt buộc")
    @JsonIgnore
    private MultipartFile eventImage;
    
    @NotNull(message = "Ảnh bìa là bắt buộc")
    @JsonIgnore
    private MultipartFile eventBanner;

    @NotBlank(message = "Vui lòng chọn thể loại sự kiện")
    private String eventCategory; 

    @NotBlank(message = "Mô tả sự kiện là bắt buộc")
    @Size(min = 20, message = "Mô tả sự kiện phải có ít nhất 20 ký tự")
    private String eventDescription;

    @NotNull
    @Valid 
    private VenueRequest venue;

    @NotNull
    @Valid
    private OrganizerRequest organizer;

    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc là bắt buộc")
    private LocalDate endDate;

    @NotBlank(message = "Giờ bắt đầu là bắt buộc")
    private String startTime;

    @NotBlank(message = "Giờ kết thúc là bắt buộc")
    private String endTime;

    @NotBlank(message = "Đường dẫn sự kiện là bắt buộc")
    @Size(min = 3, message = "Đường dẫn phải có ít nhất 3 ký tự")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Đường dẫn chỉ được chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    @NotBlank(message = "Múi giờ là bắt buộc")
    private String timezone;

    @NotEmpty(message = "Sự kiện phải có ít nhất một khu vực (zone)")
    @Valid
    private List<ZoneRequest> zones;

    @NotNull
    @Valid
    private BankInfoRequest bankInfo;

    @Data
    public static class VenueRequest {
        @NotBlank(message = "Vui lòng chọn tỉnh/thành phố")
        private String province;

        @NotBlank(message = "Địa chỉ chi tiết là bắt buộc")
        private String address;
    }

    @Data
    public static class OrganizerRequest {
        @NotBlank(message = "Tên ban tổ chức là bắt buộc")
        private String name;

        @NotBlank(message = "Giới thiệu ban tổ chức là bắt buộc")
        private String bio;

        @NotNull(message = "Logo ban tổ chức là bắt buộc")
        @JsonIgnore
        private MultipartFile logo;
    }
    
    @Data
    public static class PointRequest {
        private double x;
        private double y;
    }

    @Data
    public static class CoordinatesRequest {
        private double x;
        private double y;
        private Double width;
        private Double height;
        private Double radius;
        private List<PointRequest> points;
    }

    @Data
    public static class ZoneRequest {
        @NotBlank(message = "Tên khu vực là bắt buộc")
        private String name;

        // Đã đổi tên từ capacity
        @NotNull(message = "Số lượng vé tối đa là bắt buộc")
        @Positive(message = "Số lượng vé phải là số dương")
        private Integer maxTickets; 

        @NotNull(message = "Giá vé là bắt buộc")
        @DecimalMin(value = "0.0", inclusive = true, message = "Giá không được là số âm")
        private BigDecimal price;
        
        // --- Các trường mới ---
        @NotBlank(message = "Màu sắc là bắt buộc")
        private String color;

        @NotNull(message = "Hình dạng là bắt buộc")
        private Shape shape;

        private Boolean isSellable;
        private Boolean isSeatingZone;
        private String description;
        private Integer soldTickets = 0;

        @NotNull(message = "Góc xoay là bắt buộc")
        private Double rotation;

        @NotNull(message = "Tọa độ là bắt buộc")
        @Valid
        private CoordinatesRequest coordinates;
    }

    @Data
    public static class BankInfoRequest {
        @NotBlank(message = "Tên chủ tài khoản là bắt buộc")
        @Size(min = 2, message = "Tên chủ tài khoản phải có ít nhất 2 ký tự")
        @Pattern(regexp = "^[A-Z\\s]+$", message = "Tên chủ tài khoản chỉ được chứa chữ cái viết hoa và khoảng trắng")
        private String accountHolder;

        @NotBlank(message = "Số tài khoản là bắt buộc")
        @Size(min = 8, max = 20, message = "Số tài khoản phải có từ 8 đến 20 số")
        @Pattern(regexp = "^\\d+$", message = "Số tài khoản chỉ được chứa số")
        private String accountNumber;

        @NotBlank(message = "Tên ngân hàng là bắt buộc")
        private String bankName;

        @Size(min = 3, message = "Chi nhánh phải có ít nhất 3 ký tự")
        private String bankBranch;
    }
}