package com.example.demo.service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class RowDataService {

    private final ExcelReader excelReader;

    private List<?> getData(MultipartFile file) throws Exception {
        return this.excelReader.processExcel(file);
    }

    public List<?> validateFile(MultipartFile file) throws Exception {
        if(file.isEmpty() || file.getSize() == 0) {
            this.throwException(ErrorCode.FILE_EMPTY);
        }
        if(!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            this.throwException(ErrorCode.FILE_NOT_ACCEPTED);
        }

        return this.getData(file);
    }

    private void throwException(ErrorCode errorCode) {
        throw new BusinessException(errorCode.getCode(), errorCode.getMessage());
    }
}
