package my.learn.secondhw.service;

import jakarta.persistence.EntityNotFoundException;
import my.learn.secondhw.model.Address;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.model.AttractionType;
import my.learn.secondhw.repository.AddressRepository;
import my.learn.secondhw.repository.AttrServRepository;
import my.learn.secondhw.repository.AttractionRepository;
import my.learn.secondhw.service.attraction.AttractionHandlingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки логики класса AttractionHandlingServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class AttractionHandlingServiceImplTest {

    @Mock
    private AttractionRepository attractionRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AttrServRepository attrServRepository;

    @InjectMocks
    private AttractionHandlingServiceImpl attractionHandlingServiceImpl;

    /**
     * Тестирует метод findAll, проверяя, что он возвращает все Attraction.
     */
    @Test
    void findAllTest() {
        Attraction a1 = new Attraction();

        a1.setName("Some Attraction");
        a1.setDescription("Some Attraction Description");
        a1.setAttractionType(AttractionType.ARCHEOLOGY);

        Attraction a2 = new Attraction();

        a2.setName("Some Attraction 2");
        a2.setDescription("Some Attraction Description 2");
        a2.setAttractionType(AttractionType.PARK);

        Attraction a3 = new Attraction();

        a3.setName("Some Attraction3");
        a3.setDescription("Some Attraction Description3");
        a3.setAttractionType(AttractionType.MUSEUM);


        List<Attraction> attractions = List.of(a1, a2, a3);

        when(attractionRepository.findAll()).thenReturn(attractions);

        List<Attraction> result = attractionHandlingServiceImpl.findAll();

        assertTrue(result.contains(a1));
        assertTrue(result.contains(a2));
        assertTrue(result.contains(a3));
        assertEquals(result.size(), attractions.size());
    }

    /**
     * Тестирует метод findById, проверяя наличие Attraction по её идентификатору.
     *
     * @throws IllegalArgumentException если Attraction не найдена
     */
    @Test
    void findByIdTest() {
        Attraction a1 = new Attraction();
        a1.setName("Some Attraction");
        a1.setDescription("Some Attraction Description");
        a1.setAttractionType(AttractionType.ARCHEOLOGY);
        a1.setId(UUID.randomUUID());

        when(attractionRepository.findById(a1.getId())).thenReturn(Optional.of(a1));

        Optional<Attraction> result = attractionHandlingServiceImpl.findById(a1.getId());

        assertTrue(result.isPresent());
        assertEquals(a1, result.get());
    }

    /**
     * Тестирует метод findAllByCity, проверяя получение всех Attraction по названию города.
     */
    @Test
    void findAllByCityTest() {
        Address address = new Address();
        address.setCity("City");
        Attraction a1 = new Attraction();
        a1.setName("Some Attraction");
        a1.setDescription("Some Attraction Description");
        a1.setAttractionType(AttractionType.ARCHEOLOGY);
        a1.setId(UUID.randomUUID());
        a1.setAddress(address);
        Attraction a2 = new Attraction();
        a2.setName("Some Attraction");
        a2.setDescription("Some Attraction Description");
        a2.setAttractionType(AttractionType.ARCHEOLOGY);
        a2.setId(UUID.randomUUID());
        a2.setAddress(address);

        when(attractionRepository.findAllByCity("City")).thenReturn(List.of(a1, a2));

        List<Attraction> result = attractionHandlingServiceImpl.findAllByCity("City");

        assertTrue(result.contains(a1));
        assertTrue(result.contains(a2));
        assertEquals(result.size(), 2);
    }

    /**
     * Тестирует метод findAllByService, проверяя получение всех Attraction по имени AttractionService.
     */
    @Test
    void findAllByServiceTest() {
        UUID service1Id = UUID.randomUUID();
        AttractionService service1 = new AttractionService();
        service1.setId(service1Id);
        service1.setName("Name");

        UUID service2Id = UUID.randomUUID();
        AttractionService service2 = new AttractionService();
        service2.setId(service2Id);
        service2.setName("Random");

        List<AttractionService> serviceList = new ArrayList<>();
        serviceList.add(service1);
        serviceList.add(service2);

        Attraction a1 = new Attraction();
        Attraction a2 = new Attraction();
        a1.setAttractionServices(serviceList);
        a2.setAttractionServices(serviceList);

        when(attractionRepository.findAllByServiceName("Name")).thenReturn(List.of(a1, a2));
        List<Attraction> result = attractionHandlingServiceImpl.findAllByService("Name");
        assertTrue(result.contains(a1));
        assertTrue(result.contains(a2));
        assertEquals(result.size(), 2);
    }

    /**
     * Тестирует метод save, проверяя исключение при попытке сохранить существующий Attraction.
     */
    @Test
    void saveAttractionAlreadyExistsTest() {
        Attraction attraction = new Attraction();
        attraction.setId(UUID.randomUUID());

        when(attractionRepository.findById(attraction.getId())).thenReturn(Optional.of(attraction));

        assertThrows(IllegalArgumentException.class, () -> attractionHandlingServiceImpl.save(attraction));

    }

    /**
     * Тестирует метод save, проверяя сохранение Attraction с нулевым идентификатором.
     */
    @Test
    void saveIdNullTest() {
        Attraction attraction = new Attraction();

        Attraction result = attractionHandlingServiceImpl.save(attraction);

        verify(attractionRepository, times(1)).save(attraction);
    }

    /**
     * Тестирует метод save, проверяя сохранение Attraction с ненулевым идентификатором.
     */
    @Test
    void saveIdNotNullTest() {
        Attraction attraction = new Attraction();
        attraction.setId(UUID.randomUUID());

        when(attractionRepository.findById(attraction.getId())).thenReturn(Optional.empty());
        Attraction result = attractionHandlingServiceImpl.save(attraction);

        verify(attractionRepository, times(1)).save(attraction);
    }

    /**
     * Тестирует метод deleteById, проверяя удаление Attraction по идентификатору.
     */
    @Test
    void deleteByIdTest() {
        UUID id = UUID.randomUUID();

        attractionRepository.deleteById(id);

        verify(attractionRepository, times(1)).deleteById(id);
    }

    @Test
    void updateNoAttractionWithGivenIdTest() {
        Attraction attraction = new Attraction();
        attraction.setId(UUID.randomUUID());

        when(attractionRepository.existsById(attraction.getId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> attractionHandlingServiceImpl.update(attraction));
    }

    /**
     * Тестирует метод update, проверяя обновление Attraction.
     */
    @Test
    void updateTest() {
        Attraction attraction = new Attraction();
        attraction.setId(UUID.randomUUID());

        attractionHandlingServiceImpl.save(attraction);

        verify(attractionRepository, times(1)).save(attraction);
    }

    /**
     * Тестирует метод update, проверяя выбрасывание исключения при отсутствии Attraction с данным идентификатором.
     */
    @Test
    void updateAddressNoAttractionWithIdTest() {
        UUID id = UUID.randomUUID();
        when(attractionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attractionHandlingServiceImpl.updateAddress(id, UUID.randomUUID()));
    }

    /**
     * Тестирует метод updateAddress, проверяя выбрасывание исключения при отсутствии Address с данным идентификатором.
     */
    @Test
    void updateAddressNoAddressWithIdTest() {
        UUID attractionId = UUID.randomUUID();
        Attraction attraction = new Attraction();
        attraction.setId(attractionId);

        UUID addressId = UUID.randomUUID();

        when(attractionRepository.findById(attractionId)).thenReturn(Optional.of(attraction));
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attractionHandlingServiceImpl.updateAddress(attractionId, addressId));
    }

    /**
     * Тестирует метод updateAddress, проверяя корректное обновление Address аттракции.
     */
    @Test
    void updateAddressTest() {
        UUID attractionId = UUID.randomUUID();
        Attraction attraction = new Attraction();
        attraction.setId(attractionId);

        UUID addressId = UUID.randomUUID();
        Address address = new Address();
        address.setId(addressId);

        when(attractionRepository.findById(attractionId)).thenReturn(Optional.of(attraction));
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        attractionHandlingServiceImpl.updateAddress(attractionId, addressId);

        assertEquals(attraction.getAddress(), address);
        verify(attractionRepository, times(1)).save(attraction);
    }

    /**
     * Тестирует метод addService, проверяя выбрасывание исключения при отсутствии Attraction с данным идентификатором.
     */
    @Test
    void addServiceNoAttractionWithIdTest() {
        UUID id = UUID.randomUUID();
        when(attractionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attractionHandlingServiceImpl.updateAddress(id, UUID.randomUUID()));
    }

    /**
     * Тестирует метод addService, проверяя выбрасывание исключения при отсутствии AttractionService с данным идентификатором.
     */
    @Test
    void addServiceNoServiceWithIdTest() {
        UUID attractionId = UUID.randomUUID();
        Attraction attraction = new Attraction();
        attraction.setId(attractionId);

        UUID serviceId = UUID.randomUUID();

        when(attractionRepository.findById(attractionId)).thenReturn(Optional.of(attraction));
        when(attrServRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attractionHandlingServiceImpl.addService(attractionId, serviceId));
    }

    /**
     * Тестирует метод addService, проверяя добавление AttractionService к Attraction.
     */
    @Test
    void addServiceTest() {
        UUID attractionId = UUID.randomUUID();
        Attraction attraction = new Attraction();
        attraction.setId(attractionId);
        attraction.setAttractionServices(new ArrayList<>());

        UUID serviceId = UUID.randomUUID();
        AttractionService service = new AttractionService();
        service.setId(serviceId);

        when(attractionRepository.findById(attractionId)).thenReturn(Optional.of(attraction));
        when(attrServRepository.findById(serviceId)).thenReturn(Optional.of(service));

        attractionHandlingServiceImpl.addService(attractionId, serviceId);

        assertTrue(attraction.getAttractionServices().contains(service));
        verify(attractionRepository, times(1)).save(attraction);
    }

}
