package my.learn.secondhw.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.learn.secondhw.model.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class AttractionDto {
    private UUID id;

    private String name;

    private String description;

    private AttractionType attractionType;

    @JsonProperty(value = "address")
    private AddressDto addressDto;

    @JsonManagedReference
    @JsonProperty(value = "ticketInfo")
    private TicketInfoDto ticketInfoDto;

    @JsonProperty(value = "attractionServices")
    private List<AttrServDto> attrServDtoList;

    private AddressDto checkAddress(Address address) {
        return address == null ? null : new AddressDto(address);
    }

    private TicketInfoDto checkTicketInfo(TicketInfo ticketInfo) {
        if (ticketInfo != null) {
            TicketInfoDto ticketInfoDto = new TicketInfoDto();
            ticketInfoDto.setId(ticketInfo.getId());
            ticketInfoDto.setPrice(ticketInfo.getPrice());
            ticketInfoDto.setCurrency(ticketInfo.getCurrency());
            ticketInfoDto.setAvailability(ticketInfo.getAvailability());
            ticketInfoDto.setAttractionId(ticketInfo.getAttraction().getId());
            ticketInfoDto.setAttractionDto(this);
            return ticketInfoDto;
        } else {
            return null;
        }
    }

    private List<AttrServDto> checkAttrServices(List<AttractionService> attrServList) {
        return attrServList == null ? null : attrServList.stream().map(AttrServDto::new).collect(Collectors.toList());
    }

    public AttractionDto(Attraction attraction) {
        this.id = attraction.getId();
        this.name = attraction.getName();
        this.description = attraction.getDescription();
        this.attractionType = attraction.getAttractionType();

        this.addressDto = checkAddress(attraction.getAddress());
        this.ticketInfoDto = checkTicketInfo(attraction.getTicketInfo());
        this.attrServDtoList = checkAttrServices(attraction.getAttractionServices());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttractionDto that = (AttractionDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && attractionType == that.attractionType && Objects.equals(addressDto, that.addressDto) && Objects.equals(ticketInfoDto, that.ticketInfoDto) && Objects.equals(attrServDtoList, that.attrServDtoList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, attractionType, addressDto, ticketInfoDto, attrServDtoList);
    }
}
