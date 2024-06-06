package org.tkit.onecx.parameters.bff.rs.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.parameters.bff.rs.mappers.ExceptionMapper;
import org.tkit.onecx.parameters.bff.rs.mappers.ProductsMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.parameters.bff.rs.internal.ProductsApiService;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProductStoreSearchCriteriaDTO;
import gen.org.tkit.onecx.product.store.clients.api.ProductsApi;
import gen.org.tkit.onecx.product.store.clients.model.ProductsLoadResultPSV1;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class ProductsRestController implements ProductsApiService {

    @Inject
    @RestClient
    ProductsApi productsApi;

    @Inject
    ProductsMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response searchAllAvailableProducts(ProductStoreSearchCriteriaDTO productStoreSearchCriteriaDTO) {
        try (Response response = productsApi.loadProductsByCriteria(mapper.map(productStoreSearchCriteriaDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.maps(response.readEntity(ProductsLoadResultPSV1.class))).build();
        }
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public Response restException(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }
}
