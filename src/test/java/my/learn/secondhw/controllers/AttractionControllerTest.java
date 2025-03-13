package my.learn.secondhw.controllers;

import lombok.SneakyThrows;
import my.learn.secondhw.dto.AttractionDto;
import my.learn.secondhw.model.*;
import my.learn.secondhw.repository.AddressRepository;
import my.learn.secondhw.repository.AttrServRepository;
import my.learn.secondhw.repository.AttractionRepository;
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
import java.util.ArrayList;
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
class AttractionControllerTest {
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
    private AttractionRepository attractionRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AttrServRepository attrServRepository;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void clearDb() {
        attractionRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void getAllAttractionsIT() {
        Attraction a1 = new Attraction();
        a1.setName("A");
        a1.setAttractionType(AttractionType.ARCHEOLOGY);

        Attraction a2 = new Attraction();
        a2.setName("Second");
        a2.setAttractionType(AttractionType.MUSEUM);

        attractionRepository.saveAll(List.of(a1, a2));

        MvcResult result = mvc.perform(get("http://localhost:" + port + "/attraction"))
                .andExpect(status().isOk())
                .andReturn();

        List<AttractionDto> attractionDtoListFromDb = attractionRepository.findAll().stream().map(AttractionDto::new).toList();

        List<AttractionDto> resultAttractionDtoList =
                List.of(new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttractionDto[].class));

        assertThat(resultAttractionDtoList.size()).isEqualTo(attractionDtoListFromDb.size());
        assertTrue(attractionDtoListFromDb.containsAll(resultAttractionDtoList));
    }

    @Test
    @SneakyThrows
    void shouldFindAttractionByIdIT() {
        Attraction attraction = new Attraction();
        attraction.setName("First");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        attractionRepository.save(attraction);

        Optional<Attraction> attractionFromDbOptional = attractionRepository.findAll().stream().findAny();
        if (attractionFromDbOptional.isPresent()) {
            Attraction attractionFromDb = attractionFromDbOptional.get();

            MvcResult result = mvc.perform(get("http://localhost:" + port + "/attraction/" + attractionFromDb.getId()))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn();

            AttractionDto resultAttractionDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttractionDto.class);

            assertEquals(new AttractionDto(attractionFromDb), resultAttractionDto);
        } else {
            throw new RuntimeException("Attraction not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void shouldNotFindAttractionByIdIT() {
        Attraction attraction = new Attraction();
        attraction.setName("First");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        attractionRepository.save(attraction);

        Optional<Attraction> attractionFromDbOptional = attractionRepository.findAll().stream().findAny();
        if (attractionFromDbOptional.isPresent()) {
            Attraction attractionFromDb = attractionFromDbOptional.get();

            attractionRepository.deleteAll();

            mvc.perform(get("http://localhost:" + port + "/attraction/" + attractionFromDb.getId()))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } else {
            throw new RuntimeException("Attraction not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void saveAttractionIT() {
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

        String content = new ObjectMapper().writeValueAsString(attraction);

        MvcResult result = mvc.perform(post("http://localhost:" + port + "/attraction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andReturn();

        Optional<Attraction> attractionFromDbOptional = attractionRepository.findAll().stream().findAny();
        if (attractionFromDbOptional.isPresent()) {
            Attraction attractionFromDb = attractionFromDbOptional.get();

            AttractionDto resultAttractionDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttractionDto.class);

            assertNotNull(resultAttractionDto.getId());
            AttractionDto toCompare = new AttractionDto(attractionFromDb);

            assertEquals(resultAttractionDto, toCompare);
        } else {
            throw new RuntimeException("Attraction not found by repository, problem with repository");
        }
    }


    @Test
    @SneakyThrows
    void deleteAttractionIT() {
        Attraction attraction = new Attraction();

        attraction.setName("Some Attraction");
        attraction.setDescription("Some Attraction Description");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        attractionRepository.save(attraction);

        Optional<Attraction> attractionFromDbOptional = attractionRepository.findAll().stream().findAny();
        if (attractionFromDbOptional.isPresent()) {
            Attraction attractionFromDb = attractionFromDbOptional.get();

            mvc.perform(delete("http://localhost:" + port + "/attraction/" + attractionFromDb.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            Optional<Attraction> emptyAttraction = attractionRepository.findById(attractionFromDb.getId());

            assertTrue(emptyAttraction.isEmpty());
        } else {
            throw new RuntimeException("Attraction not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void updateAttractionIT() {
        Attraction attraction = new Attraction();

        attraction.setName("Some Attraction");
        attraction.setDescription("Some Attraction Description");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);
        attractionRepository.save(attraction);

        Optional<Attraction> attractionFromDbOptional = attractionRepository.findAll().stream().findAny();
        if (attractionFromDbOptional.isPresent()) {
            Attraction attractionToSend = new Attraction();

            attractionToSend.setName("1");
            attractionToSend.setDescription("2");
            attractionToSend.setAttractionType(AttractionType.CONSERVATION);

            String content = new ObjectMapper().writeValueAsString(attractionToSend);

            Attraction oldAttractionFromDb = attractionFromDbOptional.get();

            mvc.perform(put("http://localhost:" + port + "/attraction/" + oldAttractionFromDb.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                            .characterEncoding("utf-8"))
                    .andExpect(status().isOk())
                    .andReturn();

            Optional<Attraction> updatedAttractionFromDbOptional = attractionRepository.findById(oldAttractionFromDb.getId());

            if (updatedAttractionFromDbOptional.isPresent()) {
                Attraction updatedAttractionFromDb = updatedAttractionFromDbOptional.get();

                assertEquals(updatedAttractionFromDb.getId(), oldAttractionFromDb.getId());
                assertEquals(updatedAttractionFromDb.getName(), attractionToSend.getName());
                assertEquals(updatedAttractionFromDb.getDescription(), attractionToSend.getDescription());
                assertEquals(updatedAttractionFromDb.getAttractionType(), attractionToSend.getAttractionType());
            } else {
                throw new RuntimeException("Attraction not found by repository, problem with repository");
            }
        } else {
            throw new RuntimeException("Attraction not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void updateAddressIT() {
        Attraction attraction = new Attraction();

        attraction.setName("Some Attraction");
        attraction.setDescription("Some Attraction Description");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        attractionRepository.save(attraction);

        Address newAddress = new Address();
        newAddress.setStreet("Norfolk st.");
        newAddress.setCity("Innsmuth");
        newAddress.setBuilding(48);
        attraction.setAddress(newAddress);

        addressRepository.save(newAddress);

        Optional<Address> addressFromDbOptional = addressRepository.findAll().stream().findAny();
        Optional<Attraction> attractionFromDbOptional = attractionRepository.findAll().stream().findAny();

        if (attractionFromDbOptional.isPresent() && addressFromDbOptional.isPresent()) {
            Attraction attractionFromDb = attractionFromDbOptional.get();
            Address addressFromDb = addressFromDbOptional.get();

            MvcResult result = mvc.perform(
                            put("http://localhost:" + port + "/attraction/" + attractionFromDb.getId() + "/address")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("addressId", addressFromDb.getId().toString()))
                    .andExpect(status().isOk())
                    .andReturn();

            AttractionDto resultAttractionDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttractionDto.class);

            attractionFromDb.setAddress(addressFromDb);

            assertEquals(new AttractionDto(attractionFromDb), resultAttractionDto);
        } else {
            throw new RuntimeException("Attraction or Address not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void addServiceIT() {
        Attraction attraction = new Attraction();

        attraction.setName("Some Attraction");
        attraction.setDescription("Some Attraction Description");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        attractionRepository.save(attraction);

        AttractionService attractionService = new AttractionService();
        attractionService.setName("Name");
        attractionService.setDescription("Description");
        attractionService.setServiceType(ServiceType.DINING);

        attrServRepository.save(attractionService);

        Optional<AttractionService> serviceFromDbOptional = attrServRepository.findAll().stream().findAny();
        Optional<Attraction> attractionFromDbOptional = attractionRepository.findAll().stream().findAny();

        if (attractionFromDbOptional.isPresent() && serviceFromDbOptional.isPresent()) {
            Attraction attractionFromDb = attractionFromDbOptional.get();
            AttractionService serviceFromDb = serviceFromDbOptional.get();

            MvcResult result = mvc.perform(
                            put("http://localhost:" + port + "/attraction/" + attractionFromDb.getId() + "/service")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .param("serviceId", serviceFromDb.getId().toString()))
                    .andExpect(status().isOk())
                    .andReturn();

            AttractionDto resultAttractionDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttractionDto.class);

            attractionFromDb.setAttractionServices(List.of(serviceFromDb));

            assertEquals(new AttractionDto(attractionFromDb), resultAttractionDto);
        } else {
            throw new RuntimeException("Attraction or Address not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void findAllFromCityIT() {
        Attraction attraction = new Attraction();

        attraction.setName("Some Attraction");
        attraction.setDescription("Some Attraction Description");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        Attraction attraction2 = new Attraction();

        attraction2.setName("Some Attraction");
        attraction2.setDescription("Some Attraction Description");
        attraction2.setAttractionType(AttractionType.ARCHEOLOGY);

        Address address = new Address();
        address.setStreet("Norfolk st.");
        address.setCity("Innsmuth");
        address.setBuilding(48);
        attraction.setAddress(address);
        attraction2.setAddress(address);

        attractionRepository.saveAll(List.of(attraction, attraction2));

        MvcResult result = mvc.perform(
                        get("http://localhost:" + port + "/attraction/city")
                                .param("city", address.getCity()))
                .andExpect(status().isOk())
                .andReturn();

        List<AttractionDto> attractionDtoListFromDb = attractionRepository.findAll().stream().map(AttractionDto::new).toList();

        List<AttractionDto> resultAttractionDtoList =
                List.of(new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttractionDto[].class));

        assertThat(resultAttractionDtoList.size()).isEqualTo(attractionDtoListFromDb.size());
        assertTrue(attractionDtoListFromDb.containsAll(resultAttractionDtoList));

    }

    @Test
    @SneakyThrows
    void findByNameContainingIT() {
        Attraction attraction = new Attraction();
        attraction.setName("AbCdEf");
        attraction.setAttractionType(AttractionType.ARCHEOLOGY);

        Attraction attraction2 = new Attraction();
        attraction2.setName("BcDe");
        attraction2.setAttractionType(AttractionType.MUSEUM);

        Attraction attraction3 = new Attraction();
        attraction3.setName("ZZZZZ");
        attraction3.setAttractionType(AttractionType.PARK);

        attractionRepository.saveAll(List.of(attraction, attraction2));

        MvcResult result = mvc.perform(
                        get("http://localhost:" + port + "/attraction/name")
                                .param("name", "cd"))
                .andExpect(status().isOk())
                .andReturn();

        List<AttractionDto> attractionDtoListFromDb =
                attractionRepository.findAllByNameContainingIgnoreCase("cd").stream().map(AttractionDto::new).toList();

        List<AttractionDto> resultAttractionDtoList =
                List.of(new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttractionDto[].class));

        assertThat(resultAttractionDtoList.size()).isEqualTo(attractionDtoListFromDb.size());
        assertTrue(attractionDtoListFromDb.containsAll(resultAttractionDtoList));

    }

    @Test
    @SneakyThrows
    void findByServiceNameIT() {
        AttractionService attractionService = new AttractionService();
        attractionService.setName("Name");
        attractionService.setDescription("Description");
        attractionService.setServiceType(ServiceType.DINING);
        List<AttractionService> serviceList = List.of(attractionService);

        Attraction a1 = new Attraction();
        a1.setName("1");
        a1.setAttractionType(AttractionType.ARCHEOLOGY);
        a1.setAttractionServices(serviceList);

        Attraction a2 = new Attraction();
        a2.setName("2");
        a2.setAttractionType(AttractionType.MUSEUM);
        a1.setAttractionServices(serviceList);

        Attraction a3 = new Attraction();
        a3.setName("3");
        a3.setAttractionType(AttractionType.PARK);
        a1.setAttractionServices(serviceList);

        attractionRepository.saveAll(List.of(a1, a2));

        MvcResult result = mvc.perform(
                        get("http://localhost:" + port + "/attraction/service")
                                .param("serviceName", attractionService.getName()))
                .andExpect(status().isOk())
                .andReturn();

        List<AttractionDto> attractionDtoListFromDb =
                attractionRepository
                        .findAllByServiceName(attractionService.getName())
                        .stream()
                        .map(AttractionDto::new)
                        .toList();

        List<AttractionDto> resultAttractionDtoList =
                List.of(new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttractionDto[].class));

        assertThat(resultAttractionDtoList.size()).isEqualTo(attractionDtoListFromDb.size());
        assertTrue(attractionDtoListFromDb.containsAll(resultAttractionDtoList));

    }
}