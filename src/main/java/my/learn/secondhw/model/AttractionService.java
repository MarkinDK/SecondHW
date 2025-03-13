package my.learn.secondhw.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "services")
public class AttractionService {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "service_id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)

    private ServiceType serviceType;

    public AttractionService(String name, String description, ServiceType serviceType) {
        this.name = name;
        this.description = description;
        this.serviceType = serviceType;
    }
}

