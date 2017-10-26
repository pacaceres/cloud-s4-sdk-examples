package com.sap.cloud.sdk.tutorial.controllers;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.connectivity.ErpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.readcostcenterdata.CostCenter;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.ReadCostCenterDataService;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ReadCostCenterDataService costCenterService;

    @Autowired
    public InsuranceController(ReadCostCenterDataService costCenterService ) {
        this.costCenterService = costCenterService;
    }

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
    public ResponseEntity<List<CostCenter>> getInsuranceCostCenters(
            @PathVariable final String sapClient) {
        try {
            final ErpConfigContext configContext = getErpConfigContext(sapClient);

            final List<CostCenter> insuranceCostCenters =
                    costCenterService
                            .getAllCostCenter()
                            .select(
                                    CostCenter.COST_CENTER_ID,
                                    CostCenter.COST_CENTER_DESCRIPTION,
                                    CostCenter.STATUS,
                                    CostCenter.COMPANY_CODE,
                                    CostCenter.CATEGORY,
                                    CostCenter.VALIDITY_START_DATE,
                                    CostCenter.VALIDITY_END_DATE
                            )
                            .execute(configContext);

            return ResponseEntity.ok(insuranceCostCenters);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
