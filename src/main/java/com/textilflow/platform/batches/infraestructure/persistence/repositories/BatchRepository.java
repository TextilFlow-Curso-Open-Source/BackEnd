package com.textilflow.platform.batches.infraestructure.persistence.repositories;

import com.textilflow.platform.batches.domain.model.aggregates.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByProductionDate(LocalDate productionDate);

    boolean existsByProductionDate(LocalDate productionDate);

    boolean existsByProductionDateAndIdIsNot(LocalDate productionDate, Long id);

}
