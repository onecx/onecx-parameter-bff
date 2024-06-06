package org.tkit.onecx.parameters.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.List;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.parameters.bff.rs.controllers.ProductsRestController;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProductStorePageResultDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProductStoreSearchCriteriaDTO;
import gen.org.tkit.onecx.product.store.clients.model.*;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ProductsRestController.class)
public class ProductsRestControllerTest extends AbstractTest {

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    void searchAllAvailableProducts() {

        ProductItemLoadSearchCriteriaPSV1 svcCriteria = new ProductItemLoadSearchCriteriaPSV1();
        svcCriteria.pageNumber(0).pageSize(100);

        ProductsLoadResultPSV1 svcResult = new ProductsLoadResultPSV1();
        ProductsAbstractPSV1 productItem = new ProductsAbstractPSV1();
        productItem.basePath("test").name("test").classifications("search");
        productItem.setMicroservices(List.of(new MicroserviceAbstractPSV1().appName("app1").appId("app1")));
        svcResult.number(0).totalElements(1L).totalPages(1L).stream(List.of(productItem));

        // create mock rest endpoint
        mockServerClient
                .when(request().withPath("/v1/products/load")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(svcCriteria)))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(svcResult)));

        ProductStoreSearchCriteriaDTO storeSearchCriteriaDTO = new ProductStoreSearchCriteriaDTO();

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(storeSearchCriteriaDTO)
                .post()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProductStorePageResultDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(output.getStream().get(0).getProductName(), productItem.getName());

        Assertions.assertEquals(output.getStream().get(0).getApplications().get(0),
                productItem.getMicroservices().get(0).getAppId());
        mockServerClient.clear("mock");
    }

    @Test
    void searchAllAvailableProducts_PS_server_error_Test() {

        ProductItemLoadSearchCriteriaPSV1 svcCriteria = new ProductItemLoadSearchCriteriaPSV1();
        svcCriteria.pageNumber(0).pageSize(100);

        // create mock rest endpoint
        mockServerClient
                .when(request().withPath("/v1/products/load")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(svcCriteria)))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

        ProductStoreSearchCriteriaDTO storeSearchCriteriaDTO = new ProductStoreSearchCriteriaDTO();

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(storeSearchCriteriaDTO)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        mockServerClient.clear("mock");
    }

    @Test
    void searchAllAvailableProducts_missing_body_Test() {

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}
