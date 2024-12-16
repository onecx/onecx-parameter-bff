package org.tkit.onecx.parameters.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;
import gen.org.tkit.onecx.parameters.clients.model.ParameterHistory;
import gen.org.tkit.onecx.parameters.clients.model.ParameterHistoryCount;
import gen.org.tkit.onecx.parameters.clients.model.ParameterHistoryPageResult;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HistoryRestControllerTest extends AbstractTest {

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    void getAllApplicationParametersHistoryTest() {

        ParameterHistory h1 = new ParameterHistory();
        h1.setId("1");
        h1.setKey("key2");
        h1.setUsedValue("value1");

        ParameterHistory h2 = new ParameterHistory();
        h2.setId("2");
        h2.setKey("key2");
        h2.setUsedValue("value2");

        ParameterHistoryPageResult data = new ParameterHistoryPageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(h1, h2));

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/histories").withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post("/histories")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(HistoryPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream().size(), output.getStream().size());

    }

    @Test
    void getAllApplicationParametersHistoryLatestTest() {

        ParameterHistory h1 = new ParameterHistory();
        h1.setId("1");
        h1.setKey("key2");
        h1.setUsedValue("value1");

        ParameterHistory h2 = new ParameterHistory();
        h2.setId("2");
        h2.setKey("key2");
        h2.setUsedValue("value2");

        ParameterHistoryPageResult data = new ParameterHistoryPageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(h1, h2));

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/histories/latest").withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(new HistoryCriteriaDTO())
                .post("/histories/latest")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(HistoryPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream().size(), output.getStream().size());
    }

    @Test
    void getApplicationParametersHistoryByIdTest() {

        ParameterHistory data = new ParameterHistory();
        data.setId("test-id-1");
        data.setApplicationId("app1");
        data.setUsedValue("value1");
        data.setKey("key1");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/histories/" + data.getId()).withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", data.getId())
                .get("/histories/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(HistoryDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getId(), output.getId());
        Assertions.assertEquals(data.getApplicationId(), output.getApplicationId());

    }

    @Test
    void getCountsByCriteriaTest() {
        ParameterHistoryCount c1 = new ParameterHistoryCount();
        c1.setCount(1L);
        c1.setCreationDate(OffsetDateTime.now());

        ParameterHistoryCount c2 = new ParameterHistoryCount();
        c1.setCount(2L);
        c1.setCreationDate(OffsetDateTime.now());
        var data = List.of(c1, c2);

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/histories/counts").withMethod(HttpMethod.POST))
                .withPriority(100)
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(new HistoryCountCriteriaDTO())
                .post("/histories/counts")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(HistoryCountDTO[].class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.size(), output.length);
        mockServerClient.clear("mock");
    }

    @Test
    void getCountsByCriteria_Server_error_Test() {
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/histories/counts").withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("/histories/counts")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        mockServerClient.clear("mock");
    }
}
