package io.github.onecx.parameters.bff.rs;

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

import gen.io.github.onecx.parameters.bff.clients.model.ApplicationParameterHistory;
import gen.io.github.onecx.parameters.bff.clients.model.ApplicationParameterHistoryPageResult;
import gen.io.github.onecx.parameters.bff.clients.model.ParameterHistoryCount;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ApplicationParameterHistoryDTO;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ApplicationParameterHistoryPageResultDTO;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ParameterHistoryCountDTO;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(HistoryRestController.class)
class HistoryRestControllerTest {

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    void getAllApplicationParametersHistoryTest() {

        ApplicationParameterHistory h1 = new ApplicationParameterHistory();
        h1.setId("1");
        h1.setKey("key2");
        h1.setUsedValue("value1");

        ApplicationParameterHistory h2 = new ApplicationParameterHistory();
        h2.setId("2");
        h2.setKey("key2");
        h2.setUsedValue("value2");

        ApplicationParameterHistoryPageResult data = new ApplicationParameterHistoryPageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(h1, h2));

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/histories").withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ApplicationParameterHistoryPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream().size(), output.getStream().size());

    }

    @Test
    void getAllApplicationParametersHistoryLatestTest() {

        ApplicationParameterHistory h1 = new ApplicationParameterHistory();
        h1.setId("1");
        h1.setKey("key2");
        h1.setUsedValue("value1");

        ApplicationParameterHistory h2 = new ApplicationParameterHistory();
        h2.setId("2");
        h2.setKey("key2");
        h2.setUsedValue("value2");

        ApplicationParameterHistoryPageResult data = new ApplicationParameterHistoryPageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(h1, h2));

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/histories/latest").withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .get("latest")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ApplicationParameterHistoryPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream().size(), output.getStream().size());
    }

    @Test
    void getApplicationParametersHistoryByIdTest() {

        ApplicationParameterHistory data = new ApplicationParameterHistory();
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
                .log().all()
                .contentType(APPLICATION_JSON)
                .pathParam("id", data.getId())
                .get("{id}")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ApplicationParameterHistoryDTO.class);

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
        mockServerClient.when(request().withPath("/histories/counts").withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .get("counts")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ParameterHistoryCountDTO[].class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.size(), output.length);

    }
}
