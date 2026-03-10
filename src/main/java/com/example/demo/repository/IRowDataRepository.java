package com.example.demo.repository;

import com.example.demo.entity.RowDataEntity;
import org.springframework.data.repository.CrudRepository;

public interface IRowDataRepository extends CrudRepository<RowDataEntity, Long> {
}
