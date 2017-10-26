package com.sap.cloud.sdk.tutorial;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.datamodel.odata.helper.EntityField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.readcostcenterdata.CostCenter;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.readcostcenterdata.CostCenterFluentHelper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.ReadCostCenterDataService;
import com.sap.cloud.sdk.tutorial.controllers.InsuranceController;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(InsuranceController.class)
public class InsuranceControllerTest {

    private static final String TEST_SAP_CLIENT = "715";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private static ReadCostCenterDataService mockCostCenterService;

    private static CostCenterFluentHelper mockFluentHelper = mock(CostCenterFluentHelper.class, Answers.RETURNS_SELF);

    @Before
    public void beforeClass() throws ODataException {
        when(mockCostCenterService.getAllCostCenter()).thenReturn(mockFluentHelper);
        when(mockFluentHelper.execute(any(ErpConfigContext.class))).thenReturn(new LinkedList<CostCenter>());
    }

    @Test
    public void testGetInsuranceCostCenters() throws Exception {

        final ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/rest/client/" + TEST_SAP_CLIENT + "/costcenters"));

        action.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
