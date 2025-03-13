package my.learn.secondhw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.model.ServiceType;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AttrServDto {

    private UUID id;

    private String name;

    private String description;

    private ServiceType serviceType;

    public AttrServDto(AttractionService as) {
        this.id = as.getId();
        this.name = as.getName();
        this.description = as.getDescription();
        this.serviceType = as.getServiceType();
    }
}