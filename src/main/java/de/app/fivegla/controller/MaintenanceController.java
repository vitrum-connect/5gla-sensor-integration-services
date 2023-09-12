package de.app.fivegla.controller;


import de.app.fivegla.controller.api.BaseMappings;
import de.app.fivegla.controller.api.swagger.OperationTags;
import de.app.fivegla.controller.security.SecuredApiAccess;
import de.app.fivegla.fiware.SubscriptionService;
import de.app.fivegla.fiware.model.enums.Type;
import de.app.fivegla.scheduled.DataImportScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The MaintenanceController class handles maintenance-related operations.
 * It provides a method to send subscriptions for device measurement notifications.
 * <p>
 * This controller is mapped to the /maintenance endpoint.
 */
@Slf4j
@Profile("maintenance")
@RequestMapping(BaseMappings.MAINTENANCE)
public class MaintenanceController implements SecuredApiAccess {

    private final SubscriptionService subscriptionService;
    private final DataImportScheduler dataImportScheduler;

    @Value("${app.fiware.subscriptions.enabled}")
    private boolean subscriptionsEnabled;

    public MaintenanceController(SubscriptionService subscriptionService, DataImportScheduler dataImportScheduler) {
        this.subscriptionService = subscriptionService;
        this.dataImportScheduler = dataImportScheduler;
    }

    /**
     * Sends a subscription for device measurement notifications.
     *
     * @return A ResponseEntity object with HTTP status code and an empty body.
     */
    @Operation(
            operationId = "maintenance.send-subscription",
            description = "Sends a subscription for device measurement notifications.",
            tags = OperationTags.MAINTENANCE
    )
    @ApiResponse(
            responseCode = "200",
            description = "The subscription was sent successfully."
    )
    @ApiResponse(
            responseCode = "400",
            description = "The request is invalid since the subscriptions are disabled."
    )
    @PostMapping("/send-subscription")
    public ResponseEntity<Void> sendSubscription() {
        if (subscriptionsEnabled) {
            subscriptionService.subscribeAndReset(Type.DeviceMeasurement);
            return ResponseEntity.ok().build();
        } else {
            log.warn("Subscriptions are disabled. Not subscribing to device measurement notifications.");
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Run the import.
     */
    @Operation(
            operationId = "manual.import.run",
            description = "Run the import manually.",
            tags = OperationTags.MAINTENANCE
    )
    @ApiResponse(
            responseCode = "200",
            description = "The import has been started asynchronously."
    )
    @PostMapping(value = "/run", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> runAllImports() {
        dataImportScheduler.scheduleDataImport();
        return ResponseEntity.ok().build();
    }

}
