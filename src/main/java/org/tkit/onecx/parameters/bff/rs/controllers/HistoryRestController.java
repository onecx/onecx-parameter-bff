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

import gen.org.tkit.onecx.parameters.bff.rs.internal.HistoriesApiService;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.HistoryCountCriteriaDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.HistoryCriteriaDTO;
import gen.org.tkit.onecx.parameters.clients.api.HistoriesApi;
import gen.org.tkit.onecx.parameters.clients.model.ParameterHistory;
import gen.org.tkit.onecx.parameters.clients.model.ParameterHistoryCount;
import gen.org.tkit.onecx.parameters.clients.model.ParameterHistoryPageResult;

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
    public Response getAllHistory(HistoryCriteriaDTO criteriaDTO) {
        var criteria = mapper.map(criteriaDTO);
        try (Response response = client.getAllParametersHistory(criteria)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(ParameterHistoryPageResult.class))).build();
        }
    }

    @Override
    public Response getAllHistoryLatest(HistoryCriteriaDTO criteriaDTO) {
        try (Response response = client.getAllParametersHistoryLatest(mapper.map(criteriaDTO))) {
            var result = mapper.map(response.readEntity(ParameterHistoryPageResult.class));
            return Response.status(response.getStatus()).entity(result).build();
        }
    }

    @Override
    public Response getHistoryById(String id) {
        try (Response response = client.getParametersHistoryById(id)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(ParameterHistory.class))).build();
        }
    }

    @Override
    public Response getCountsByCriteria(HistoryCountCriteriaDTO criteriaDTO) {
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
