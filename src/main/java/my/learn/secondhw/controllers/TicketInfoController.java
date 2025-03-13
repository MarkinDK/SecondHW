package my.learn.secondhw.controllers;

import my.learn.secondhw.dto.TicketInfoDto;
import my.learn.secondhw.model.TicketInfo;
import my.learn.secondhw.service.ticketinfo.TicketInfoService;
import my.learn.secondhw.utils.converter.TicketInfoConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для управления TicketInfo.
 */
@RestController
@RequestMapping("/ticketinfo")
public class TicketInfoController {
    private final TicketInfoService ticketInfoService;

    /**
     * Конструктор контроллера информации о билетах.
     *
     * @param ticketInfoService сервис для обработки TicketInfo
     */
    public TicketInfoController(TicketInfoService ticketInfoService) {
        this.ticketInfoService = ticketInfoService;
    }

    /**
     * Получает список всех TicketInfo.
     *
     * @return ResponseEntity содержащий список объектов TicketInfoDto
     */
    @GetMapping
    public ResponseEntity<?> getAllTicketInfos() {
        List<TicketInfo> ticketInfoList = ticketInfoService.findAll();
        List<TicketInfoDto> ticketInfoDtoList = new ArrayList<>();
        ticketInfoList.forEach(ticketInfo ->
                ticketInfoDtoList.add(new TicketInfoDto(ticketInfo)));
        return ResponseEntity.ok(ticketInfoDtoList);
    }

    /**
     * Получает TicketInfo по идентификатору.
     *
     * @param id уникальный идентификатор TicketInfo в строке запроса
     * @return ResponseEntity содержащий объект TicketInfoDto, если TicketInfo найден;
     * иначе возвращает 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketInfoById(@PathVariable("id") UUID id) {
        Optional<TicketInfo> ticketInfo = ticketInfoService.findById(id);
        if (ticketInfo.isPresent()) {
            return ResponseEntity.ok(new TicketInfoDto(ticketInfo.get()));
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Сохраняет TicketInfo.
     *
     * @param ticketInfoDto объект TicketInfoDto, который необходимо сохранить
     * @return ResponseEntity с сохранённой информацией о билете в виде TicketInfoDto
     */
    @PostMapping
    public ResponseEntity<?> saveTicketInfo(@RequestBody TicketInfoDto ticketInfoDto) {
        return ResponseEntity.ok(new TicketInfoDto(ticketInfoService.save(TicketInfoConverter.dtoToTicketInfo(ticketInfoDto))));
    }

    /**
     * Удаляет TicketInfo по идентификатору.
     *
     * @param id уникальный идентификатор TicketInfo в строке запроса
     * @return ResponseEntity с статусом 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicketInfo(@PathVariable UUID id) {
        ticketInfoService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет существующий TicketInfo.
     *
     * @param id            уникальный идентификатор TicketInfo в строке запроса
     * @param ticketInfoDto объект TicketInfoDto с новыми данными
     * @return ResponseEntity с статусом 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicketInfo(@PathVariable UUID id, @RequestBody TicketInfoDto ticketInfoDto) {
        ticketInfoDto.setId(id);
        ticketInfoService.update(TicketInfoConverter.dtoToTicketInfo(ticketInfoDto));
        return ResponseEntity.ok().build();
    }

}
