package com.example.gestionpolizas.repository;

import com.example.gestionpolizas.model.EstadoPoliza;
import com.example.gestionpolizas.model.Poliza;
import com.example.gestionpolizas.model.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    @Query("SELECT p FROM Poliza p WHERE " +
           "(:tipo IS NULL OR p.tipo = :tipo) AND " +
           "(:estado IS NULL OR p.estado = :estado)")
    List<Poliza> findByTipoAndEstadoOptional(
            @Param("tipo") TipoPoliza tipo,
            @Param("estado") EstadoPoliza estado
    );
}
