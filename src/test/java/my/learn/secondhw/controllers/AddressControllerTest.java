package my.learn.secondhw.controllers;

import lombok.SneakyThrows;
import my.learn.secondhw.dto.AddressDto;
import my.learn.secondhw.model.Address;
import my.learn.secondhw.repository.AddressRepository;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(value = {
        "classpath:application-test.properties"
})
@AutoConfigureMockMvc
class AddressControllerTest {
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
    private AddressRepository addressRepository;
    private Address address;

    @BeforeEach
    void clearDB() {
        addressRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void getAllAddressesIT() {
        Address address1 = new Address();
        address1.setBuilding(10);
        address1.setStreet("Main Street");
        address1.setCity("San Francisco");

        Address address2 = new Address();
        address2.setBuilding(2);
        address2.setStreet("Static Street");
        address2.setCity("San Sebastian");

        addressRepository.saveAll(List.of(address1, address2));

        List<AddressDto> addressDtoFromDb = addressRepository.findAll().stream().map(AddressDto::new).toList();

        MvcResult result = mvc.perform(get("http://localhost:" + port + "/address"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        List<AddressDto> resultAddressDtoList =
                List.of(new ObjectMapper().readValue(result.getResponse().getContentAsString(), AddressDto[].class));

        assertThat(resultAddressDtoList.size()).isEqualTo(addressDtoFromDb.size());
        assertTrue(addressDtoFromDb.containsAll(resultAddressDtoList));
    }

    @Test
    @SneakyThrows
    void shouldFindAddressByIdIT() {
        Address address = new Address();
        address.setBuilding(10);
        address.setStreet("Main Street");
        address.setCity("San Francisco");

        addressRepository.save(address);

        Optional<Address> addressFromDbOptional = addressRepository.findAll().stream().findAny();
        if (addressFromDbOptional.isPresent()) {
            Address addressFromDb = addressFromDbOptional.get();

            MvcResult result = mvc.perform(get("http://localhost:" + port + "/address/" + addressFromDb.getId()))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn();

            AddressDto resultAddressDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), AddressDto.class);

            assertEquals(new AddressDto(addressFromDb), resultAddressDto);
        } else {
            throw new RuntimeException("Address not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void shouldNotFindAddressByIdIT() {
        Address address = new Address();
        address.setBuilding(10);
        address.setStreet("Main Street");
        address.setCity("San Francisco");

        addressRepository.save(address);

        Optional<Address> addressFromDbOptional = addressRepository.findAll().stream().findAny();
        if (addressFromDbOptional.isPresent()) {
            Address addressFromDb = addressFromDbOptional.get();

            addressRepository.deleteAll();

            mvc.perform(get("http://localhost:" + port + "/address/" + addressFromDb.getId()))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } else {
            throw new RuntimeException("Address not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void saveAddressIT() {
        Address address = new Address();
        address.setBuilding(10);
        address.setStreet("Main Street");
        address.setCity("San Francisco");

        String content = new ObjectMapper().writeValueAsString(address);

        MvcResult result = mvc.perform(post("http://localhost:" + port + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andReturn();

        Optional<Address> addressFromDbOptional = addressRepository.findAll().stream().findAny();
        if (addressFromDbOptional.isPresent()) {
            Address addressFromDb = addressFromDbOptional.get();

            AddressDto resultAddressDto =
                    new ObjectMapper().readValue(result.getResponse().getContentAsString(), AddressDto.class);

            assertNotNull(resultAddressDto.getId());
            assertEquals(resultAddressDto, new AddressDto(addressFromDb));
        } else {
            throw new RuntimeException("Address not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void deleteAddressIT() {
        Address address = new Address();
        address.setBuilding(10);
        address.setStreet("Main Street");
        address.setCity("San Francisco");
        addressRepository.save(address);

        Optional<Address> addressFromDbOptional = addressRepository.findAll().stream().findAny();
        if (addressFromDbOptional.isPresent()) {
            Address addressFromDb = addressFromDbOptional.get();

            MvcResult result = mvc.perform(delete("http://localhost:" + port + "/address/" + addressFromDb.getId()))
                    .andExpect(status().isOk())
                    .andReturn();

            Optional<Address> emptyAddress = addressRepository.findById(addressFromDb.getId());

            assertTrue(emptyAddress.isEmpty());
        } else {
            throw new RuntimeException("Address not found by repository, problem with repository");
        }
    }

    @Test
    @SneakyThrows
    void updateAddressIT() {
        Address addressToSave = new Address();
        addressToSave.setBuilding(10);
        addressToSave.setStreet("Main Street");
        addressToSave.setCity("San Francisco");
        addressRepository.save(addressToSave);

        Optional<Address> addressFromDbOptional = addressRepository.findAll().stream().findAny();
        if (addressFromDbOptional.isPresent()) {
            Address addressToSend = new Address();
            addressToSend.setBuilding(20);
            addressToSend.setStreet("Public Street");
            addressToSend.setCity("San Diego");

            String content = new ObjectMapper().writeValueAsString(addressToSend);

            Address addressFromDb = addressFromDbOptional.get();

            mvc.perform(put("http://localhost:" + port + "/address/" + addressFromDb.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                            .characterEncoding("utf-8"))
                    .andExpect(status().isOk())
                    .andReturn();

            Optional<Address> updatedAddressFromDbOptional = addressRepository.findById(addressFromDb.getId());

            if (updatedAddressFromDbOptional.isPresent()) {
                Address updatedAddressFromDb = updatedAddressFromDbOptional.get();

                assertEquals(updatedAddressFromDb.getId(), addressFromDb.getId());
                assertEquals(updatedAddressFromDb.getCity(), addressToSend.getCity());
                assertEquals(updatedAddressFromDb.getBuilding(), addressToSend.getBuilding());
                assertEquals(updatedAddressFromDb.getStreet(), addressToSend.getStreet());
            } else {
                throw new RuntimeException("Address not found by repository, problem with repository");
            }
        } else {
            throw new RuntimeException("Address not found by repository, problem with repository");
        }
    }
}

