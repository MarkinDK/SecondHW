package my.learn.secondhw.controllers;

import my.learn.secondhw.dto.AddressDto;
import my.learn.secondhw.model.Address;
import my.learn.secondhw.service.address.AddressService;
import my.learn.secondhw.utils.converter.AddressConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Контроллер для управления Address.
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    private final AddressService addressService;

    /**
     * Конструктор контроллера Address.
     *
     * @param addressService сервис для работы с Address
     */
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Получает список всех Address.
     *
     * @return ResponseEntity содержащий список объектов AddressDto
     */
    @GetMapping
    public ResponseEntity<?> getAllAddresses() {
        return ResponseEntity.ok(
                addressService
                        .findAll()
                        .stream()
                        .map(AddressDto::new)
                        .collect(Collectors.toList()));
    }

    /**
     * Получает Address по его идентификатору.
     *
     * @param id уникальный идентификатор Address в строке запроса
     * @return ResponseEntity содержащий объект AddressDto, если Address найден;
     * иначе возвращает 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable("id") UUID id) {
        Optional<Address> address = addressService.findById(id);
        if (address.isPresent()) {
            return ResponseEntity.ok(new AddressDto(address.get()));
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Сохраняет новый Address.
     *
     * @param addressDto объект AddressDto, который нужно сохранить
     * @return ResponseEntity с созданным Address в виде AddressDto и статусом 201 Created
     */
    @PostMapping
    public ResponseEntity<?> saveAddress(@RequestBody AddressDto addressDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AddressDto(addressService.save(AddressConverter.dtoToAddress(addressDto))));
    }

    /**
     * Удаляет Address по его идентификатору.
     *
     * @param id уникальный идентификатор Address в строке запроса
     * @return ResponseEntity с статусом 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable UUID id) {
        addressService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет существующий Address.
     *
     * @param id         уникальный идентификатор Address в строке запроса
     * @param addressDto объект AddressDto с новыми данными Address
     * @return ResponseEntity с статусом 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable String id, @RequestBody AddressDto addressDto) {
        addressDto.setId(UUID.fromString(id));
        addressService.update(AddressConverter.dtoToAddress(addressDto));
        return ResponseEntity.ok().build();
    }
}
