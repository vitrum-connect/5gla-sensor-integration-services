package de.app.fivegla.integration.soilscout;


import de.app.fivegla.api.enums.EntityType;
import de.app.fivegla.integration.fiware.FiwareEntityIntegrationService;
import de.app.fivegla.integration.fiware.model.DeviceMeasurement;
import de.app.fivegla.integration.fiware.model.internal.DateTimeAttribute;
import de.app.fivegla.integration.fiware.model.internal.EmptyAttribute;
import de.app.fivegla.integration.fiware.model.internal.NumberAttribute;
import de.app.fivegla.integration.fiware.model.internal.TextAttribute;
import de.app.fivegla.integration.soilscout.model.SensorData;
import de.app.fivegla.persistence.entity.Group;
import de.app.fivegla.persistence.entity.Tenant;
import de.app.fivegla.persistence.entity.ThirdPartyApiConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for integration with FIWARE.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SoilScoutFiwareIntegrationServiceWrapper {

    private final SoilScoutSensorIntegrationService soilScoutSensorIntegrationService;
    private final FiwareEntityIntegrationService fiwareEntityIntegrationService;

    /**
     * Create soil scout sensor data in FIWARE.
     *
     * @param sensorData the sensor data to create
     */
    public void persist(Tenant tenant, Group group, ThirdPartyApiConfiguration thirdPartyApiConfiguration, SensorData sensorData) {
        var soilScoutSensor = soilScoutSensorIntegrationService.fetch(thirdPartyApiConfiguration, sensorData.getDevice());
        log.debug("Found sensor with id {} in Soil Scout API.", sensorData.getDevice());

        var temperature = new DeviceMeasurement(
                tenant.getFiwarePrefix() + soilScoutSensor.getId(),
                EntityType.SOILSCOUT_SENSOR.getKey(),
                new TextAttribute(group.getGroupId()),
                new TextAttribute("temperature"),
                new NumberAttribute(sensorData.getTemperature()),
                new DateTimeAttribute(sensorData.getTimestamp().toInstant()),
                new EmptyAttribute(),
                soilScoutSensor.getLocation().getLatitude(),
                soilScoutSensor.getLocation().getLongitude());
        fiwareEntityIntegrationService.persist(temperature);

        var moisture = new DeviceMeasurement(
                tenant.getFiwarePrefix() + soilScoutSensor.getId(),
                EntityType.SOILSCOUT_SENSOR.getKey(),
                new TextAttribute(group.getGroupId()),
                new TextAttribute("moisture"),
                new NumberAttribute(sensorData.getMoisture()),
                new DateTimeAttribute(sensorData.getTimestamp().toInstant()),
                new EmptyAttribute(),
                soilScoutSensor.getLocation().getLatitude(),
                soilScoutSensor.getLocation().getLongitude());
        fiwareEntityIntegrationService.persist(moisture);

        var conductivity = new DeviceMeasurement(
                tenant.getFiwarePrefix() + soilScoutSensor.getId(),
                EntityType.SOILSCOUT_SENSOR.getKey(),
                new TextAttribute(group.getGroupId()),
                new TextAttribute("conductivity"),
                new NumberAttribute(sensorData.getConductivity()),
                new DateTimeAttribute(sensorData.getTimestamp().toInstant()),
                new EmptyAttribute(),
                soilScoutSensor.getLocation().getLatitude(),
                soilScoutSensor.getLocation().getLongitude());
        fiwareEntityIntegrationService.persist(conductivity);

        var salinity = new DeviceMeasurement(
                tenant.getFiwarePrefix() + soilScoutSensor.getId(),
                EntityType.SOILSCOUT_SENSOR.getKey(),
                new TextAttribute(group.getGroupId()),
                new TextAttribute("salinity"),
                new NumberAttribute(sensorData.getSalinity()),
                new DateTimeAttribute(sensorData.getTimestamp().toInstant()),
                new EmptyAttribute(),
                soilScoutSensor.getLocation().getLatitude(),
                soilScoutSensor.getLocation().getLongitude());
        fiwareEntityIntegrationService.persist(salinity);

        var waterBalance = new DeviceMeasurement(
                tenant.getFiwarePrefix() + soilScoutSensor.getId(),
                EntityType.SOILSCOUT_SENSOR.getKey(),
                new TextAttribute(group.getGroupId()),
                new TextAttribute("waterBalance"),
                new NumberAttribute(sensorData.getWaterBalance()),
                new DateTimeAttribute(sensorData.getTimestamp().toInstant()),
                new EmptyAttribute(),
                soilScoutSensor.getLocation().getLatitude(),
                soilScoutSensor.getLocation().getLongitude());
        fiwareEntityIntegrationService.persist(waterBalance);
    }

}
