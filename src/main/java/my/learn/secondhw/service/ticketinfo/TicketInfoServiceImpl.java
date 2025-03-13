package my.learn.secondhw.service.ticketinfo;

import jakarta.persistence.EntityNotFoundException;
import my.learn.secondhw.exception.TicketInfoExistsException;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.model.TicketInfo;
import my.learn.secondhw.repository.AttractionRepository;
import my.learn.secondhw.repository.TicketInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления TicketInfo.
 */
@Service
public class TicketInfoServiceImpl implements TicketInfoService {
    private final TicketInfoRepository ticketInfoRepository;
    private final AttractionRepository attractionRepository;

    public TicketInfoServiceImpl(TicketInfoRepository ticketInfoRepository, AttractionRepository attractionRepository) {
        this.ticketInfoRepository = ticketInfoRepository;
        this.attractionRepository = attractionRepository;
    }

    /**
     * Находит все TicketInfo.
     *
     * @return List со всеми TicketInfo
     */
    @Override
    public List<TicketInfo> findAll() {
        return ticketInfoRepository.findAll();
    }

    /**
     * Находит TicketInfo по идентификатору.
     *
     * @param id идентификатор TicketInfo
     * @return Optional, содержащий TicketInfo, если она найдена
     */
    @Override
    public Optional<TicketInfo> findById(UUID id) {
        return ticketInfoRepository.findById(id);
    }

    /**
     * Сохраняет TicketInfo.
     *
     * @param ticketInfo TicketInfo, которую нужно сохранить
     * @return сохраненная TicketInfo
     * @throws EntityNotFoundException   если Attraction с указанным идентификатором не найден
     * @throws TicketInfoExistsException если TicketInfo уже существует для данного Attraction
     */
    @Override
    public TicketInfo save(TicketInfo ticketInfo) {
        Attraction attraction
                = attractionRepository
                .findById(ticketInfo.getAttractionId())
                .orElseThrow(() ->
                        new EntityNotFoundException("No attraction with id: " + ticketInfo.getAttractionId()));

        if (attraction.getTicketInfo() != null) {
            throw new TicketInfoExistsException("Ticket info already exists for attraction with id: " + attraction.getId());
        }
        ticketInfo.setAttraction(attraction);
        attraction.setTicketInfo(ticketInfo);

        return ticketInfoRepository.save(ticketInfo);
    }

    /**
     * Удаляет TicketInfo по идентификатору.
     *
     * @param id идентификатор TicketInfo, которую нужно удалить
     */
    @Override
    public void deleteById(UUID id) {
        Optional<Attraction> attraction = attractionRepository.findByTicketInfoId(id);
        if (attraction.isPresent()) {
            Attraction a = attraction.get();
            a.setTicketInfo(null);
            attractionRepository.save(a);
            ticketInfoRepository.deleteById(id);
        }
    }

    /**
     * Обновляет TicketInfo.
     *
     * @param updatedTicketInfo TicketInfo с обновленными данными
     * @throws EntityNotFoundException если TicketInfo с указанным идентификатором не найдена
     */
    @Override
    public void update(TicketInfo updatedTicketInfo) {
        ticketInfoRepository.findById(updatedTicketInfo.getId()).ifPresentOrElse(
                ticketInfo -> ticketInfoRepository.updateById(
                        updatedTicketInfo.getPrice(),
                        updatedTicketInfo.getCurrency(),
                        updatedTicketInfo.getAvailability(),
                        updatedTicketInfo.getId()
                ),
                () -> {
                    throw new EntityNotFoundException("No ticket info found with id: " + updatedTicketInfo.getId());
                });
    }
}
