package my.learn.secondhw.converter;

import my.learn.secondhw.dto.AddressDto;
import my.learn.secondhw.dto.AttrServDto;
import my.learn.secondhw.dto.AttractionDto;
import my.learn.secondhw.dto.TicketInfoDto;
import my.learn.secondhw.model.*;
import my.learn.secondhw.utils.converter.AttractionConverter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс AttractionConverterTest содержит тесты для проверки работы
 * конвертера AttractionConverter.
 */
public class AttractionConverterTest {
    /**
     * Тестирует метод dtoToAttraction, проверяя, что Attraction
     * содержит корректные данные из AttractionDto.
     */
    @Test
    void dtoToAttractionTest() {
        AttractionDto attractionDto = new AttractionDto();
        attractionDto.setName("Some Attraction");
        attractionDto.setDescription("Some Attraction Description");
        attractionDto.setAttractionType(AttractionType.ARCHEOLOGY);
        attractionDto.setId(UUID.randomUUID());

        AddressDto addressDto = new AddressDto();
        addressDto.setStreet("Norfolk st.");
        addressDto.setCity("Innsmuth");
        addressDto.setBuilding(48);
        addressDto.setId(UUID.randomUUID());
        attractionDto.setAddressDto(addressDto);

        AttrServDto attrServDto = new AttrServDto();
        attrServDto.setName("Some name");
        attrServDto.setDescription("Some description");
        attrServDto.setServiceType(ServiceType.DINING);
        attrServDto.setId(UUID.randomUUID());
        List<AttrServDto> attrServDtoList = new ArrayList<>();
        attrServDtoList.add(attrServDto);
        attractionDto.setAttrServDtoList(attrServDtoList);

        TicketInfoDto ticketInfoDto = new TicketInfoDto();
        ticketInfoDto.setCurrency("USD");
        ticketInfoDto.setPrice(new BigDecimal("300.00"));
        ticketInfoDto.setAvailability(true);
        attractionDto.setTicketInfoDto(ticketInfoDto);
        ticketInfoDto.setAttractionDto(attractionDto);
        ticketInfoDto.setAttractionId(attractionDto.getId());

        Attraction attraction = AttractionConverter.dtoToAttraction(attractionDto);

        assertEquals(attraction.getTicketInfo().getId(), ticketInfoDto.getId());
        assertEquals(attraction.getTicketInfo().getPrice(), ticketInfoDto.getPrice());
        assertEquals(attraction.getTicketInfo().getCurrency(), ticketInfoDto.getCurrency());
        assertEquals(attraction.getTicketInfo().getAvailability(), ticketInfoDto.getAvailability());
        assertEquals(attraction.getTicketInfo().getAttractionId(), ticketInfoDto.getAttractionId());

        Address address = attraction.getAddress();

        assertEquals(address.getCity(), addressDto.getCity());
        assertEquals(address.getBuilding(), addressDto.getBuilding());
        assertEquals(address.getStreet(), addressDto.getStreet());
        assertEquals(address.getId(), addressDto.getId());

        assertEquals(attraction.getAttractionServices().size(), attrServDtoList.size());

        AttractionService attractionService = attraction.getAttractionServices().get(0);
        assertEquals(attractionService.getId(), attrServDto.getId());
        assertEquals(attractionService.getName(), attrServDto.getName());
        assertEquals(attractionService.getDescription(), attrServDto.getDescription());
        assertEquals(attractionService.getServiceType(), attrServDto.getServiceType());

    }
}
