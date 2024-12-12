package org.tkit.onecx.parameters.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;

import gen.org.tkit.onecx.parameters.bff.clients.model.*;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ParametersRestControllerTest extends AbstractTest {
    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    void getAllApplicationsTest() {

        List<Product> data = new ArrayList<>();
        Product product = new Product();
        product.setProductName("p1");
        product.setApplications(List.of("app1", "app2", "app3"));
        data.add(product);

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters/applications").withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("/parameters/applications")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProductDTO[].class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.size(), output.length);
        Assertions.assertEquals(data.get(0).getApplications().size(), output[0].getApplications().size());
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
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("/parameters/keys")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(KeysPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream(), output.getStream());
    }

    @Test
    void createParameterValueTest() {

        ApplicationParameterCreate data = new ApplicationParameterCreate();
        data.setApplicationId("app1");
        data.setProductName("product1");
        data.setKey("key1");
        data.setValue("value1");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(data)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        ParameterCreateDTO input = new ParameterCreateDTO();
        input.setApplicationId("app1");
        input.setValue("value1");
        input.setProductName("product1");
        input.setKey("key1");

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(input)
                .post("/parameters")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        //create with no body should return BAD_REQUEST
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post("/parameters")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void deleteParameterTest() {

        String id = "test-id-1";

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters/" + id).withMethod(HttpMethod.DELETE))
                .withPriority(100)
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", id)
                .delete("/parameters/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        mockServerClient.clear("mock");
    }

    @Test
    void deleteParameter_BAD_REQUEST_Test() {

        String id = "test-id-1";

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters/" + id).withMethod(HttpMethod.DELETE))
                .withPriority(100)
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", id)
                .delete("/parameters/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        mockServerClient.clear("mock");
    }

    @Test
    void searchApplicationParametersTest() {
        ApplicationParameter p1 = new ApplicationParameter();
        p1.setId("1");
        p1.setKey("key2");
        p1.setValue("value1");

        ApplicationParameter p2 = new ApplicationParameter();
        p2.setId("2");
        p2.setKey("key2");
        p2.setValue("value2");

        ApplicationParameterPageResult data = new ApplicationParameterPageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(p1, p2));

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters/search").withMethod(HttpMethod.POST)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(JsonBody.json(new ParameterSearchCriteria().applicationId("app1").pageNumber(0).pageSize(5))))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(new ParameterSearchCriteriaDTO().applicationId("app1").pageNumber(0).pageSize(5))
                .post("/parameters/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ParameterPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream().size(), output.getStream().size());
    }

    @Test
    void getParameterByIdTest() {

        ApplicationParameter data = new ApplicationParameter();
        data.setId("test-id-1");
        data.setApplicationId("app1");
        data.setValue("value1");
        data.setKey("key1");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters/" + data.getId()).withMethod(HttpMethod.GET))
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
                .get("/parameters/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ParameterDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getId(), output.getId());
        Assertions.assertEquals(data.getApplicationId(), output.getApplicationId());
    }

    @Test
    void updateParameterValueTest() {

        String id = "test-update-1";

        ApplicationParameterUpdate data = new ApplicationParameterUpdate();
        data.description("description");
        data.setValue("value1");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/parameters/" + id).withMethod(HttpMethod.PUT)
                .withBody(JsonBody.json(data)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        ParameterUpdateDTO input = new ParameterUpdateDTO();
        input.description("description");
        input.setValue("value1");

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", id)
                .body(input)
                .put("/parameters/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}
