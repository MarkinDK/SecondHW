package my.learn.secondhw.repository;

import my.learn.secondhw.model.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, UUID> {
    List<Attraction> findAllByAddressId(UUID addressId);

    @Query("SELECT a FROM Attraction a JOIN a.address address WHERE address.city = :city")
    List<Attraction> findAllByCity(String city);

    Optional<Attraction> findByTicketInfoId(UUID ticketInfoId);

    @Query("SELECT a FROM Attraction a JOIN a.attractionServices s WHERE s.id = :serviceId")
    List<Attraction> findAttractionsByServiceId(@Param("serviceId") UUID serviceId);

    @Query("SELECT a FROM Attraction a JOIN a.attractionServices s WHERE s.name = :serviceName")
    List<Attraction> findAllByServiceName(String serviceName);

    @Query("SELECT a FROM Attraction a WHERE lower(a.name) like %:name%")
    List<Attraction> findAllByNameContainingIgnoreCase(String name);
}
