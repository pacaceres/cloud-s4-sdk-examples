package com.sap.cloud.sdk.tutorial;

import com.sap.cloud.sdk.cloudplatform.servlet.Executable;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.cloud.sdk.tutorial.controllers.InsuranceController;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Calendar;
import java.util.GregorianCalendar;

@RunWith(SpringRunner.class)
@WebMvcTest(InsuranceController.class)
public class InsuranceServiceIntegrationTest {

    private static final MockUtil mockSdk = new MockUtil();

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void beforeClass() {
        mockSdk.mockDefaults();
        mockSdk.mockErpDestination();
    }

    @Test
    public void testGetInsuranceCostCenters() throws Exception {
        final SapClient sapClient = mockSdk.getErpSystem().getSapClient();

        mockSdk.requestContextExecutor().execute(new Executable() {
            @Override
            public void execute() throws Exception {
                final ResultActions action = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/rest/client/" + sapClient + "/costcenters"));
                action.andExpect(MockMvcResultMatchers.status().isOk());
            }
        });
    }
}
