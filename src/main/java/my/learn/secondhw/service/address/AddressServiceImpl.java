package my.learn.secondhw.service.address;

import jakarta.persistence.EntityNotFoundException;
import my.learn.secondhw.model.Address;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.repository.AddressRepository;
import my.learn.secondhw.repository.AttractionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с адресами.
 */
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AttractionRepository attractionRepository;

    /**
     * Конструктор для инициализации AddressServiceImpl.
     *
     * @param addressRepository    репозиторий для работы с адресами
     * @param attractionRepository репозиторий для работы с аттракционами
     */
    public AddressServiceImpl(AddressRepository addressRepository, AttractionRepository attractionRepository) {
        this.addressRepository = addressRepository;
        this.attractionRepository = attractionRepository;
    }

    /**
     * Находит все адреса.
     *
     * @return список всех адресов
     */
    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    /**
     * Находит адрес по идентификатору.
     *
     * @param id идентификатор адреса
     * @return Optional<Address>
     */
    @Override
    public Optional<Address> findById(UUID id) {
        return addressRepository.findById(id);
    }

    /**
     * Сохраняет новый адрес.
     *
     * @param address адрес для сохранения
     * @return сохраненный адрес
     * @throws IllegalArgumentException если город не указан
     */
    @Override
    public Address save(Address address) {
        if (address.getCity() == null) {
            throw new IllegalArgumentException("City is required");
        }
        return addressRepository.save(address);
    }

    /**
     * Удаляет адрес по идентификатору.
     *
     * @param id идентификатор удаляемого адреса
     */
    @Override
    public void deleteById(UUID id) {
        List<Attraction> attractions = attractionRepository.findAllByAddressId(id);
        attractions.forEach(a -> a.setAddress(null));
        attractionRepository.saveAll(attractions);
        addressRepository.deleteById(id);
    }

    /**
     * Обновляет существующий адрес.
     *
     * @param updatedAddress адрес с обновленными данными
     * @throws EntityNotFoundException если адрес с указанным идентификатором не найден
     */
    @Override
    public void update(Address updatedAddress) {
        Optional<Address> optionalAddress = addressRepository.findById(updatedAddress.getId());
        if (optionalAddress.isPresent()) {
            Address address = optionalAddress.get();
            address.setCity(updatedAddress.getCity());
            address.setBuilding(updatedAddress.getBuilding());
            address.setStreet(updatedAddress.getStreet());
            addressRepository.save(address);
        } else {
            throw new EntityNotFoundException("No address found with id: " + updatedAddress.getId());
        }
    }

}
