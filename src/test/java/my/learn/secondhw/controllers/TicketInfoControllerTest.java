package my.learn.secondhw.controllers;

import lombok.SneakyThrows;
import my.learn.secondhw.dto.TicketInfoDto;
import my.learn.secondhw.model.Attraction;
import my.learn.secondhw.model.AttractionType;
import my.learn.secondhw.model.TicketInfo;
import my.learn.secondhw.repository.AttractionRepository;
import my.learn.secondhw.repository.TicketInfoRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(value = {
        "classpath:application-test.properties"
})
@AutoConfigureMockMvc
public class TicketInfoControllerTest {
    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("secondhw")
            .withUsername("root")
            .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AttractionRepository attractionRepository;
    @Autowired
    private TicketInfoRepository ticketInfoRepository;

    @BeforeEach
    void clearDB() {
        attractionRepository.deleteAll();
        ticketInfoRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void getAllTicketInfosIT() {
        Attraction attraction1 = new Attraction();
        attraction1.setName("attraction1");
        attraction1.setAttractionType(AttractionType.ARCHEOLOGY);

        TicketInfo ticketInfo1 = new TicketInfo();
        ticketInfo1.setCurrency("EUR");
        ticketInfo1.setPrice(new BigDecimal("100.00"));
        ticketInfo1.setAvailability(false);
        attraction1.setTicketInfo(ticketInfo1);
        ticketInfo1.setAttraction(attraction1);

        Attraction attraction2 = new Attraction();
        attraction2.setName("attraction2");
        attraction2.setAttractionType(AttractionType.MUSEUM);

        TicketInfo ticketInfo2 = new TicketInfo();
        ticketInfo2.setCurrency("USD");
        ticketInfo2.setPrice(new BigDecimal("300.00"));
        ticketInfo2.setAvailability(true);
        attraction2.setTicketInfo(ticketInfo2);
        ticketInfo2.setAttraction(attraction2);

        attractionRepository.saveAll(List.of(attraction1, attraction2));


        List<TicketInfoDto> ticketInfoDtoFromDb = ticketInfoRepository.findAll().stream().map(TicketInfoDto::new).toList();

        MvcResult result = mvc.perform(get("http://localhost:" + port + "/ticketinfo"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<TicketInfoDto> resultTicketInfoDtoList =
                List.of(new ObjectMapper().readValue(result.getResponse().getContentAsString(), TicketInfoDto[].class));

        ticketInfoDtoFromDb.forEach(t -> t.setAttractionDto(null));

        assertThat(resultTicketInfoDtoList.size()).isEqualTo(ticketInfoDtoFromDb.size());
        assertTrue(ticketInfoDtoFromDb.containsAll(resultTicketInfoDtoList));

    }

    @Test
    @SneakyThrows
    void shouldFindTicketInfoByIdIT() {
        Attraction attraction = new Attraction();
        attraction.setName("attraction1");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setCurrency("EUR");
        ticketInfo.setPrice(new BigDecimal("100.00"));
        ticketInfo.setAvailability(false);
        attraction.setTicketInfo(ticketInfo);
        ticketInfo.setAttraction(attraction);

        attractionRepository.save(attraction);

        Optional<TicketInfo> ticketInfoFromDbOptional = ticketInfoRepository.findById(ticketInfo.getId());

        if (ticketInfoFromDbOptional.isPresent()) {
            TicketInfo ticketInfoFromDB = ticketInfoFromDbOptional.get();
            ticketInfoFromDB.setAttractionId(null);

            ticketInfoRepository.deleteAll();

            MvcResult result = mvc
                    .perform(get("http://localhost:" + port + "/ticketinfo/" + ticketInfoFromDB.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            TicketInfoDto resultTicketInfo =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), TicketInfoDto.class);

            assertEquals(new TicketInfoDto(ticketInfoFromDB), resultTicketInfo);

        } else {
            throw new RuntimeException("TicketInfo not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void shouldNotFindTicketInfoByIdIT() {
        Attraction attraction = new Attraction();
        attraction.setName("attraction1");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setCurrency("EUR");
        ticketInfo.setPrice(new BigDecimal("100.00"));
        ticketInfo.setAvailability(false);
        attraction.setTicketInfo(ticketInfo);
        ticketInfo.setAttraction(attraction);

        attractionRepository.save(attraction);

        Optional<TicketInfo> ticketInfoFromDbOptional = ticketInfoRepository.findById(ticketInfo.getId());

        if (ticketInfoFromDbOptional.isPresent()) {
            TicketInfo ticketInfoFromDB = ticketInfoFromDbOptional.get();

            attractionRepository.deleteAll();

            MvcResult result = mvc.perform(get("http://localhost:" + port + "/ticketinfo/" + ticketInfoFromDB.getId()))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } else {
            throw new RuntimeException("TicketInfo not found by repository, problem with repository");
        }
    }


    @Test
    @SneakyThrows
    void saveTicketInfoIT() {
        Attraction attraction = new Attraction();
        attraction.setName("attraction");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        attractionRepository.save(attraction);

        Optional<Attraction> attractionFromDbOptional = attractionRepository.findAll().stream().findAny();
        Attraction attractionFromDb =
                attractionFromDbOptional
                        .orElseThrow(() -> new RuntimeException("TicketInfo not found by repository, problem with repository"));

        TicketInfoDto ticketInfoDto = new TicketInfoDto();
        ticketInfoDto.setCurrency("EUR");
        ticketInfoDto.setPrice(new BigDecimal("100.00"));
        ticketInfoDto.setAvailability(false);
        ticketInfoDto.setAttractionId(attractionFromDb.getId());

        String content = new ObjectMapper().writeValueAsString(ticketInfoDto);

        MvcResult result = mvc.perform(post("http://localhost:" + port + "/ticketinfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .characterEncoding("utf-8"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Optional<TicketInfo> ticketInfoFromDbOptional = ticketInfoRepository.findAll().stream().findAny();
        if (ticketInfoFromDbOptional.isPresent()) {
            TicketInfo ticketInfoFromDb = ticketInfoFromDbOptional.get();

            TicketInfoDto resultTicketInfoDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), TicketInfoDto.class);

            TicketInfoDto ticketInfoFromDbDto = new TicketInfoDto(ticketInfoFromDb);
            ticketInfoFromDbDto.setAttractionDto(null);

            assertNotNull(resultTicketInfoDto.getId());
            assertEquals(resultTicketInfoDto, ticketInfoFromDbDto);
        } else {
            throw new RuntimeException("TicketInfo not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void deleteByIdIT() {
        Attraction attraction = new Attraction();
        attraction.setName("attraction1");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setCurrency("EUR");
        ticketInfo.setPrice(new BigDecimal("100.00"));
        ticketInfo.setAvailability(false);
        attraction.setTicketInfo(ticketInfo);
        ticketInfo.setAttraction(attraction);

        attractionRepository.save(attraction);

        Optional<TicketInfo> ticketInfoFromDbOptional = ticketInfoRepository.findAll().stream().findAny();
        if (ticketInfoFromDbOptional.isPresent()) {
            TicketInfo ticketInfoFromDb = ticketInfoFromDbOptional.get();

            mvc.perform(delete("http://localhost:" + port + "/ticketinfo/" + ticketInfoFromDb.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            Optional<TicketInfo> emptyTicketInfo = ticketInfoRepository.findById(ticketInfoFromDb.getId());

            assertTrue(emptyTicketInfo.isEmpty());
        } else {
            throw new RuntimeException("TicketInfo not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void updateAddressIT() {
        Attraction attraction = new Attraction();
        attraction.setName("attraction1");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setCurrency("EUR");
        ticketInfo.setPrice(new BigDecimal("100.00"));
        ticketInfo.setAvailability(false);
        attraction.setTicketInfo(ticketInfo);
        ticketInfo.setAttraction(attraction);

        attractionRepository.save(attraction);

        Optional<TicketInfo> ticketInfoFromDbOptional = ticketInfoRepository.findAll().stream().findAny();
        if (ticketInfoFromDbOptional.isPresent()) {
            TicketInfo ticketInfoToSend = new TicketInfo();
            ticketInfoToSend.setCurrency("USD");
            ticketInfoToSend.setPrice(new BigDecimal("200.00"));
            ticketInfoToSend.setAvailability(true);

            TicketInfo ticketInfoFromDb = ticketInfoFromDbOptional.get();

            String content = new ObjectMapper().writeValueAsString(ticketInfoToSend);

            mvc.perform(put("http://localhost:" + port + "/ticketinfo/" + ticketInfoFromDb.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                            .characterEncoding("utf-8"))
                    .andExpect(status().isOk())
                    .andReturn();

            Optional<TicketInfo> updatedTicketInfoOptional = ticketInfoRepository.findById(ticketInfoFromDb.getId());
            if (updatedTicketInfoOptional.isPresent()) {
                TicketInfo updatedTicketInfo = updatedTicketInfoOptional.get();
                assertEquals(ticketInfoToSend.getPrice(), updatedTicketInfo.getPrice());
                assertEquals(ticketInfoToSend.getCurrency(), updatedTicketInfo.getCurrency());
                assertEquals(ticketInfoToSend.getAvailability(), updatedTicketInfo.getAvailability());
            } else {
                throw new RuntimeException("TicketInfo not found by repository, problem with repository");
            }
        } else {
            throw new RuntimeException("TicketInfo not found by repository, problem with repository");
        }
    }
}
