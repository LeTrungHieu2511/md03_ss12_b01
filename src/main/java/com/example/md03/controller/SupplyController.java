package com.example.md03.controller;

import com.example.md03.dto.*;
import com.example.md03.service.SupplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/supplies")
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService service;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplyResponse>> create(@Valid @RequestBody SupplyCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(201, "Tạo vật tư thành công", service.create(req)));
    }

    @PutMapping("/{id}")
    public ApiResponse<SupplyResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody SupplyUpdateRequest req) {
        return ApiResponse.of(200, "Cập nhật vật tư thành công", service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ApiResponse<List<SupplyResponse>> getAll() {
        return ApiResponse.of(200, "Lấy danh sách vật tư thành công", service.getAll());
    }

    @GetMapping("/search")
    public ApiResponse<List<SupplyResponse>> search(@RequestParam("name") String name) {
        return ApiResponse.of(200, "Kết quả tìm kiếm", service.search(name));
    }

    @PatchMapping("/{id}/export")
    public ApiResponse<SupplyResponse> export(@PathVariable Long id,
                                              @Valid @RequestBody AmountRequest req) {
        return ApiResponse.of(200, "Xuất kho thành công", service.export(id, req.getAmount()));
    }

    @PatchMapping("/{id}/import")
    public ApiResponse<SupplyResponse> importStock(@PathVariable Long id,
                                                   @Valid @RequestBody AmountRequest req) {
        return ApiResponse.of(200, "Nhập kho thành công", service.importStock(id, req.getAmount()));
    }

    @GetMapping("/statistics/daily-export")
    public ApiResponse<List<DailyExportResponse>> dailyExport() {
        return ApiResponse.of(200, "Thống kê xuất kho trong ngày", service.dailyExport());
    }

    @GetMapping("/statistics/top-export")
    public ApiResponse<TopExportResponse> topExport() {
        return ApiResponse.of(200, "Vật tư xuất kho nhiều nhất", service.topExport());
    }
}