package org.tkit.onecx.parameters.bff.rs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;
import org.mockserver.client.MockServerClient;
import org.mockserver.mock.Expectation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;

@QuarkusTestResource(MockServerTestResource.class)
public abstract class AbstractTest {

    private static final List<String> MOCK_IDS = new ArrayList<>();

    protected static final String USER = "bob";

    protected static final String ADMIN = "alice";

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    protected static final String APM_HEADER_PARAM = ConfigProvider.getConfig()
            .getValue("%test.tkit.rs.context.token.header-param", String.class);
    static {
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory(
                        (cls, charset) -> {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.registerModule(new JavaTimeModule());
                            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                            return objectMapper;
                        }));
    }

    protected void addExpectation(Expectation[] exceptions) {
        for (Expectation e : List.of(exceptions)) {
            MOCK_IDS.add(e.getId());
        }

    }

    protected void clearExpectation(MockServerClient client) {
        MOCK_IDS.forEach(x -> {
            try {
                client.clear(x);
            } catch (Exception ex) {
                //  mockId not existing
            }
        });
        MOCK_IDS.clear();
    }
}
