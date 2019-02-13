package org.tudelft.blockchain.booking.otawebapp.endpoint;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PropertyOwnerControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mvc;

    @Test
    public void givenWac_whenServletContext_thenItProvidesGreetController() {
        ServletContext servletContext = wac.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof ApplicationContext);
        Assert.assertNotNull(wac.getBean("propertyOwnerController"));
    }

    /**
     * Create property tests the following:
     * 1. Create a channel with the name of the property.
     * 2. Instantiate chaincode with name ${propertyName}Overbooking in the created channel.
     * @throws Exception
     */
    @Test
    public void createProperty() throws Exception {
        mvc.perform(post("/api/po/property").param("name", "property" + UUID.randomUUID())).andDo(print()).andExpect(status().isOk());
    }

    @After
    public void deleteProperty() {

    }
}
