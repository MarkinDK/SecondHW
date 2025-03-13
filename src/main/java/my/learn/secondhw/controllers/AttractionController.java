package my.learn.secondhw.controllers;

import my.learn.secondhw.dto.AttractionDto;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.service.attraction.AttractionHandlingService;
import my.learn.secondhw.utils.converter.AttractionConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Контроллер для управления Attraction.
 */
@RestController
@RequestMapping("/attraction")
public class AttractionController {
    private final AttractionHandlingService attractionHandlingService;

    /**
     * Конструктор контроллера Attraction.
     *
     * @param attractionHandlingService сервис для обработки Attraction
     */
    public AttractionController(final AttractionHandlingService attractionHandlingService) {
        this.attractionHandlingService = attractionHandlingService;
    }

    /**
     * Получает список всех Attraction.
     *
     * @return ResponseEntity содержащий список объектов AttractionDto
     */
    @GetMapping
    public ResponseEntity<?> getAllAttractions() {

        return ResponseEntity.ok(
                attractionHandlingService
                        .findAll()
                        .stream()
                        .map(AttractionDto::new)
                        .collect(Collectors.toList()));

    }

    /**
     * Получает Attraction по идентификатору.
     *
     * @param id уникальный идентификатор Attraction в строке запроса
     * @return ResponseEntity содержащий объект AttractionDto, если Attraction найден;
     * иначе возвращает 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAttractionById(@PathVariable("id") UUID id) {
        Optional<Attraction> attraction = attractionHandlingService.findById(id);
        if (attraction.isPresent()) {
            return ResponseEntity.ok(new AttractionDto(attraction.get()));
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Сохраняет новый Attraction.
     *
     * @param attractionDto объект AttractionDto, который необходимо сохранить
     * @return ResponseEntity с сохранённым Attraction в виде объекта AttractionDto
     */
    @PostMapping
    public ResponseEntity<?> saveAttraction(@RequestBody AttractionDto attractionDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AttractionDto(attractionHandlingService.save(AttractionConverter.dtoToAttraction(attractionDto))));
    }

    /**
     * Удаляет Attraction по идентификатору.
     *
     * @param id уникальный идентификатор Attraction в строке запроса
     * @return ResponseEntity с статусом 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttraction(@PathVariable UUID id) {
        attractionHandlingService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет существующий Attraction.
     *
     * @param id            уникальный идентификатор Attraction в строке запроса
     * @param attractionDto объект AttractionDto с новыми данными
     * @return ResponseEntity с статусом 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAttraction(@PathVariable String id, @RequestBody AttractionDto attractionDto) {
        attractionDto.setId(UUID.fromString(id));
        attractionHandlingService.update(AttractionConverter.dtoToAttraction(attractionDto));
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет Address объекта Attraction.
     *
     * @param id        уникальный идентификатор Attraction в строке запроса
     * @param addressId уникальный идентификатор нового Address, параметр запроса
     * @return ResponseEntity с обновлённой Attraction в виде объекта AttractionDto
     */
    @PutMapping("/{id}/address")
    public ResponseEntity<?> updateAddress(@PathVariable UUID id, @RequestParam(name = "addressId") UUID addressId) {

        return ResponseEntity.ok(new AttractionDto(attractionHandlingService.updateAddress(id, addressId)));
    }

    /**
     * Добавляет Service к Attraction.
     *
     * @param id        уникальный идентификатор Attraction в строке запроса
     * @param serviceId уникальный идентификатор Service, параметр запроса
     * @return ResponseEntity с обновлённой Attraction
     */
    @PutMapping("/{id}/service")
    public ResponseEntity<?> addService(@PathVariable UUID id, @RequestParam(name = "serviceId") UUID serviceId) {

        return ResponseEntity.ok(new AttractionDto(attractionHandlingService.addService(id, serviceId)));
    }

    /**
     * Получает все достопримечательности из указанного города.
     *
     * @param city название города, параметр запроса
     * @return ResponseEntity содержащий список Attraction из указанного города
     */
    @GetMapping("/city")
    public ResponseEntity<?> findAllFromCity(@RequestParam String city) {
        return ResponseEntity.ok(
                attractionHandlingService
                        .findAllByCity(city)
                        .stream()
                        .map(AttractionDto::new)
                        .collect(Collectors.toList()));
    }

    /**
     * Получает Attraction по их названию.
     *
     * @param name часть названия Attraction, параметр запроса
     * @return ResponseEntity содержащий список Attraction,
     * названия которых содержат указанный текст
     */
    @GetMapping("/name")
    public ResponseEntity<?> findByNameContaining(@RequestParam String name) {
        return ResponseEntity.ok(
                attractionHandlingService
                        .findAllByNameContaining(name)
                        .stream()
                        .map(AttractionDto::new)
                        .collect(Collectors.toList()));
    }

    /**
     * Получает все достопримечательности по названию Service.
     *
     * @param serviceName название Service, параметр запроса
     * @return ResponseEntity содержащий список AttractionDto,
     * предлагающих указанный Service
     */
    @GetMapping("/service")
    public ResponseEntity<?> findAllByServiceName(@RequestParam String serviceName) {
        return ResponseEntity.ok(
                attractionHandlingService
                        .findAllByService(serviceName)
                        .stream()
                        .map(AttractionDto::new)
                        .collect(Collectors.toList()));
    }
}
