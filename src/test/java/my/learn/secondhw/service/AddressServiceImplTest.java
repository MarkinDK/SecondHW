package my.learn.secondhw.service;

import jakarta.persistence.EntityNotFoundException;
import my.learn.secondhw.model.Address;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.repository.AddressRepository;
import my.learn.secondhw.repository.AttractionRepository;
import my.learn.secondhw.service.address.AddressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки логики класса AddressServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AttractionRepository attractionRepository;

    @InjectMocks
    private AddressServiceImpl addressServiceImpl;

    /**
     * Тестирует метод findAll, проверяя, что он возвращает все Address.
     */
    @Test
    void findAllTest() {
        Address address1 = new Address();
        address1.setId(UUID.randomUUID());
        address1.setBuilding(10);
        address1.setCity("San Francisco");
        address1.setStreet("Main Street");

        Address address2 = new Address();
        address2.setId(UUID.randomUUID());
        address2.setBuilding(102);
        address2.setCity("San Francisco 2");
        address2.setStreet("Main Street 2");

        List<Address> addresses = List.of(address1, address2);

        when(addressRepository.findAll()).thenReturn(addresses);

        List<Address> result = addressServiceImpl.findAll();
        assertTrue(result.contains(address1));
        assertTrue(result.contains(address2));
        assertEquals(2, result.size());
        verify(addressRepository, times(1)).findAll();
    }

    /**
     * Тестирует метод findById, проверяя наличие адреса по его идентификатору.
     *
     * @throws IllegalArgumentException если адрес не найден
     */
    @Test
    void findByIdTest() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setBuilding(10);
        address.setCity("San Francisco");
        address.setStreet("Main Street");

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        Optional<Address> result = addressServiceImpl.findById(address.getId());

        assertTrue(result.isPresent());
        assertEquals(address, result.get());
    }

    /**
     * Тестирует метод save, проверяя, что он выбрасывает исключение при city==null в объекте Address.
     */
    @Test
    void saveWithCityNullThrowsExceptionTest() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setBuilding(10);
        address.setStreet("Main Street");

        assertThrows(IllegalArgumentException.class, () -> addressServiceImpl.save(address));
    }

    /**
     * Тестирует метод save, проверяя, что он правильно сохраняет Address.
     */
    @Test
    void saveTest() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setBuilding(10);
        address.setStreet("Main Street");
        address.setCity("San Francisco");

        when(addressRepository.save(address)).thenReturn(address);

        Address result = addressServiceImpl.save(address);
        assertEquals(result, address);
        verify(addressRepository, times(1)).save(address);
    }

    /**
     * Тестирует метод deleteById, проверяя, что все Attraction, связанные с Address, удаляются при удалении Address.
     */
    @Test
    void deleteByIdTest() {
        UUID id = UUID.randomUUID();
        Address address = new Address();
        address.setId(id);

        Attraction a1 = new Attraction();
        Attraction a2 = new Attraction();
        Attraction a3 = new Attraction();
        a1.setAddress(address);
        a2.setAddress(address);
        a3.setAddress(address);

        List<Attraction> attractions = List.of(a1, a2, a3);

        when(attractionRepository.findAllByAddressId(id)).thenReturn(attractions);

        addressServiceImpl.deleteById(id);

        assertNull(a1.getAddress());
        assertNull(a2.getAddress());
        assertNull(a3.getAddress());

        verify(attractionRepository, times(1)).saveAll(attractions);
        verify(addressRepository, times(1)).deleteById(id);
    }

    /**
     * Тестирует метод update, проверяя, что он выбрасывает исключение, если идентификатор Address не существует в базе данных.
     *
     * @throws EntityNotFoundException если Address с указанным идентификатором не найден
     */
    @Test
    void updateWithIdNotInDBTest() {
        Address address = new Address();
        address.setId(UUID.randomUUID());

        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> addressServiceImpl.update(address));
    }

    /**
     * Тестирует метод update, проверяя, что Address обновляется в базе данных.
     */
    @Test
    void updateTest() {
        UUID id = UUID.randomUUID();
        Address address = new Address();
        address.setId(id);

        when(addressRepository.findById(id)).thenReturn(Optional.of(address));

        addressServiceImpl.update(address);

        verify(addressRepository, times(1)).save(address);
    }

}
