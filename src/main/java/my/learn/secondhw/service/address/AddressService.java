package my.learn.secondhw.service.address;

import my.learn.secondhw.model.Address;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressService {
    List<Address> findAll();

    Address save(Address address);

    void deleteById(UUID id);

    void update(Address updatedAddress);

    Optional<Address> findById(UUID id);
}
