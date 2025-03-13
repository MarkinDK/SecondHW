package my.learn.secondhw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import my.learn.secondhw.model.Address;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AddressDto {
    private UUID id;
    private String city;
    private String street;
    private Integer building;

    public AddressDto(Address address) {
        this.city = address.getCity();
        this.street = address.getStreet();
        this.building = address.getBuilding();
        this.id = address.getId();
    }
}