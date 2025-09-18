package me.trihung.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    public List<String> getEventCategories() {
        return List.of("Hội thảo", "Sự kiện thể thao", "Âm nhạc", "Nghệ thuật", "Giáo dục", "Công nghệ", "Kinh doanh", "Giải trí", "Ẩm thực", "Du lịch", "Khác");
    }

    public List<String> getProvinces() {
        return List.of("Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ", "An Giang", "Bà Rịa - Vũng Tàu");
    }

    public List<String> getBanks() {
        return List.of("Vietcombank", "BIDV", "Vietinbank", "Agribank", "ACB", "Techcombank", "MB Bank", "VPBank", "TPBank");
    }

    
}