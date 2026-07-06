package com.example.gestionpolizas.repository;

import com.example.gestionpolizas.model.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {
}
