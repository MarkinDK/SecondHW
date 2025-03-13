package my.learn.secondhw.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "attractions")
public class Attraction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attraction_id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "attraction_type", nullable = false)
    private AttractionType attractionType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @JsonManagedReference
    @OneToOne(mappedBy = "attraction", cascade = CascadeType.ALL)
    private TicketInfo ticketInfo;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "attraction_service",
            joinColumns = @JoinColumn(name = "attraction_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<AttractionService> attractionServices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attraction that = (Attraction) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && attractionType == that.attractionType && Objects.equals(address, that.address) && Objects.equals(ticketInfo, that.ticketInfo) && Objects.equals(attractionServices, that.attractionServices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, attractionType, address, ticketInfo, attractionServices);
    }
}

