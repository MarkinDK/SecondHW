package my.learn.secondhw.service.ticketinfo;

import my.learn.secondhw.model.TicketInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketInfoService {
    List<TicketInfo> findAll();

    TicketInfo save(TicketInfo ticketInfo);

    void deleteById(UUID id);

    void update(TicketInfo updatedTicketInfo);

    Optional<TicketInfo> findById(UUID id);
}
