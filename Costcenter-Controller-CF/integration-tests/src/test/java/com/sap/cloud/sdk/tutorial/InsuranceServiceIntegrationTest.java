package com.sap.cloud.sdk.tutorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.cloudplatform.servlet.Executable;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.readcostcenterdata.CostCenter;
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

    private static final String EXISTING_COSTCENTER_ID = "0010101101";
    private static final String DEMO_COSTCENTER_ID = generateId(); // 10 chars

    private static final Calendar DEMO_VALID_FROM = new GregorianCalendar(2016, 4, 1);
    private static final Calendar DEMO_VALID_TO = new GregorianCalendar(2016, 5, 1);

    private static final MockUtil mockSdk = new MockUtil();

    // Update these parameters for your S/4HANA demo data
    private static final String COST_CENTER_LANGUAGE = "EN";
    private static final String COST_CENTER_CATEGORY = "E";
    private static final String COMPANY_CODE = "1010";


    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void beforeClass() {
        mockSdk.mockDefaults();
        mockSdk.mockErpDestination();
    }

    private static String generateId() {
        return "T" + StringUtils.leftPad(Long.toString(new DateTime().minus(Period.years(45)).getMillis(), 36).toUpperCase(), 9, '0');
    }

    private String getNewCostCenterAsJson(final String costCenterId) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
            CostCenter.builder()
                .costCenterID(costCenterId)
                .costCenterName("dummy")
                .costCenterDescription("hello demo")
                .category(COST_CENTER_CATEGORY)
                .companyCode(COMPANY_CODE)
                .status("ACTIVE")
                .validityStartDate(DEMO_VALID_FROM)
                .validityEndDate(DEMO_VALID_TO)
                .language(COST_CENTER_LANGUAGE)
                .build()
        );
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
