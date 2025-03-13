package my.learn.secondhw.service.attractionservice;


import jakarta.persistence.EntityNotFoundException;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.repository.AttrServRepository;
import my.learn.secondhw.repository.AttractionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления AttractionService.
 */
@Service
public class AttrServServiceImpl implements AttrServService {
    private final AttrServRepository attrServRepository;
    private final AttractionRepository attractionRepository;

    public AttrServServiceImpl(AttrServRepository attrServRepository, AttractionRepository attractionRepository) {
        this.attrServRepository = attrServRepository;
        this.attractionRepository = attractionRepository;
    }

    /**
     * Находит все AttractionService.
     *
     * @return List с AttractionService
     */
    @Override
    public List<AttractionService> findAll() {
        return attrServRepository.findAll();
    }

    /**
     * Находит AttractionService по идентификатору.
     *
     * @param id идентификатор AttractionService
     * @return Optional, содержащий AttractionService, если она найдена
     */
    @Override
    public Optional<AttractionService> findById(UUID id) {
        return attrServRepository.findById(id);
    }

    /**
     * Сохраняет новую услугу Attraction.
     *
     * @param attractionService AttractionService, которую нужно сохранить
     * @return сохраненный AttractionService
     * @throws IllegalArgumentException если имя или тип AttractionService отсутствуют
     */
    @Override
    public AttractionService save(AttractionService attractionService) {
        if (attractionService.getName() == null) {
            throw new IllegalArgumentException("Name is required");
        }
        if (attractionService.getServiceType() == null) {
            throw new IllegalArgumentException("ServiceType is required");
        }
        return attrServRepository.save(attractionService);
    }

    /**
     * Удаляет AttractionService по идентификатору.
     *
     * @param id идентификатор AttractionService, которую нужно удалить
     */
    @Override
    public void deleteById(UUID id) {
        List<Attraction> attractions = attractionRepository.findAttractionsByServiceId(id);
        attractions.forEach(
                a -> a.getAttractionServices().removeIf(
                        attractionService -> attractionService.getId().equals(id)));
        attractionRepository.saveAll(attractions);
        attrServRepository.deleteById(id);
    }

    /**
     * Обновляет информацию об AttractionService Attraction.
     *
     * @param updatedService AttractionService с обновленными данными
     * @throws EntityNotFoundException если AttractionService с указанным идентификатором не найдена
     */
    @Override
    public void update(AttractionService updatedService) {
        Optional<AttractionService> optionalService = attrServRepository.findById(updatedService.getId());
        if (optionalService.isPresent()) {
            AttractionService service = optionalService.get();
            service.setName(updatedService.getName());
            service.setServiceType(updatedService.getServiceType());
            service.setDescription(updatedService.getDescription());
            attrServRepository.save(service);
        } else {
            throw new EntityNotFoundException("No address found with id: " + updatedService.getId());
        }
    }

}
