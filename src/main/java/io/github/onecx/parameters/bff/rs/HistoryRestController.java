package io.github.onecx.parameters.bff.rs;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.io.github.onecx.parameters.bff.clients.api.HistoriesApi;
import gen.io.github.onecx.parameters.bff.rs.internal.HistoriesApiService;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
public class HistoryRestController implements HistoriesApiService {

    @Inject
    @RestClient
    HistoriesApi client;

    @Override
    public Response getAllApplicationParametersHistory(String applicationId, String key, Integer pageNumber, Integer pageSize,
            List<String> type) {
        return Response.fromResponse(client.getAllApplicationParametersHistory(applicationId, key, pageNumber, pageSize, type))
                .build();
    }

    @Override
    public Response getAllApplicationParametersHistoryLatest(String applicationId, String key, Integer pageNumber,
            Integer pageSize, List<String> type) {
        return Response
                .fromResponse(client.getAllApplicationParametersHistoryLatest(applicationId, key, pageNumber, pageSize, type))
                .build();
    }

    @Override
    public Response getApplicationParametersHistoryById(String id) {
        System.out.println("############ -> " + id);
        return Response.fromResponse(client.getApplicationParametersHistoryById(id)).build();
    }

    @Override
    public Response getCountsByCriteria(String applicationId, String key, Integer pageNumber, Integer pageSize,
            List<String> type) {
        return Response.fromResponse(client.getCountsByCriteria(applicationId, key, pageNumber, pageSize, type)).build();
    }
}
