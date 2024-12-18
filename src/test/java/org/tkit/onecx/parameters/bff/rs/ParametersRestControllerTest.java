package org.tkit.onecx.parameters.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.parameters.bff.rs.controllers.ParametersRestController;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;
import gen.org.tkit.onecx.parameters.clients.model.*;
import gen.org.tkit.onecx.product.store.clients.model.MicroserviceAbstractPSV1;
import gen.org.tkit.onecx.product.store.clients.model.ProductItemLoadSearchCriteriaPSV1;
import gen.org.tkit.onecx.product.store.clients.model.ProductsAbstractPSV1;
import gen.org.tkit.onecx.product.store.clients.model.ProductsLoadResultPSV1;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ParametersRestController.class)
class ParametersRestControllerTest extends AbstractTest {
    @InjectMockServerClient
    MockServerClient mockServerClient;

    @BeforeEach
    void resetExpectation() {
        clearExpectation(mockServerClient);
    }

    @Test
    void getAllApplicationsTest() {

        List<Product> data = new ArrayList<>();
        Product product = new Product();
        product.setProductName("p1");
        product.setApplications(List.of("app1", "app2", "app3"));
        data.add(product);

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters/applications").withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data))));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("applications")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProductDTO[].class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.size(), output.length);
        Assertions.assertEquals(data.get(0).getApplications().size(), output[0].getApplications().size());
    }

    @Test
    void getAllNamesTest() {

        NamesPageResult data = new NamesPageResult();
        data.setNumber(1);
        data.setSize(3);
        data.setTotalElements(3L);
        data.setTotalPages(1L);
        data.setStream(List.of("key1", "key2", "key3"));

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters/names/p1")
                .withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data))));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("names/p1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(NamesPageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream(), output.getStream());
    }

    @Test
    void createParameterValueTest() {

        ParameterCreate data = new ParameterCreate();
        data.setApplicationId("app1");
        data.setProductName("product1");
        data.setName("key1");
        data.setValue(100);

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(data)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)));

        ParameterCreateDTO input = new ParameterCreateDTO();
        input.setApplicationId("app1");
        input.setValue(100);
        input.setProductName("product1");
        input.setName("key1");

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        //create with nobody should return BAD_REQUEST
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void createParameterBadRequestTest() {
        var problem = new ProblemDetailResponse()
                .errorCode("PERSIST_ENTITY_FAILED")
                .detail("could not execute statement [ERROR: duplicate key value violates unique constraint")
                .addParamsItem(new ProblemDetailParam().key("constraint")
                        .value("could not execute statement [ERROR: duplicate key value violates unique constraint"));

        var data = new ParameterCreate().applicationId("app1").productName("product1").name("key1").value(100);

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(data)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(problem))));

        ParameterCreateDTO input = new ParameterCreateDTO().applicationId("app1")
                .value(100).productName("product1").name("key1");

        var response = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then().log().all()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(problem.getErrorCode(), response.getErrorCode());
    }

    @Test
    void deleteParameterTest() {

        String id = "test-id-1";

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters/" + id).withMethod(HttpMethod.DELETE))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .delete(id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }

    @Test
    void deleteParameter_BAD_REQUEST_Test() {

        String id = "test-id-1";

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters/" + id).withMethod(HttpMethod.DELETE))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .delete(id)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

    }

    @Test
    void searchApplicationParametersTest() {
        Parameter p1 = new Parameter();
        p1.setId("1");
        p1.setName("key2");
        p1.setValue("value1");

        Parameter p2 = new Parameter();
        p2.setId("2");
        p2.setName("key2");
        p2.setValue("value2");

        ParameterPageResult data = new ParameterPageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(p1, p2));

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters/search").withMethod(HttpMethod.POST)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(JsonBody.json(new ParameterSearchCriteria().applicationId("app1").pageNumber(0).pageSize(5))))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data))));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(new ParameterSearchCriteriaDTO().applicationId("app1").pageNumber(0).pageSize(5))
                .post("search")
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

        Parameter data = new Parameter();
        data.setId("test-id-1");
        data.setApplicationId("app1");
        data.setValue(Map.of("key1", "value1", "key2", Map.of("s1", "v1")));
        data.setName("key1");

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters/" + data.getId()).withMethod(HttpMethod.GET))
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
                .then().log().all()
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

        ParameterUpdate data = new ParameterUpdate();
        data.description("description");
        data.setValue(100);

        // create mock rest endpoint
        addExpectation(mockServerClient.when(request().withPath("/parameters/" + id).withMethod(HttpMethod.PUT)
                .withBody(JsonBody.json(data)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)));

        ParameterUpdateDTO input = new ParameterUpdateDTO();
        input.description("description");

        input.setValue(data.getValue());

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(input)
                .put(id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void getProductsTest() {

        ProductItemLoadSearchCriteriaPSV1 svcCriteria = new ProductItemLoadSearchCriteriaPSV1().pageNumber(0).pageSize(1000);

        ProductsLoadResultPSV1 svcResult = new ProductsLoadResultPSV1()
                .number(0)
                .totalElements(1L)
                .totalPages(1L)
                .addStreamItem(
                        new ProductsAbstractPSV1()
                                .basePath("test1")
                                .name("test1")
                                .displayName("test1")
                                .addMicroservicesItem(new MicroserviceAbstractPSV1().appName("app1").appId("app1")))
                .addStreamItem(
                        new ProductsAbstractPSV1()
                                .basePath("test2")
                                .name("test2")
                                .displayName("CustomDisplayName")
                                .addMicroservicesItem(new MicroserviceAbstractPSV1().appName("app1").appId("app1")))
                .addStreamItem(
                        new ProductsAbstractPSV1()
                                .basePath("test3")
                                .name("test3")
                                .displayName("test3")
                                .addMicroservicesItem(new MicroserviceAbstractPSV1().appName("app1").appId("app1")));

        // create mock rest endpoint
        addExpectation(mockServerClient
                .when(request().withPath("/v1/products/load")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(svcCriteria)))
                .withPriority(2000)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(svcResult))));

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
        Assertions.assertEquals(3, wrapper.getProducts().size());
        Assertions.assertNotNull(wrapper.getUsedProducts());
        Assertions.assertEquals(1, wrapper.getUsedProducts().size());
        Assertions.assertEquals("CustomDisplayName", wrapper.getUsedProducts().get(0).getDisplayName());
    }
}
