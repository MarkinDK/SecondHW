package my.learn.secondhw.service.attraction;

import my.learn.secondhw.model.Attraction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttractionHandlingService {
    List<Attraction> findAllByCity(String city);

    List<Attraction> findAllByNameContaining(String name);

    List<Attraction> findAll();

    Attraction save(Attraction attraction);

    void deleteById(UUID id);

    void update(Attraction attraction);

    Attraction updateAddress(UUID attractionID, UUID addressId);

    Optional<Attraction> findById(UUID id);

    Attraction addService(UUID attractionId, UUID serviceId);

    List<Attraction> findAllByService(String serviceName);
}
