package my.learn.secondhw.service;

import jakarta.persistence.EntityNotFoundException;
import my.learn.secondhw.exception.TicketInfoExistsException;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.model.TicketInfo;
import my.learn.secondhw.repository.AttractionRepository;
import my.learn.secondhw.repository.TicketInfoRepository;
import my.learn.secondhw.service.ticketinfo.TicketInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки логики класса TicketInfoServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class TicketInfoServiceImplTests {
    @Mock
    private TicketInfoRepository ticketInfoRepository;

    @Mock
    private AttractionRepository attractionRepository;

    @InjectMocks
    private TicketInfoServiceImpl ticketInfoService;

    /**
     * Тестирует метод findAll, проверяя получение всех сохранённых TicketInfo.
     */
    @Test
    void findAllTest() {
        Attraction a1 = new Attraction();
        a1.setId(UUID.randomUUID());

        Attraction a2 = new Attraction();
        a2.setId(UUID.randomUUID());

        TicketInfo ticketInfo1 = new TicketInfo();
        ticketInfo1.setId(UUID.randomUUID());
        ticketInfo1.setCurrency("USD");
        ticketInfo1.setPrice(new BigDecimal("300.00"));
        ticketInfo1.setAvailability(true);
        ticketInfo1.setAttraction(a1);

        TicketInfo ticketInfo2 = new TicketInfo();
        ticketInfo2.setId(UUID.randomUUID());
        ticketInfo2.setCurrency("EUR");
        ticketInfo2.setPrice(new BigDecimal("100.00"));
        ticketInfo2.setAvailability(false);
        ticketInfo2.setAttraction(a2);

        List<TicketInfo> ticketInfoList = List.of(ticketInfo1, ticketInfo2);
        when(ticketInfoRepository.findAll()).thenReturn(ticketInfoList);

        List<TicketInfo> result = ticketInfoService.findAll();

        verify(ticketInfoRepository, times(1)).findAll();

        assertTrue(result.contains(ticketInfo1));
        assertTrue(result.contains(ticketInfo2));
        assertEquals(ticketInfoList.size(), result.size());
    }

    /**
     * Тестирует метод findById, проверяя поиск TicketInfo по идентификатору.
     */
    @Test
    void findByIdTest() {
        Attraction a1 = new Attraction();
        a1.setId(UUID.randomUUID());

        TicketInfo ticketInfo = new TicketInfo();
        UUID id = UUID.randomUUID();
        ticketInfo.setId(id);
        ticketInfo.setCurrency("USD");
        ticketInfo.setPrice(new BigDecimal("300.00"));
        ticketInfo.setAvailability(true);
        ticketInfo.setAttraction(a1);

        when(ticketInfoRepository.findById(id)).thenReturn(Optional.of(ticketInfo));

        Optional<TicketInfo> result = ticketInfoService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(ticketInfo, result.get());
    }

    /**
     * Тестирует метод save, проверяя выбрасывание исключения, если нет связанного объекта Attraction.
     */
    @Test
    void saveTicketInfoWhenAttractionDoesNotExistTest() {
        UUID id = UUID.randomUUID();
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setAttractionId(id);

        when(attractionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ticketInfoService.save(ticketInfo));
    }

    /**
     * Тестирует метод save, проверяя выбрасывание исключения, если TicketInfo уже существует для объекта Attraction.
     */
    @Test
    void saveTicketInfoWhenTicketInfoAlreadyExists() {
        UUID id = UUID.randomUUID();
        TicketInfo ticketInfoToSave = new TicketInfo();
        ticketInfoToSave.setAttractionId(id);

        Attraction attraction = new Attraction();
        attraction.setId(id);
        TicketInfo ticketInfoExists = new TicketInfo();
        ticketInfoExists.setId(UUID.randomUUID());
        attraction.setTicketInfo(ticketInfoExists);


        when(attractionRepository.findById(id)).thenReturn(Optional.of(attraction));

        assertThrows(TicketInfoExistsException.class, () -> ticketInfoService.save(ticketInfoToSave));
    }

    /**
     * Тестирует метод save, проверяя успешное сохранение TicketInfo.
     */
    @Test
    void saveTest() {
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setAttractionId(UUID.randomUUID());
        Attraction attraction = new Attraction();
        when(attractionRepository.findById(ticketInfo.getAttractionId())).thenReturn(Optional.of(attraction));
        when(ticketInfoRepository.save(ticketInfo)).thenReturn(ticketInfo);

        TicketInfo result = ticketInfoService.save(ticketInfo);

        assertNotNull(result);
        assertEquals(ticketInfo, result);
        verify(attractionRepository, times(1)).findById(ticketInfo.getAttractionId());
        verify(ticketInfoRepository, times(1)).save(ticketInfo);
    }

    /**
     * Тестирует метод deleteById, проверяя поведение, если Attraction не найдена.
     */
    @Test
    void deleteByIdWhenAttractionNotFound() {
        UUID id = UUID.randomUUID();

        when(attractionRepository.findByTicketInfoId(id)).thenReturn(Optional.empty());

        ticketInfoService.deleteById(id);

        verify(attractionRepository, times(1)).findByTicketInfoId(id);
        verify(attractionRepository, never()).save(any(Attraction.class));
        verify(ticketInfoRepository, never()).deleteById(id);

    }

    /**
     * Тестирует метод deleteById.
     */
    @Test
    void deleteByIdTest() {
        UUID attractionId = UUID.randomUUID();
        Attraction attraction = new Attraction();
        attraction.setId(attractionId);

        TicketInfo ticketInfo = new TicketInfo();
        UUID ticketInfoId = UUID.randomUUID();
        ticketInfo.setId(ticketInfoId);
        attraction.setTicketInfo(ticketInfo);
        when(attractionRepository.findByTicketInfoId(attractionId)).thenReturn(Optional.of(attraction));

        ticketInfoService.deleteById(attractionId);

        verify(attractionRepository, times(1)).findByTicketInfoId(attractionId);
        verify(attractionRepository, times(1)).save(attraction);
        assertNull(attraction.getTicketInfo());
        verify(ticketInfoRepository, times(1)).deleteById(attractionId);
    }

    /**
     * Тестирует метод update, когда задан несуществующий идентификатор.
     */
    @Test
    void updateWhenNoWithId() {
        UUID id = UUID.randomUUID();
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setId(id);
        when(ticketInfoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ticketInfoService.update(ticketInfo));
    }

    /**
     * Тестирует метод update, обновление TicketInfo.
     */
    @Test
    void updateTest() {
        UUID id = UUID.randomUUID();
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setId(id);

        when(ticketInfoRepository.findById(id)).thenReturn(Optional.of(ticketInfo));

        ticketInfoService.update(ticketInfo);


        verify(ticketInfoRepository, times(1)).updateById(
                ticketInfo.getPrice(),
                ticketInfo.getCurrency(),
                ticketInfo.getAvailability(),
                ticketInfo.getId()
        );
    }

}
