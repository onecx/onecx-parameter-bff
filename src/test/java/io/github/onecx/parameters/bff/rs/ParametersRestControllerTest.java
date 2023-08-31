package io.github.onecx.parameters.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;

import gen.io.github.onecx.parameters.bff.clients.model.ApplicationsPageResult;
import gen.io.github.onecx.parameters.bff.clients.model.KeysPageResult;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ApplicationParameterCreateDTO;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ApplicationsPageResultDTO;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ParametersRestController.class)
@QuarkusTestResource(MockServerTestResource.class)
class ParametersRestControllerTest {
    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    void getAllApplicationsTest() {

        ApplicationsPageResult data = new ApplicationsPageResult();
        data.setNumber(1);
        data.setSize(3);
        data.setTotalElements(3L);
        data.setTotalPages(1L);
        data.setStream(List.of("app1", "app2", "app3"));

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters/applications").withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .get("applications")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ApplicationsPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream(), output.getStream());
    }

    @Test
    void getAllKeysTest() {

        KeysPageResult data = new KeysPageResult();
        data.setNumber(1);
        data.setSize(3);
        data.setTotalElements(3L);
        data.setTotalPages(1L);
        data.setStream(List.of("key1", "key2", "key3"));

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters/keys").withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .get("keys")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ApplicationsPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream(), output.getStream());
    }

    @Test
    void createParameterValueTest() {

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters").withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        ApplicationParameterCreateDTO input = new ApplicationParameterCreateDTO();
        input.setApplicationId("app1");
        input.setValue("value1");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode())
                .contentType(APPLICATION_JSON);
    }
}
