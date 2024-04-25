package io.github.onecx.parameters.bff.rs;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.io.github.onecx.parameters.bff.clients.api.HistoriesApi;
import gen.io.github.onecx.parameters.bff.clients.model.ApplicationParameterHistory;
import gen.io.github.onecx.parameters.bff.clients.model.ApplicationParameterHistoryPageResult;
import gen.io.github.onecx.parameters.bff.clients.model.ParameterHistoryCount;
import gen.io.github.onecx.parameters.bff.rs.internal.HistoriesApiService;
import io.github.onecx.parameters.bff.rs.mappers.ParametersMapper;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
public class HistoryRestController implements HistoriesApiService {

    @Inject
    @RestClient
    HistoriesApi client;

    @Inject
    ParametersMapper mapper;

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
}
