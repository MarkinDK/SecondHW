package my.learn.secondhw.service;

import jakarta.persistence.EntityNotFoundException;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.model.ServiceType;
import my.learn.secondhw.repository.AttrServRepository;
import my.learn.secondhw.repository.AttractionRepository;
import my.learn.secondhw.service.attractionservice.AttrServServiceImpl;
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
 * Тестовый класс для проверки логики класса AttrServServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class AttrServServiceImplTest {

    @Mock
    private AttrServRepository attrServRepository;

    @Mock
    private AttractionRepository attractionRepository;

    @InjectMocks
    private AttrServServiceImpl attrServServiceImpl;

    /**
     * Тестирует метод findAll, проверяя получение всех AttractionService.
     */
    @Test
    void findAllTest() {
        AttractionService service1 = new AttractionService();
        service1.setName("service1");
        service1.setDescription("service1");
        service1.setId(UUID.randomUUID());
        service1.setServiceType(ServiceType.DINING);

        AttractionService service2 = new AttractionService();
        service2.setName("service2");
        service2.setDescription("service2");
        service2.setId(UUID.randomUUID());
        service2.setServiceType(ServiceType.SPORT);

        List<AttractionService> serviceList = List.of(service1, service2);

        when(attrServRepository.findAll()).thenReturn(serviceList);

        List<AttractionService> result = attrServServiceImpl.findAll();

        assertTrue(result.contains(service1));
        assertTrue(result.contains(service2));
        assertEquals(result.size(), serviceList.size());
    }

    /**
     * Тестирует метод findById, проверяя поиск AttractionService по идентификатору.
     */
    @Test
    void findByIdTest() {
        AttractionService service = new AttractionService();
        service.setName("service1");
        service.setDescription("service1");
        service.setId(UUID.randomUUID());
        service.setServiceType(ServiceType.DINING);

        when(attrServRepository.findById(service.getId())).thenReturn(Optional.of(service));

        Optional<AttractionService> result = attrServServiceImpl.findById(service.getId());

        assertTrue(result.isPresent());
        assertEquals(service, result.get());
    }

    /**
     * Тестирует метод save, проверяя выбрасывание исключения при name==null у AttractionService.
     */
    @Test
    void saveWithoutNameThrowsExceptionTest() {
        AttractionService service = new AttractionService();
        service.setServiceType(ServiceType.DINING);

        assertThrows(IllegalArgumentException.class, () -> attrServServiceImpl.save(service));
    }

    /**
     * Тестирует метод save, проверяя выбрасывание исключения при serviceType==null AttractionService.
     */
    @Test
    void saveWithoutTypeThrowsExceptionTest() {
        AttractionService service = new AttractionService();
        service.setName("service");

        assertThrows(IllegalArgumentException.class, () -> attrServServiceImpl.save(service));
    }

    /**
     * Тестирует метод save, проверяя сохранение AttractionService.
     */
    @Test
    void saveTest() {
        AttractionService service = new AttractionService();
        service.setName("service");
        service.setServiceType(ServiceType.DINING);

        when(attrServRepository.save(service)).thenReturn(service);

        AttractionService result = attrServServiceImpl.save(service);

        assertEquals(service, result);
        verify(attrServRepository, times(1)).save(service);
    }

    /**
     * Тестирует метод deleteById, проверяя удаление AttractionService.
     */
    @Test
    void deleteByIdTest() {
        UUID serviceIdToDelete = UUID.randomUUID();
        AttractionService serviceToDelete = new AttractionService();
        serviceToDelete.setId(serviceIdToDelete);

        UUID serviceId = UUID.randomUUID();
        AttractionService serviceToKeep = new AttractionService();
        serviceToKeep.setId(serviceId);

        List<AttractionService> serviceList = new ArrayList<>();
        serviceList.add(serviceToDelete);
        serviceList.add(serviceToKeep);

        Attraction a1 = new Attraction();
        Attraction a2 = new Attraction();
        Attraction a3 = new Attraction();
        a1.setAttractionServices(serviceList);
        a2.setAttractionServices(serviceList);
        a3.setAttractionServices(serviceList);

        List<Attraction> attractions = List.of(a1, a2, a3);

        when(attractionRepository.findAttractionsByServiceId(serviceIdToDelete)).thenReturn(attractions);

        attrServServiceImpl.deleteById(serviceIdToDelete);

        verify(attrServRepository, times(1)).deleteById(serviceIdToDelete);
        verify(attractionRepository, times(1)).saveAll(attractions);

        assertFalse(a1.getAttractionServices().contains(serviceToDelete));
        assertFalse(a2.getAttractionServices().contains(serviceToDelete));
        assertFalse(a3.getAttractionServices().contains(serviceToDelete));

        assertTrue(a1.getAttractionServices().contains(serviceToKeep));
        assertTrue(a2.getAttractionServices().contains(serviceToKeep));
        assertTrue(a3.getAttractionServices().contains(serviceToKeep));
    }

    /**
     * Тестирует метод update, проверяя выбрасывание исключения при попытке обновления AttractionService с неправильным идентификатором.
     */
    @Test
    void updateWithWrongIdTest() {
        AttractionService service = new AttractionService();
        service.setName("service");
        service.setServiceType(ServiceType.DINING);
        service.setId(UUID.randomUUID());

        when(attrServRepository.findById(service.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attrServServiceImpl.update(service));
    }

    /**
     * Тестирует метод update, проверяя обновление AttractionService с правильным идентификатором.
     */
    @Test
    void updateTest() {
        AttractionService service = new AttractionService();
        service.setName("service");
        service.setServiceType(ServiceType.DINING);
        service.setId(UUID.randomUUID());

        when(attrServRepository.findById(service.getId())).thenReturn(Optional.of(service));

        attrServServiceImpl.update(service);

        verify(attrServRepository, times(1)).save(service);
    }

}
