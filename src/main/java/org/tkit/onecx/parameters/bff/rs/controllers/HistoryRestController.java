package org.tkit.onecx.parameters.bff.rs.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.parameters.bff.rs.mappers.ExceptionMapper;
import org.tkit.onecx.parameters.bff.rs.mappers.ParametersMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.parameters.bff.clients.api.HistoriesApi;
import gen.org.tkit.onecx.parameters.bff.clients.model.ApplicationParameterHistory;
import gen.org.tkit.onecx.parameters.bff.clients.model.ApplicationParameterHistoryPageResult;
import gen.org.tkit.onecx.parameters.bff.clients.model.ParameterHistoryCount;
import gen.org.tkit.onecx.parameters.bff.rs.internal.HistoriesApiService;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ApplicationParameterHistoryCriteriaDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ParameterHistoryCountCriteriaDTO;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class HistoryRestController implements HistoriesApiService {

    @Inject
    @RestClient
    HistoriesApi client;

    @Inject
    ParametersMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response getAllApplicationParametersHistory(ApplicationParameterHistoryCriteriaDTO criteriaDTO) {
        var criteria = mapper.map(criteriaDTO);
        try (Response response = client.getAllApplicationParametersHistory(criteria)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(ApplicationParameterHistoryPageResult.class))).build();
        }
    }

    @Override
    public Response getAllApplicationParametersHistoryLatest(ApplicationParameterHistoryCriteriaDTO criteriaDTO) {
        try (Response response = client.getAllApplicationParametersHistoryLatest(mapper.map(criteriaDTO))) {
            var result = mapper.map(response.readEntity(ApplicationParameterHistoryPageResult.class));
            return Response.status(response.getStatus()).entity(result).build();
        }
    }

    @Override
    public Response getApplicationParametersHistoryById(String id) {
        try (Response response = client.getApplicationParametersHistoryById(id)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(ApplicationParameterHistory.class))).build();
        }
    }

    @Override
    public Response getCountsByCriteria(ParameterHistoryCountCriteriaDTO criteriaDTO) {
        var criteria = mapper.map(criteriaDTO);
        try (Response response = client.getCountsByCriteria(criteria)) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(ParameterHistoryCount[].class)))
                    .build();
        }
    }

    @ServerExceptionMapper
    public Response restException(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }
}
