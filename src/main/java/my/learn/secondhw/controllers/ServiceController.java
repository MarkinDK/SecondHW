package my.learn.secondhw.controllers;

import my.learn.secondhw.dto.AttrServDto;
import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.service.attractionservice.AttrServService;
import my.learn.secondhw.service.attractionservice.AttrServServiceImpl;
import my.learn.secondhw.utils.converter.AttractionServiceConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Контроллер для управления AttractionService, связанными с Attraction.
 */
@RestController
@RequestMapping("/service")
public class ServiceController {
    private final AttrServService attrServService;

    /**
     * Конструктор контроллера AttractionService.
     *
     * @param attrServService сервис для обработки AttractionService
     */
    public ServiceController(AttrServServiceImpl attrServService) {
        this.attrServService = attrServService;
    }

    /**
     * Получает список всех AttractionService.
     *
     * @return ResponseEntity содержащий список объектов AttrServDto
     */
    @GetMapping
    public ResponseEntity<?> getAllServices() {
        return ResponseEntity.ok(
                attrServService
                        .findAll()
                        .stream()
                        .map(AttrServDto::new)
                        .collect(Collectors.toList()));
    }

    /**
     * Получает AttractionService по её идентификатору.
     *
     * @param id уникальный идентификатор AttractionService в строке запроса
     * @return ResponseEntity содержащий объект AttrServDto, если AttractionService найден;
     * иначе возвращает 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable("id") UUID id) {
        Optional<AttractionService> service = attrServService.findById(id);
        if (service.isPresent()) {
            return ResponseEntity.ok(new AttrServDto(service.get()));
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Сохраняет новую AttractionService.
     *
     * @param AttrServDto объект AttrServDto, который необходимо сохранить
     * @return ResponseEntity с AttrServDto и статусом 201 Created
     */
    @PostMapping
    public ResponseEntity<?> saveService(@RequestBody AttrServDto AttrServDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AttrServDto(attrServService.save(AttractionServiceConverter.dtoToAttractionService(AttrServDto))));

    }

    /**
     * Удаляет AttractionService по идентификатору.
     *
     * @param id уникальный идентификатор AttractionService в строке запроса
     * @return ResponseEntity с статусом 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable UUID id) {
        attrServService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет существующий AttractionService.
     *
     * @param id          уникальный идентификатор AttractionService в строке запроса
     * @param AttrServDto объект AttrServDto с новыми данными
     * @return ResponseEntity с статусом 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable UUID id, @RequestBody AttrServDto AttrServDto) {
        AttrServDto.setId(id);
        attrServService.update(AttractionServiceConverter.dtoToAttractionService(AttrServDto));
        return ResponseEntity.ok().build();
    }
}
