package my.learn.secondhw.service.attractionservice;

import my.learn.secondhw.model.AttractionService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttrServService {
    List<AttractionService> findAll();

    AttractionService save(AttractionService attractionService);

    void deleteById(UUID id);

    void update(AttractionService attractionService);

    Optional<AttractionService> findById(UUID id);
}
