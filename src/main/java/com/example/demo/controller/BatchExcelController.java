package com.example.demo.controller;

import com.example.demo.service.RowDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class BatchExcelController {

    private final RowDataService rowDataService;

    @PostMapping("/upload")
    public ResponseEntity<?> batchExcel(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(this.rowDataService.validateFile(file));
    }
}
