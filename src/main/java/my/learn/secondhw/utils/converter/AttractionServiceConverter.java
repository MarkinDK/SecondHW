package my.learn.secondhw.utils.converter;

import my.learn.secondhw.dto.AttrServDto;
import my.learn.secondhw.model.AttractionService;

/**
 * Класс AttractionServiceConverter предоставляет метод для преобразования
 * объектов AttractionService из DTO в сущности.
 */
public class AttractionServiceConverter {
    /**
     * Преобразует объект AttrServDto в объект AttractionService.
     *
     * @param attrServDto объект AttrServDto, который нужно преобразовать.
     * @return новый объект AttractionService, заполненный данными из attrServDto.
     */
    public static AttractionService dtoToAttractionService(AttrServDto attrServDto) {
        AttractionService attractionService = new AttractionService();
        attractionService.setName(attrServDto.getName());
        attractionService.setDescription(attrServDto.getDescription());
        attractionService.setServiceType(attrServDto.getServiceType());
        attractionService.setId(attrServDto.getId());
        return attractionService;
    }
}
