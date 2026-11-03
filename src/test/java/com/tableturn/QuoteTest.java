package com.tableturn;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.tableturn.party.PartyService;

/** The quote and the real wait, with the clock fixed at a known moment so every time is exact. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:quote-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuoteTest {

    static final Instant T = Instant.parse("2026-09-27T19:10:00Z");

    @TestConfiguration
    static class FixedClock {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(T, ZoneOffset.UTC);
        }
    }

    @Autowired
    MockMvc http;

    @Autowired
    PartyService parties;

    @Test
    void aPartyIsQuotedTheDefaultWaitAtTheMomentItJoins() throws Exception {
        http.perform(post("/waitlist").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Kulkarni\",\"covers\":6}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quotedMinutes").value(40))
                .andExpect(jsonPath("$.quotedAt").value("2026-09-27T19:10:00Z"));
        http.perform(get("/parties/W001"))
                .andExpect(jsonPath("$.quotedMinutes").value(40))
                .andExpect(jsonPath("$.waitedMinutes").doesNotExist());
    }

    @Test
    void theRealWaitIsStampedInWholeMinutesAndStaysPut() throws Exception {
        http.perform(post("/waitlist").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Kulkarni\",\"covers\":6}")).andExpect(status().isCreated());
        parties.stampWait(1, T, T.plus(Duration.ofMinutes(47)).plusSeconds(30));
        http.perform(get("/parties/W001")).andExpect(jsonPath("$.waitedMinutes").value(47));
        http.perform(get("/parties/W001")).andExpect(jsonPath("$.waitedMinutes").value(47));
    }
}