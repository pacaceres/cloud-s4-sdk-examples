package com.sap.cloud.sdk.tutorial.controllers;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQuery;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryResult;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.connectivity.ErpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.bapi.services.DefaultCostCenterService;
import com.sap.cloud.sdk.s4hana.datamodel.bapi.structures.CostCenterCreateInput;
import com.sap.cloud.sdk.s4hana.datamodel.bapi.structures.ReturnParameter;
import com.sap.cloud.sdk.s4hana.datamodel.bapi.types.*;
import com.sap.cloud.sdk.s4hana.serialization.ErpBoolean;
import com.sap.cloud.sdk.s4hana.serialization.SapClient;
import com.sap.cloud.sdk.tutorial.models.CostCenterDetails;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
public class CostCenterController {

    private static final Logger logger = CloudLoggerFactory.getLogger(CostCenterController.class);

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
    public ResponseEntity<List<CostCenterDetails>> getCostCenter(
            @PathVariable final String sapClient) {
        try {
            final ErpConfigContext configContext = getErpConfigContext(sapClient);

            final ODataQuery query = ODataQueryBuilder
                    .withEntity(
                        "/sap/opu/odata/sap/FCO_PI_COST_CENTER",
                        "CostCenterCollection")
                    .select(
                        "CostCenterID",
                        "CostCenterDescription",
                        "Status",
                        "CompanyCode",
                        "Category",
                        "ValidityStartDate",
                        "ValidityEndDate")
                    .build();

            final ODataQueryResult queryResult = query.execute(configContext);
            final List<CostCenterDetails> costCenterDetails = queryResult.asList(CostCenterDetails.class);

            return ResponseEntity.ok(costCenterDetails);

        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "api/v1/rest/client/{sapClient:[\\d]+}/costcenters", method = RequestMethod.POST)
    public ResponseEntity<List<ReturnParameter>> postCostCenter(
            @PathVariable final String sapClient,
            @RequestBody final CostCenterDetails details,
            @RequestParam(defaultValue = "false") final boolean testRun) {
        try {
            final ErpConfigContext configContext = getErpConfigContext(sapClient);

            final List<ReturnParameter> result = (new DefaultCostCenterService())
                .createMultiple(
                    // Required parameter: ControllingArea
                    details.getControllingArea(),
                    // Required parameter: CostCenter Input
                    CostCenterCreateInput
                        .builder()
                        .validFrom(CostCenterDetails.asLocalDate(details.getValidFrom()))
                        .validTo(CostCenterDetails.asLocalDate(details.getValidTo()))
                        .costcenter(new CostCenter(details.getId().getValue()))
                        .name("SAP dummy")
                        .currency(CurrencyKey.of("EUR"))
                        .descript(details.getDescription())
                        .costcenterType(IndicatorForCostCenterType.of(details.getCategory()))
                        .personInCharge(CostCenterManager.of(details.getPersonResponsible()))
                        .costctrHierGrp(SetId.of(details.getCostCenterGroup()))
                        .compCode(details.getCompanyCode())
                        .profitCtr(details.getProfitCenter())
                        .build())
                .testRun(new ErpBoolean(testRun))
                .execute(configContext)
                .getMessages();

            return ResponseEntity.ok(result);

        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
