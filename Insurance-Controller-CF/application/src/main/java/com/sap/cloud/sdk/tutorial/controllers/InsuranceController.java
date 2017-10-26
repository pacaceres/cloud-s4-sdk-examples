package com.sap.cloud.sdk.tutorial.controllers;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.odatav2.connectivity.*;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.connectivity.ErpDestination;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;
import com.sap.cloud.sdk.tutorial.models.CostCenterDetails;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
public class InsuranceController {

    private static final Logger logger = CloudLoggerFactory.getLogger(InsuranceController.class);

    private ErpConfigContext getErpConfigContext(final String sapClient) {
        final ErpConfigContext config =
                new ErpConfigContext(
                        ErpDestination.getDefaultName(),
                        new SapClient(sapClient),
                        Locale.ENGLISH
                );
        return config;
    }

    @RequestMapping(value = "api/v1/rest/client/{sapClient:[\\d]+}/costcenters", method = RequestMethod.GET)
    public ResponseEntity<List<CostCenterDetails>> getInsuranceCostCenters(
            @PathVariable final String sapClient) {
        try {
            final ErpConfigContext configContext = getErpConfigContext(sapClient);

            final ODataQuery query = ODataQueryBuilder
                    .withEntity(
                        "/sap/opu/odata/sap/FCO_PI_COST_CENTER",
                        "CostCenterCollection")
                    .filter(
                            ODataProperty.field("CompanyCode").eq(ODataType.of("1010")))
                    .build();

            final ODataQueryResult queryResult = query.execute(configContext);
            final List<CostCenterDetails> costCenterDetails = queryResult.asList(CostCenterDetails.class);

            return ResponseEntity.ok(costCenterDetails);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
