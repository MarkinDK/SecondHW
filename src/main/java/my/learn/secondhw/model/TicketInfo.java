package my.learn.secondhw.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

//@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ticket_info")
public class TicketInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ticket_info_id")
    private UUID id;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "availability", nullable = false)
    private Boolean availability;

    @Transient
    @JsonIgnore
    private UUID attractionId;

    @OneToOne
    @JoinColumn(name = "attraction_id", nullable = false)
    @JsonBackReference
    private Attraction attraction;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketInfo that = (TicketInfo) o;
        return Objects.equals(id, that.id) && Objects.equals(price, that.price) && Objects.equals(currency, that.currency) && Objects.equals(availability, that.availability) && Objects.equals(attractionId, that.attractionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, currency, availability, attractionId);
    }
}
