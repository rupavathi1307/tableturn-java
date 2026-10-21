package com.tableturn;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** The floor, read over HTTP through MockMvc on the whole app (an in-memory database for this class). */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:floor-test")
@AutoConfigureMockMvc
class FloorTest {

    @Autowired
    MockMvc http;

    @Test
    void theFloorShows22FreeTables() throws Exception {
        http.perform(get("/branches/Baner/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branch").value("Baner"))
                .andExpect(jsonPath("$.free").value(22))
                .andExpect(jsonPath("$.tables.length()").value(22))
                .andExpect(jsonPath("$.tables[?(@.state != 'free')]").isEmpty());
    }

    @Test
    void filteringToGardenShowsOnlyGardenTablesAndCountsThem() throws Exception {
        http.perform(get("/branches/Baner/tables").param("section", "Garden"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tables.length()").value(8))
                .andExpect(jsonPath("$.free").value(8))
                .andExpect(jsonPath("$.tables[?(@.section != 'Garden')]").isEmpty());
                    }

    @Test
    void anUnknownSectionIs400Validation() throws Exception {
        http.perform(get("/branches/Baner/tables").param("section", "Roof"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/validation"));
    }

    @Test
    void oneTableIsReadWithItsSeatsAndSection() throws Exception {
        http.perform(get("/branches/Baner/tables/T11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats").value(4))
                .andExpect(jsonPath("$.section").value("Garden"))
                .andExpect(jsonPath("$.state").value("free"));
                }
 
    @Test
    void anUnknownTableIs404NoSuchTable() throws Exception {
        http.perform(get("/branches/Baner/tables/T99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("/errors/no-such-table"));
    }
 
    @Test
    void anUnknownBranchIs404NoSuchBranch() throws Exception {
        http.perform(get("/branches/Kothrud/tables"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("/errors/no-such-branch"));
    }
}
 