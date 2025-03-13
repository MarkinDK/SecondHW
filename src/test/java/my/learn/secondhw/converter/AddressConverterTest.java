package my.learn.secondhw.converter;

import my.learn.secondhw.dto.AddressDto;
import my.learn.secondhw.model.Address;
import my.learn.secondhw.utils.converter.AddressConverter;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс AddressConverterTest содержит тесты для проверки правильности
 * работы конвертера адресов AddressConverter.
 */
public class AddressConverterTest {
    /**
     * Тестирует метод dtoToAddress, проверяя, что преобразованный адрес
     * содержит корректные данные из AddressDto.
     */
    @Test
    void dtoToAddressTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet("Street");
        addressDto.setCity("City");
        addressDto.setBuilding(10);
        addressDto.setId(UUID.randomUUID());

        Address address = AddressConverter.dtoToAddress(addressDto);
        assertEquals(address.getCity(), addressDto.getCity());
        assertEquals(address.getBuilding(), addressDto.getBuilding());
        assertEquals(address.getStreet(), addressDto.getStreet());
        assertEquals(address.getId(), addressDto.getId());
    }

    /**
     * Тестирует метод partialUpdateConverter, проверяя, что
     * город не обновляется, когда новое значение не задано.
     */
    @Test
    void partialUpdateConverterNoUpdatedCityTest() {
        Address presentAddress = new Address();
        presentAddress.setStreet("Street");
        presentAddress.setCity("City");
        presentAddress.setBuilding(10);

        Address updatedAddress = new Address();
        updatedAddress.setStreet("Street2");
        updatedAddress.setBuilding(102);

        Address address = AddressConverter.partialUpdateConverter(presentAddress, updatedAddress);

        assertEquals(address.getCity(), presentAddress.getCity());
        assertEquals(address.getBuilding(), updatedAddress.getBuilding());
        assertEquals(address.getStreet(), updatedAddress.getStreet());
    }

    /**
     * Тестирует метод partialUpdateConverter, проверяя, что
     * город обновляется, когда новое значение задано.
     */
    @Test
    void partialUpdateConverterWithUpdatedCityTest() {
        Address presentAddress = new Address();
        presentAddress.setStreet("Street");
        presentAddress.setCity("City");
        presentAddress.setBuilding(10);

        Address updatedAddress = new Address();
        updatedAddress.setStreet("Street2");
        updatedAddress.setBuilding(102);
        updatedAddress.setCity("City2");

        Address address = AddressConverter.partialUpdateConverter(presentAddress, updatedAddress);

        assertEquals(address.getCity(), updatedAddress.getCity());
        assertEquals(address.getBuilding(), updatedAddress.getBuilding());
        assertEquals(address.getStreet(), updatedAddress.getStreet());
    }
}
