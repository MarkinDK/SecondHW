package my.learn.secondhw.utils.converter;

import my.learn.secondhw.dto.AddressDto;
import my.learn.secondhw.model.Address;

/**
 * Класс AddressConverter предоставляет методы для преобразования объектов Address
 * между DTO и сущностями, а также для частичного обновления Address.
 */
public class AddressConverter {
    /**
     * Преобразует объект AddressDto в объект Address.
     *
     * @param addressDto объект AddressDto, который нужно преобразовать.
     * @return новый объект Address, заполненный данными из addressDto.
     */
    public static Address dtoToAddress(AddressDto addressDto) {
        Address address = new Address();
        address.setCity(addressDto.getCity());
        address.setId(addressDto.getId());
        address.setStreet(addressDto.getStreet());
        address.setBuilding(addressDto.getBuilding());
        return address;
    }

    /**
     * Обновляет поля существующего объекта Address на основе данных из
     * объекта обновленного Address. Если некоторые поля в объекте
     * обновленного Address равны null, будут использоваться значения из
     * существующего Address.
     *
     * @param presentAddress текущий объект Address, который нужно обновить.
     * @param updatedAddress объект Address с новыми данными.
     * @return новый объект Address с обновленными данными.
     */
    public static Address partialUpdateConverter(Address presentAddress, Address updatedAddress) {
        Address address = new Address();

        address.setCity(updatedAddress.getCity() == null ? presentAddress.getCity() : updatedAddress.getCity());

        address.setStreet(updatedAddress.getStreet());
        address.setBuilding(updatedAddress.getBuilding());

        return address;
    }
}
