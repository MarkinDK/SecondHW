package my.learn.secondhw.service.attraction;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import my.learn.secondhw.model.*;
import my.learn.secondhw.repository.AddressRepository;
import my.learn.secondhw.repository.AttrServRepository;
import my.learn.secondhw.repository.AttractionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для обработки Attraction.
 */
@Service
public class AttractionHandlingServiceImpl implements AttractionHandlingService {
    private final AttractionRepository attractionRepository;
    private final AddressRepository addressRepository;
    private final AttrServRepository attrServRepository;

    /**
     * Конструктор для инициализации AttractionHandlingServiceImpl.
     *
     * @param attractionRepository репозиторий для работы с Attraction
     * @param addressRepository    репозиторий для работы с Address
     * @param attrServRepository   репозиторий для работы с AttractionService
     */
    public AttractionHandlingServiceImpl(AttractionRepository attractionRepository, AddressRepository addressRepository, AttrServRepository attrServRepository) {
        this.attractionRepository = attractionRepository;
        this.addressRepository = addressRepository;
        this.attrServRepository = attrServRepository;
    }
    /**
     * Сохраняет в базу данных набор всех сущностей
     */
    @PostConstruct
    private void init() {
        Attraction attraction = new Attraction();

        attraction.setName("Some Attraction");
        attraction.setDescription("Some Attraction Description");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        Address address = new Address();
        address.setStreet("Norfolk st.");
        address.setCity("Innsmuth");
        address.setBuilding(48);
        attraction.setAddress(address);

        AttractionService as = new AttractionService();
        as.setName("Some name");
        as.setDescription("Some description");
        as.setServiceType(ServiceType.DINING);
        List<AttractionService> services = new ArrayList<>();
        services.add(as);
        attraction.setAttractionServices(services);

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setCurrency("USD");
        ticketInfo.setPrice(new BigDecimal("300.00"));
        ticketInfo.setAvailability(true);
        attraction.setTicketInfo(ticketInfo);
        ticketInfo.setAttraction(attraction);

        attractionRepository.save(attraction);
    }

    /**
     * Находит все Attraction.
     *
     * @return List со всеми Attraction
     */
    @Override
    public List<Attraction> findAll() {
        return attractionRepository.findAll();
    }

    /**
     * Находит Attraction по идентификатору.
     *
     * @param id идентификатор Attraction
     * @return Optional с Attraction
     */
    @Override
    public Optional<Attraction> findById(UUID id) {
        return attractionRepository.findById(id);
    }

    /**
     * Находит все Attraction по городу.
     *
     * @param city город, в котором находятся Attraction
     * @return List с Attraction в указанном городе
     */
    @Override
    public List<Attraction> findAllByCity(String city) {
        return attractionRepository.findAllByCity(city);
    }

    /**
     * Находит все Attraction, содержащие указанный текст в названии.
     *
     * @param name текст для поиска в названиях Attraction
     * @return List с Attraction, содержащими указанный текст в названии
     */
    @Override
    public List<Attraction> findAllByNameContaining(String name) {
        System.out.println(name);
        return attractionRepository.findAllByNameContainingIgnoreCase(name.toLowerCase());
    }

    /**
     * Находит все Attraction по названию AttractionService.
     *
     * @param serviceName название AttractionService
     * @return List с Attraction, связанными с AttractionService
     */
    @Override
    public List<Attraction> findAllByService(String serviceName) {
        return attractionRepository.findAllByServiceName(serviceName);
    }

    /**
     * Сохраняет новый Attraction.
     *
     * @param attraction Attraction для сохранения
     * @return сохраненный Attraction
     * @throws IllegalArgumentException если Attraction с таким идентификатором уже существует
     */
    @Override
    public Attraction save(Attraction attraction) {
        if (attraction.getId() == null) {
            return attractionRepository.save(attraction);
        } else {
            if (attractionRepository.findById(attraction.getId()).isPresent()) {
                throw new IllegalArgumentException("Attraction with id " + attraction.getId() + " already exists");
            } else {
                attraction.setId(null);
                return attractionRepository.save(attraction);
            }
        }
    }

    /**
     * Удаляет Attraction по идентификатору.
     *
     * @param id идентификатор удаляемого Attraction
     */
    @Override
    public void deleteById(UUID id) {
        attractionRepository.deleteById(id);
    }

    /**
     * Обновляет существующий Attraction.
     *
     * @param updatedAttraction Attraction с обновленными данными
     * @throws EntityNotFoundException если Attraction с указанным идентификатором не найден
     */
    @Override
    public void update(Attraction updatedAttraction) {
        if (!attractionRepository.existsById(updatedAttraction.getId())) {
            throw new EntityNotFoundException("No attraction with id " + updatedAttraction.getId());
        }
        attractionRepository.save(updatedAttraction);
    }

    /**
     * Обновляет Address объекта Attraction.
     *
     * @param attractionId идентификатор Attraction, который нужно обновить
     * @param addressId    идентификатор нового Address
     * @return обновленный Attraction
     * @throws EntityNotFoundException если Attraction или Address с указанными идентификаторами не найдены
     */
    @Override
    public Attraction updateAddress(UUID attractionId, UUID addressId) {
        Optional<Attraction> optionalAttraction = attractionRepository.findById(attractionId);
        if (optionalAttraction.isPresent()) {
            Optional<Address> optionalAddress = addressRepository.findById(addressId);
            if (optionalAddress.isPresent()) {
                Attraction attraction = optionalAttraction.get();
                Address address = optionalAddress.get();
                attraction.setAddress(address);
                return attractionRepository.save(attraction);
            } else {
                throw new EntityNotFoundException("No address with id " + addressId);
            }
        } else {
            throw new EntityNotFoundException("No attraction with id " + attractionId);
        }

    }

    /**
     * Добавляет AttractionService к Attraction.
     *
     * @param attractionId идентификатор Attraction, к которому добавляется услуга
     * @param serviceId    идентификатор AttractionService, которую нужно добавить
     * @throws EntityNotFoundException если Attraction или AttractionService с указанными идентификаторами не найдены
     */
    @Override
    public Attraction addService(UUID attractionId, UUID serviceId) {
        Optional<Attraction> optionalAttraction = attractionRepository.findById(attractionId);
        if (optionalAttraction.isPresent()) {
            Optional<AttractionService> optionalService = attrServRepository.findById(serviceId);
            if (optionalService.isPresent()) {
                Attraction attraction = optionalAttraction.get();
                AttractionService service = optionalService.get();
                if (!attraction.getAttractionServices().contains(service)) {
                    attraction.getAttractionServices().add(service);
                }
                return attractionRepository.save(attraction);
            } else {
                throw new EntityNotFoundException("No service with id " + serviceId);
            }
        } else {
            throw new EntityNotFoundException("No attraction with id " + attractionId);
        }
    }

}
