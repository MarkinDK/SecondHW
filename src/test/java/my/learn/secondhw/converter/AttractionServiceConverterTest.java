package my.learn.secondhw.converter;

import my.learn.secondhw.dto.AttrServDto;
import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.model.ServiceType;
import my.learn.secondhw.utils.converter.AttractionServiceConverter;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс AttractionServiceConverterTest содержит тесты для проверки работы
 * конвертера AttractionServiceConverter.
 */
public class AttractionServiceConverterTest {
    /**
     * Тестирует метод dtoToAttractionService, проверяя, что AttrServDto
     * содержит корректные данные из AttrServDto.
     */
    @Test
    void dtoToAttractionServiceTest() {
        AttrServDto attrServDto = new AttrServDto();
        attrServDto.setName("Some name");
        attrServDto.setDescription("Some description");
        attrServDto.setServiceType(ServiceType.DINING);
        attrServDto.setId(UUID.randomUUID());

        AttractionService service = AttractionServiceConverter.dtoToAttractionService(attrServDto);

        assertEquals(service.getId(), attrServDto.getId());
        assertEquals(service.getName(), attrServDto.getName());
        assertEquals(service.getDescription(), attrServDto.getDescription());
        assertEquals(service.getServiceType(), attrServDto.getServiceType());
    }
}
