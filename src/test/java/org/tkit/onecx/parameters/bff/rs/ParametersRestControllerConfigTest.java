package org.tkit.onecx.parameters.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.parameters.bff.rs.controllers.ParametersRestController;
import org.tkit.onecx.parameters.bff.rs.models.ParameterConfig;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProductWrapperDTO;
import gen.org.tkit.onecx.parameters.clients.model.Product;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
@TestHTTPEndpoint(ParametersRestController.class)
class ParametersRestControllerConfigTest extends AbstractTest {

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @InjectMock
    ParameterConfig parameterConfig;

    @Inject
    Config config;

    public static class ConfigProducer {

        @Inject
        Config config;

        @Produces
        @ApplicationScoped
        @Mock
        ParameterConfig config() {
            return config.unwrap(SmallRyeConfig.class).getConfigMapping(ParameterConfig.class);
        }
    }

    @BeforeEach
    void resetExpectation() {
        clearExpectation(mockServerClient);

        Mockito.when(parameterConfig.restClients()).thenReturn(new ParameterConfig.RestClientsConfig() {
            @Override
            public ParameterConfig.RestClientConfig productStore() {
                return new ParameterConfig.RestClientConfig() {
                    @Override
                    public boolean enabled() {
                        return false;
                    }

                    @Override
                    public int pageSize() {
                        return 0;
                    }

                    @Override
                    public int pageNumber() {
                        return 0;
                    }
                };
            }
        });
    }

    @Test
    void loadProductsWithoutProductStoreTest() {
        List<Product> data = new ArrayList<>();
        Product product = new Product();
        product.setProductName("test2");
        product.setApplications(List.of("app1"));
        data.add(product);

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters/applications").withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data))));

        var wrapper = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("products")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(ProductWrapperDTO.class);

        Assertions.assertNotNull(wrapper);
        Assertions.assertNotNull(wrapper.getProducts());
        Assertions.assertEquals(0, wrapper.getProducts().size());
        Assertions.assertNotNull(wrapper.getUsedProducts());
        Assertions.assertEquals(1, wrapper.getUsedProducts().size());
        Assertions.assertNull(wrapper.getUsedProducts().get(0).getDisplayName());
    }
}
