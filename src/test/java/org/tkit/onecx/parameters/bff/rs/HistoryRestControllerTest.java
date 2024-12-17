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
import org.tkit.onecx.parameters.bff.rs.controllers.HistoryRestController;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;
import gen.org.tkit.onecx.parameters.clients.model.History;
import gen.org.tkit.onecx.parameters.clients.model.HistoryCount;
import gen.org.tkit.onecx.parameters.clients.model.HistoryPageResult;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(HistoryRestController.class)
class HistoryRestControllerTest extends AbstractTest {

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    void getAllApplicationParametersHistoryTest() {

        History h1 = new History();
        h1.setId("1");
        h1.setName("key2");
        h1.setUsedValue("value1");

        History h2 = new History();
        h2.setId("2");
        h2.setName("key2");
        h2.setUsedValue("value2");

        HistoryPageResult data = new HistoryPageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(h1, h2));

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/histories").withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data))));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post()
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

        History h1 = new History();
        h1.setId("1");
        h1.setName("key2");
        h1.setUsedValue("value1");

        History h2 = new History();
        h2.setId("2");
        h2.setName("key2");
        h2.setUsedValue("value2");

        HistoryPageResult data = new HistoryPageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(h1, h2));

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/histories/latest").withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data))));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(new HistoryCriteriaDTO())
                .post("latest")
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

        History data = new History();
        data.setId("test-id-1");
        data.setApplicationId("app1");
        data.setUsedValue("value1");
        data.setName("key1");

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/histories/" + data.getId()).withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data))));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get(data.getId())
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
        HistoryCount c1 = new HistoryCount();
        c1.setCount(1L);
        c1.setCreationDate(OffsetDateTime.now());

        HistoryCount c2 = new HistoryCount();
        c1.setCount(2L);
        c1.setCreationDate(OffsetDateTime.now());
        var data = List.of(c1, c2);

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/histories/counts").withMethod(HttpMethod.POST))
                .withPriority(100)
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data))));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(new HistoryCountCriteriaDTO())
                .post("counts")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(HistoryCountDTO[].class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.size(), output.length);
    }

    @Test
    void getCountsByCriteria_Server_error_Test() {
        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/histories/counts").withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("/counts")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}
