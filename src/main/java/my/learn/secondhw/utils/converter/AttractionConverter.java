package my.learn.secondhw.utils.converter;

import my.learn.secondhw.dto.AddressDto;
import my.learn.secondhw.dto.AttrServDto;
import my.learn.secondhw.dto.AttractionDto;
import my.learn.secondhw.model.Address;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.model.TicketInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Конвертер для преобразования данных о достопримечательностях между
 * объектами Attraction и AttractionDto.
 */
public class AttractionConverter {
    /**
     * Преобразует объект AttractionDto в объект Attraction.
     *
     * @param attractionDto объект AttractionDto, который нужно преобразовать
     * @return преобразованный объект Attraction
     */
    public static Attraction dtoToAttraction(AttractionDto attractionDto) {
        Attraction attraction = createAttraction(attractionDto);

        if (attractionDto.getTicketInfoDto() != null) {
            TicketInfo ticketInfo = TicketInfoConverter.createTicketInfo(attractionDto.getTicketInfoDto());
            ticketInfo.setAttraction(attraction);
            attraction.setTicketInfo(ticketInfo);
        }
        return attraction;
    }

    /**
     * Создает новый объект Attraction из объекта AttractionDto.
     *
     * @param attractionDto объект AttractionDto, который нужно преобразовать
     * @return новый объект Attraction
     */
    public static Attraction createAttraction(AttractionDto attractionDto) {
        Attraction attraction = new Attraction();
        attraction.setId(attractionDto.getId());
        attraction.setName(attractionDto.getName());
        attraction.setDescription(attractionDto.getDescription());
        attraction.setAttractionType(attractionDto.getAttractionType());

        attraction.setAddress(checkAddressDto(attractionDto.getAddressDto()));

        attraction.setAttractionServices(checkAttrServDtoList(attractionDto.getAttrServDtoList()));

        return attraction;
    }

    /**
     * Проверяет наличие объекта AddressDto и преобразует его в объект Address.
     *
     * @param addressDto объект AddressDto, который нужно проверить и преобразовать
     * @return преобразованный объект Address или null, если addressDto является null
     */
    private static Address checkAddressDto(AddressDto addressDto) {
        return addressDto == null ? null : AddressConverter.dtoToAddress(addressDto);
    }

    /**
     * Проверяет список объектов AttrServDto и преобразует их в список объектов AttractionService.
     *
     * @param attrServDtoList список объектов AttrServDto, который нужно проверить и преобразовать
     * @return список преобразованных объектов AttractionService или null, если список является null
     */
    private static List<AttractionService> checkAttrServDtoList(List<AttrServDto> attrServDtoList) {
        return attrServDtoList == null ? null :
                attrServDtoList
                        .stream()
                        .map(AttractionServiceConverter::dtoToAttractionService)
                        .collect(Collectors.toList());
    }
}
