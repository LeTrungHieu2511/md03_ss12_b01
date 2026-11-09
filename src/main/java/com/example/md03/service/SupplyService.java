package com.example.md03.service;

import com.example.md03.dto.*;
import com.example.md03.entity.*;
import com.example.md03.exception.BadRequestException;
import com.example.md03.exception.ResourceNotFoundException;
import com.example.md03.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplyService {

    private static final Logger historyLog = LoggerFactory.getLogger("HISTORY");

    private final SupplyRepository supplyRepo;
    private final SupplyTransactionRepository txRepo;

    // 1. Thêm mới
    @Transactional
    public SupplyResponse create(SupplyCreateRequest req) {
        Supply s = new Supply();
        s.setName(req.getName());
        s.setSpecification(req.getSpecification());
        s.setProvider(req.getProvider());
        s.setUnit(req.getUnit());
        s.setQuantity(0); // luôn bằng 0
        Supply saved = supplyRepo.save(s);
        log.info("Đã tạo mới vật tư: {} với ID: {}", saved.getName(), saved.getId());
        return SupplyResponse.from(saved);
    }

    // 2. Cập nhật
    @Transactional
    public SupplyResponse update(Long id, SupplyUpdateRequest req) {
        if (req.getId() != null || req.getQuantity() != null) {
            log.warn("Client cố gửi dữ liệu cấm khi cập nhật vật tư ID {}: id={}, quantity={}",
                    id, req.getId(), req.getQuantity());
            throw new BadRequestException("Không được gửi trường id hoặc quantity khi cập nhật");
        }
        Supply s = findActive(id);
        s.setName(req.getName());
        s.setSpecification(req.getSpecification());
        s.setProvider(req.getProvider());
        log.info("Đã cập nhật vật tư ID: {}", id);
        return SupplyResponse.from(supplyRepo.save(s));
    }

    // 3. Xóa mềm
    @Transactional
    public void delete(Long id) {
        Supply s = findActive(id); // đã xóa hoặc không tồn tại -> 404
        s.setDeleted(true);
        supplyRepo.save(s);
        log.info("Đã xóa mềm vật tư ID: {}", id);
    }

    // 4. Danh sách
    @Transactional(readOnly = true)
    public List<SupplyResponse> getAll() {
        List<Supply> list = supplyRepo.findAllActive();
        log.debug("Truy vấn được {} vật tư đang hoạt động", list.size());
        log.info("Lấy danh sách vật tư");
        return list.stream().map(SupplyResponse::from).toList();
    }

    // 5. Tìm kiếm
    @Transactional(readOnly = true)
    public List<SupplyResponse> search(String name) {
        List<Supply> list = supplyRepo.searchByName("%" + name.trim().toLowerCase() + "%");
        if (list.isEmpty()) {
            log.info("Không tìm thấy vật tư nào khớp với từ khóa: {}", name);
        }
        return list.stream().map(SupplyResponse::from).toList();
    }

    // 6. Xuất kho
    @Transactional
    public SupplyResponse export(Long id, int amount) {
        Supply s = findActive(id);
        if (s.getQuantity() < amount) {
            log.error("Thất bại khi xuất kho ID {}: Yêu cầu {}, hiện có {}", id, amount, s.getQuantity());
            throw new BadRequestException("Số lượng tồn kho không đủ để xuất");
        }
        s.setQuantity(s.getQuantity() - amount);
        supplyRepo.save(s);
        txRepo.save(new SupplyTransaction(s, TransactionType.EXPORT, amount));
        log.info("Xuất kho ID {}, số lượng {}, tồn còn {}", id, amount, s.getQuantity());
        return SupplyResponse.from(s);
    }

    // 7. Nhập kho
    @Transactional
    public SupplyResponse importStock(Long id, int amount) {
        Supply s = findActive(id);
        int oldQuantity = s.getQuantity();
        s.setQuantity(oldQuantity + amount);
        supplyRepo.save(s);
        txRepo.save(new SupplyTransaction(s, TransactionType.IMPORT, amount));
        historyLog.info("Nhập kho ID {}, số lượng +{}, tồn cũ {}", id, amount, oldQuantity);
        log.info("Nhập kho ID {} thành công", id);
        return SupplyResponse.from(s);
    }

    // 8. Thống kê xuất trong ngày
    @Transactional(readOnly = true)
    public List<DailyExportResponse> dailyExport() {
        long begin = System.currentTimeMillis();
        log.info("Bắt đầu thống kê vật tư xuất kho trong ngày");
        LocalDateTime start = LocalDate.now().atStartOfDay();
        List<DailyExportResponse> result = txRepo
                .sumByTypeBetween(TransactionType.EXPORT, start, start.plusDays(1))
                .stream()
                .map(e -> new DailyExportResponse(e.getSupplyId(), e.getSupplyName(), e.getTotalQuantity()))
                .toList();
        log.info("Hoàn thành thống kê sau {} ms, {} vật tư", System.currentTimeMillis() - begin, result.size());
        return result;
    }

    // 9. Top xuất nhiều nhất
    @Transactional(readOnly = true)
    public TopExportResponse topExport() {
        List<ExportSummary> top = txRepo.findTopByType(TransactionType.EXPORT, PageRequest.of(0, 1));
        if (top.isEmpty()) {
            throw new ResourceNotFoundException("Chưa có dữ liệu giao dịch để thống kê");
        }
        ExportSummary e = top.get(0);
        log.info("Vật tư xuất nhiều nhất: {} ({})", e.getSupplyName(), e.getTotalQuantity());
        return new TopExportResponse(e.getSupplyName(), e.getTotalQuantity());
    }

    private Supply findActive(Long id) {
        return supplyRepo.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư với ID: " + id));
    }
}