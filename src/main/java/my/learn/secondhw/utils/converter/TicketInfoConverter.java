package my.learn.secondhw.utils.converter;

import my.learn.secondhw.dto.TicketInfoDto;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.model.TicketInfo;

/**
 * Конвертер для преобразования данных информации о билетах между
 * объектами TicketInfo и TicketInfoDto.
 */
public class TicketInfoConverter {
    /**
     * Преобразует объект TicketInfoDto в объект TicketInfo.
     *
     * @param ticketInfoDto объект TicketInfoDto, который нужно преобразовать
     * @return преобразованный объект TicketInfo
     */
    public static TicketInfo dtoToTicketInfo(TicketInfoDto ticketInfoDto) {
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setId(ticketInfoDto.getId());
        ticketInfo.setPrice(ticketInfoDto.getPrice());
        ticketInfo.setCurrency(ticketInfoDto.getCurrency());
        ticketInfo.setAvailability(ticketInfoDto.getAvailability());
        ticketInfo.setAttractionId(ticketInfoDto.getAttractionId());
        if (ticketInfoDto.getAttractionDto() != null) {
            Attraction attraction = AttractionConverter.createAttraction(ticketInfoDto.getAttractionDto());
            attraction.setTicketInfo(ticketInfo);
            ticketInfo.setAttraction(attraction);
        }
        return ticketInfo;
    }

    /**
     * Создает новый объект TicketInfo из объекта TicketInfoDto без
     * связывания с объектом Attraction.
     *
     * @param ticketInfoDto объект TicketInfoDto, который нужно преобразовать
     * @return новый объект TicketInfo
     */
    public static TicketInfo createTicketInfo(TicketInfoDto ticketInfoDto) {
        TicketInfo ticketInfo = new TicketInfo();

        ticketInfo.setId(ticketInfoDto.getId());
        ticketInfo.setPrice(ticketInfoDto.getPrice());
        ticketInfo.setAvailability(ticketInfoDto.getAvailability());
        ticketInfo.setCurrency(ticketInfoDto.getCurrency());
        ticketInfo.setAttractionId(ticketInfoDto.getAttractionId());

        return ticketInfo;
    }
}
