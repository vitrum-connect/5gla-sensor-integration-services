package de.app.fivegla.integration.farm21;

import de.app.fivegla.api.Manufacturer;
import de.app.fivegla.integration.farm21.model.Sensor;
import de.app.fivegla.integration.farm21.model.SensorData;
import de.app.fivegla.monitoring.JobMonitor;
import de.app.fivegla.persistence.ApplicationDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Scheduled data import from Farm21 API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Farm21MeasurementImport {

    private final Farm21SensorDataIntegrationService farm21SensorDataIntegrationService;
    private final ApplicationDataRepository applicationDataRepository;
    private final Farm21FiwareIntegrationServiceWrapper farm21FiwareIntegrationServiceWrapper;
    private final JobMonitor jobMonitor;

    @Value("${app.scheduled.daysInThePastForInitialImport}")
    private int daysInThePastForInitialImport;

    /**
     * Run scheduled data import.
     */
    public void run() {
        var begin = Instant.now();
        try {
            if (applicationDataRepository.getLastRun(Manufacturer.FARM21).isPresent()) {
                log.info("Running scheduled data import from Farm21 API");
                var lastRun = applicationDataRepository.getLastRun(Manufacturer.FARM21).get();
                var measurements = farm21SensorDataIntegrationService.fetchAll(lastRun, Instant.now());
                jobMonitor.logNrOfEntitiesFetched(Manufacturer.FARM21, measurements.size());
                log.info("Found {} measurements", measurements.size());
                log.info("Persisting {} measurements", measurements.size());
                measurements.entrySet().forEach(this::persistDataWithinFiware);
            } else {
                log.info("Running initial data import from Farm21 API, this may take a while");
                var measurements = farm21SensorDataIntegrationService.fetchAll(Instant.now().minus(daysInThePastForInitialImport, ChronoUnit.DAYS), Instant.now());
                log.info("Found {} measurements", measurements.size());
                log.info("Persisting {} measurements", measurements.size());
                jobMonitor.logNrOfEntitiesFetched(Manufacturer.FARM21, measurements.size());
                measurements.entrySet().forEach(this::persistDataWithinFiware);
            }
            applicationDataRepository.updateLastRun(Manufacturer.FARM21);
        } catch (Exception e) {
            log.error("Error while running scheduled data import from Farm21 API", e);
            jobMonitor.logErrorDuringExecution(Manufacturer.FARM21);
        } finally {
            log.info("Finished scheduled data import from Farm21 API");
            var end = Instant.now();
            jobMonitor.logJobExecutionTime(Manufacturer.FARM21, begin.until(end, ChronoUnit.SECONDS));
        }
    }

    private void persistDataWithinFiware(Map.Entry<Sensor, List<SensorData>> entry) {
        try {
            Sensor key = entry.getKey();
            List<SensorData> value = entry.getValue();
            farm21FiwareIntegrationServiceWrapper.persist(key, value);
        } catch (Exception e) {
            log.error("Error while running scheduled data import from Farm21 API", e);
            jobMonitor.logErrorDuringExecution(Manufacturer.FARM21);
        }
    }

}
