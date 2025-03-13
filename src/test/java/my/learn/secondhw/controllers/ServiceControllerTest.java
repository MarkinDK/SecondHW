package my.learn.secondhw.controllers;

import lombok.SneakyThrows;
import my.learn.secondhw.dto.AttrServDto;
import my.learn.secondhw.model.AttractionService;
import my.learn.secondhw.model.ServiceType;
import my.learn.secondhw.repository.AttrServRepository;
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
class ServiceControllerTest {
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
    private AttrServRepository repository;

    @BeforeEach
    void clearDB() {
        repository.deleteAll();
    }


    @Test
    @SneakyThrows
    void getAllServicesIT() {
        AttractionService service1 = new AttractionService();
        service1.setName("First service");
        service1.setDescription("First service");
        service1.setServiceType(ServiceType.DINING);

        AttractionService service2 = new AttractionService();
        service2.setName("Second service");
        service2.setDescription("Second service");
        service2.setServiceType(ServiceType.SPORT);

        repository.saveAll(List.of(service1, service2));

        List<AttrServDto> attrServDtoList = repository.findAll().stream().map(AttrServDto::new).toList();

        MvcResult result = mvc.perform(get("http://localhost:" + port + "/service"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<AttrServDto> resultAttrServDtoList =
                List.of(new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttrServDto[].class));


        assertThat(resultAttrServDtoList.size()).isEqualTo(attrServDtoList.size());
        assertTrue(attrServDtoList.containsAll(resultAttrServDtoList));
    }

    @Test
    @SneakyThrows
    void getServiceByIdIT() {
        AttractionService service = new AttractionService();
        service.setName("First service");
        service.setDescription("First service");
        service.setServiceType(ServiceType.DINING);

        repository.save(service);

        Optional<AttractionService> attrServFromDbOptional = repository.findAll().stream().findAny();
        if (attrServFromDbOptional.isPresent()) {
            AttractionService serviceFromDb = attrServFromDbOptional.get();

            MvcResult result = mvc.perform(get("http://localhost:" + port + "/service/" + serviceFromDb.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            AttrServDto resultAttrServDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttrServDto.class);

            assertEquals(new AttrServDto(serviceFromDb), resultAttrServDto);
        } else {
            throw new RuntimeException("Attraction service not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void shouldNotGetServiceByIdIT() {
        AttractionService service = new AttractionService();
        service.setName("First service");
        service.setDescription("First service");
        service.setServiceType(ServiceType.DINING);

        repository.save(service);

        Optional<AttractionService> attrServFromDbOptional = repository.findAll().stream().findAny();
        if (attrServFromDbOptional.isPresent()) {
            AttractionService serviceFromDb = attrServFromDbOptional.get();

            repository.deleteAll();

            mvc.perform(get("http://localhost:" + port + "/service/" + serviceFromDb.getId()))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } else {
            throw new RuntimeException("Attraction service not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void saveServiceIT() {
        AttractionService service = new AttractionService();
        service.setName("First service");
        service.setDescription("First service");
        service.setServiceType(ServiceType.DINING);

        String content = new ObjectMapper().writeValueAsString(service);

        MvcResult result = mvc.perform(post("http://localhost:" + port + "/service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andReturn();

        Optional<AttractionService> serviceFromDbOptional = repository.findAll().stream().findAny();
        if (serviceFromDbOptional.isPresent()) {
            AttractionService serviceFromDb = serviceFromDbOptional.get();

            AttrServDto resultServiceDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), AttrServDto.class);

            assertNotNull(resultServiceDto.getId());
            assertEquals(resultServiceDto, new AttrServDto(serviceFromDb));
        } else {
            throw new RuntimeException("Address not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void deleteServiceIT() {
        AttractionService service = new AttractionService();
        service.setName("First service");
        service.setDescription("First service");
        service.setServiceType(ServiceType.DINING);

        repository.save(service);

        Optional<AttractionService> serviceFromDbOptional = repository.findAll().stream().findAny();
        if (serviceFromDbOptional.isPresent()) {
            AttractionService serviceFromDb = serviceFromDbOptional.get();

            mvc.perform(delete("http://localhost:" + port + "/service/" + serviceFromDb.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            Optional<AttractionService> emptyService = repository.findById(serviceFromDb.getId());

            assertTrue(emptyService.isEmpty());
        } else {
            throw new RuntimeException("Address not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void updateServiceIT() {
        AttractionService service = new AttractionService();
        service.setName("First service");
        service.setDescription("First service");
        service.setServiceType(ServiceType.DINING);

        repository.save(service);

        Optional<AttractionService> serviceFromDbOptional = repository.findAll().stream().findAny();
        if (serviceFromDbOptional.isPresent()) {


            AttractionService serviceToSend = new AttractionService();
            serviceToSend.setName("First service");
            serviceToSend.setDescription("First service");
            serviceToSend.setServiceType(ServiceType.DINING);

            String content = new ObjectMapper().writeValueAsString(serviceToSend);

            AttractionService serviceFromDb = serviceFromDbOptional.get();

            mvc.perform(put("http://localhost:" + port + "/service/" + serviceFromDb.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                            .characterEncoding("utf-8"))
                    .andExpect(status().isOk())
                    .andReturn();

            Optional<AttractionService> updatedServiceFromDbOptional = repository.findById(serviceFromDb.getId());

            if (updatedServiceFromDbOptional.isPresent()) {
                AttractionService updatedServiceFromDb = updatedServiceFromDbOptional.get();

                assertEquals(updatedServiceFromDb.getId(), serviceFromDb.getId());
                assertEquals(updatedServiceFromDb.getName(), serviceFromDb.getName());
                assertEquals(updatedServiceFromDb.getDescription(), serviceFromDb.getDescription());
                assertEquals(updatedServiceFromDb.getServiceType(), serviceFromDb.getServiceType());
            } else {
                throw new RuntimeException("Address not found by repository, problem with repository");
            }
        } else {
            throw new RuntimeException("Address not found by repository, problem with repository");
        }

    }
}