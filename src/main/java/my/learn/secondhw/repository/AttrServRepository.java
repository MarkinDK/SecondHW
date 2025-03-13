package my.learn.secondhw.repository;

import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface AttrServRepository extends JpaRepository<AttractionService, UUID> {
    @Transactional
    @Modifying
    @Query(
            "UPDATE AttractionService s SET " +
                    "s.name = :name, " +
                    "s.description = :description, " +
                    "s.serviceType = :serviceType " +
                    "WHERE s.id = :id")
    void updateById(
            @Param("name") String name,
            @Param("description") String description,
            @Param("serviceType") ServiceType serviceType,
            @Param("id") UUID id);
}
