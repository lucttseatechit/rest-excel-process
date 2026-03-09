package com.example.demo.service;

import com.example.demo.dto.DataErrorDto;
import com.example.demo.dto.RowData;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.util.CellReference;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;

@RequiredArgsConstructor
public class BatchDispatcher {

    private final ExecutorService executor;
    private final int batchSize;

    private final List<RowData> batch = new ArrayList<>();

    private final ConcurrentLinkedQueue<DataErrorDto> errors;
    private final ConcurrentLinkedQueue<RowData> batchData;

    public void addRow(RowData row) {

        batch.add(row);

        if (batch.size() >= batchSize) {
            submitBatch();
        }
    }

    private void submitBatch() {

        List<RowData> taskBatch = new ArrayList<>(batch);
        batchData.addAll(taskBatch);
        batch.clear();

        executor.submit(() -> validateBatch(taskBatch));
    }

    public void flush() {
        if (!batch.isEmpty()) {
            submitBatch();
        }
    }

    private void validateBatch(List<RowData> rows) {

        for (RowData row : rows) {

            if (row.getName() == null || row.getName().isBlank()) {
                String cellId = CellReference.convertNumToColString(0) + (rows.indexOf(row) + 2);
                errors.add(
                        new DataErrorDto(
                                "Name is empty",
                                cellId,
                                null
                        )
                );
            }

            if (row.getAge() < 0) {
                String cellId = CellReference.convertNumToColString(1) + (rows.indexOf(row) + 2);
                errors.add(
                        new DataErrorDto(
                                "Age is not smaller than zero",
                                cellId,
                                null
                        )
                );
            }
        }
    }
}
