package com.tableturn;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Joining the waitlist, over HTTP through MockMvc on the whole app. Every test gets a fresh in-memory list. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:waitlist-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WaitlistTest {

    @Autowired
    MockMvc http;

    ResultActions join(String body) throws Exception {
        return http.perform(post("/waitlist").contentType(MediaType.APPLICATION_JSON).content(body));
    }

        @Test
    void theKulkarnisJoinAndGetANumberAndAPlace() throws Exception {
        join("{\"name\":\"Kulkarni\",\"covers\":6,\"phone\":\"+919876543210\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.partyId").value("W001"))
                .andExpect(jsonPath("$.position").value(1))
                .andExpect(header().string("Location", "/parties/W001"));
        http.perform(get("/parties/W001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kulkarni"))
                .andExpect(jsonPath("$.state").value("waiting"));
    }

    @Test
    void eachNewPartyQueuesBehindTheOnesWaiting() throws Exception {
        join("{\"name\":\"A\",\"covers\":2}").andExpect(jsonPath("$.position").value(1));
        join("{\"name\":\"B\",\"covers\":2}").andExpect(jsonPath("$.position").value(2));
    }

        @Test
    void aPartyOfNineIsTooBigAndTheLargestTableIsNamed() throws Exception {
        join("{\"name\":\"Big\",\"covers\":9}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/party-too-big"))
                .andExpect(jsonPath("$.maxCovers").value(6));
        http.perform(get("/parties/W001")).andExpect(status().isNotFound());
    }

    @Test
    void aBlankNameIsAValidationErrorNamingTheField() throws Exception {
        join("{\"name\":\"\",\"covers\":2}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/validation"))
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void anUnknownPartyIs404() throws Exception {
        http.perform(get("/parties/W999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("/errors/no-such-party"));
    }
}