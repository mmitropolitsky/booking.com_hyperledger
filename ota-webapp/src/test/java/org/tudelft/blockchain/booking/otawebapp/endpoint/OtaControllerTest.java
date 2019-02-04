package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OtaControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mvc;

    private String propertyName;


    @Before
    public void setup() throws Exception {
        propertyName = "property" + UUID.randomUUID();
        mvc.perform(post("/api/po/property").param("name", propertyName)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void joinOtaAToProperty() throws Exception {
        mvc.perform(get("/api/ota/OtaA/join/" + propertyName)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void joinOtaBToProperty() throws Exception {
        mvc.perform(get("/api/ota/OtaB/join/" + propertyName)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void otaABook() throws Exception {
        mvc.perform(post("/api/ota/OtaA/" + propertyName + "/book/")
                .param("fromDate", "2019-02-10").param("toDate", "2019-02-15")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    public void otaBCanBook() throws Exception {
        mvc.perform(post("/api/ota/OtaB/" + propertyName + "/book/")
                .param("fromDate", "2019-02-16").param("toDate", "2019-02-18"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    public void otaBCantBook() throws Exception {
        mvc.perform(post("/api/ota/OtaB/" + propertyName + "/book/")
                .param("fromDate", "2019-02-10").param("toDate", "2019-02-15")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(false)));
    }

}
