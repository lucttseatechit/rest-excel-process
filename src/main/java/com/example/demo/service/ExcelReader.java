package com.example.demo.service;

import com.example.demo.dto.DataErrorDto;
import com.example.demo.dto.RowData;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelReader {

    private final ExecutorService excelProcessExecutor;

    public List<?> processExcel(MultipartFile file) throws Exception {

        ConcurrentLinkedQueue<DataErrorDto> errors = new ConcurrentLinkedQueue<>();

        ConcurrentLinkedQueue<RowData> batchData = new ConcurrentLinkedQueue<>();

        BatchDispatcher dispatcher = new BatchDispatcher(excelProcessExecutor, 500, errors, batchData);

        try (InputStream is = file.getInputStream()) {

            OPCPackage pkg = OPCPackage.open(is);

            XSSFReader reader = new XSSFReader(pkg);

            SharedStringsTable sst = (SharedStringsTable) reader.getSharedStringsTable();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XMLReader parser = factory.newSAXParser().getXMLReader();

            parser.setContentHandler(new SheetHandler(sst, dispatcher));

            Iterator<InputStream> sheets = reader.getSheetsData();

            while (sheets.hasNext()) {

                try (InputStream sheet = sheets.next()) {

                    parser.parse(new InputSource(sheet));
                }
            }

            dispatcher.flush();
        }

        this.excelProcessExecutor.shutdown();
        boolean isFinished = this.excelProcessExecutor.awaitTermination(1, TimeUnit.HOURS);

        if (!isFinished) {
            this.excelProcessExecutor.shutdownNow();
            throw new BusinessException(ErrorCode.FILE_TIME_OUT.getCode(), ErrorCode.FILE_TIME_OUT.getMessage());
        }

        return new ArrayList<>(errors.isEmpty() ? batchData : errors);
    }

}
