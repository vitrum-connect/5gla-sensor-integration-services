package de.app.fivegla.integration.sensoterra;

import de.app.fivegla.api.Error;
import de.app.fivegla.api.ErrorMessage;
import de.app.fivegla.api.exceptions.BusinessException;
import de.app.fivegla.integration.sensoterra.cache.ApiKeyWithSettingsCache;
import de.app.fivegla.integration.sensoterra.dto.ApiKeyWithSettings;
import de.app.fivegla.integration.sensoterra.dto.request.LoginRequest;
import de.app.fivegla.persistence.entity.ThirdPartyApiConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service for integration with Sensoterra.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyIntegrationService {

    private final ApiKeyWithSettingsCache apiKeyWithSettingsCache;

    private final RestTemplate restTemplate;

    /**
     * Fetch the API key from the API.
     */
    public String fetchApiKey(ThirdPartyApiConfiguration thirdPartyApiConfiguration) {
        var url = thirdPartyApiConfiguration.getUrl();
        var username = thirdPartyApiConfiguration.getUsername();
        var password = thirdPartyApiConfiguration.getPassword();
        if (apiKeyWithSettingsCache.isExpired()) {
            try {
                var response = restTemplate.postForEntity(url + "/customer/auth", new LoginRequest(username, password), ApiKeyWithSettings.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    log.error("Error while login against the API. Status code: {}", response.getStatusCode());
                    throw new BusinessException(ErrorMessage.builder()
                            .error(Error.SENSOTERRA_COULD_NOT_LOGIN_AGAINST_API)
                            .message("Could not login against the API.")
                            .build());
                } else {
                    log.info("Successfully logged in against the API.");
                    var credentials = response.getBody();
                    if (null == credentials) {
                        throw new BusinessException(ErrorMessage.builder()
                                .error(Error.SENSOTERRA_COULD_NOT_LOGIN_AGAINST_API)
                                .message("Could not login against the API. Response was empty.")
                                .build());
                    } else {
                        log.info("API key found: {}", credentials.getApiKey());
                        apiKeyWithSettingsCache.setApiKeyWithSettings(credentials);
                        return credentials.getApiKey();
                    }
                }
            } catch (Exception e) {
                log.error("Error while login against the API.", e);
                throw new BusinessException(ErrorMessage.builder()
                        .error(Error.SENSOTERRA_COULD_NOT_LOGIN_AGAINST_API)
                        .message("Could not login against the API.")
                        .build());
            }
        } else {
            return apiKeyWithSettingsCache.getApiKey();
        }
    }

}
