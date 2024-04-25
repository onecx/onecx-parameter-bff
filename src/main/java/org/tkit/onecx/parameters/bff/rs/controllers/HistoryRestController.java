package org.tkit.onecx.parameters.bff.rs.controllers;

import java.util.List;

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
    public Response getAllApplicationParametersHistory(String applicationId, String key, Integer pageNumber, Integer pageSize,
            List<String> type) {
        try (Response response = client.getAllApplicationParametersHistory(applicationId, key, pageNumber, pageSize, type)) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(ApplicationParameterHistoryPageResult.class))).build();
        }
    }

    @Override
    public Response getAllApplicationParametersHistoryLatest(String applicationId, String key, Integer pageNumber,
            Integer pageSize, List<String> type) {
        try (Response response = client.getAllApplicationParametersHistoryLatest(applicationId, key, pageNumber, pageSize,
                type)) {
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
    public Response getCountsByCriteria(String applicationId, String key, Integer pageNumber, Integer pageSize,
            List<String> type) {
        try (Response response = client.getCountsByCriteria(applicationId, key, pageNumber, pageSize, type)) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(ParameterHistoryCount[].class)))
                    .build();
        }
    }

    @ServerExceptionMapper
    public Response restException(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }
}
