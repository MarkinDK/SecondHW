package my.learn.secondhw.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.learn.secondhw.model.TicketInfo;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TicketInfoDto {

    private UUID id;

    private BigDecimal price;

    private String currency;

    private Boolean availability;

    private UUID attractionId;

    @JsonBackReference
    private AttractionDto attractionDto;

    public TicketInfoDto(TicketInfo ticketInfo) {
        this.id = ticketInfo.getId();
        this.price = ticketInfo.getPrice();
        this.currency = ticketInfo.getCurrency();
        this.availability = ticketInfo.getAvailability();
        this.attractionDto = ticketInfo.getAttraction() == null ? null : new AttractionDto(ticketInfo.getAttraction());
        this.attractionId = ticketInfo.getAttraction() == null ? null : ticketInfo.getAttraction().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketInfoDto that = (TicketInfoDto) o;
        return Objects.equals(id, that.id) && Objects.equals(price, that.price) && Objects.equals(currency, that.currency) && Objects.equals(availability, that.availability) && Objects.equals(attractionId, that.attractionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, currency, availability, attractionId);
    }
}
