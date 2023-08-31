package io.github.onecx.parameters.bff.rs;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.io.github.onecx.parameters.bff.clients.api.ParametersApi;
import gen.io.github.onecx.parameters.bff.rs.internal.ParametersApiService;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ApplicationParameterCreateDTO;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ApplicationParameterUpdateDTO;
import io.github.onecx.parameters.bff.rs.mappers.ParametersMapper;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
public class ParametersRestController implements ParametersApiService {

    @Inject
    @RestClient
    ParametersApi client;

    @Inject
    ParametersMapper mapper;

    @Override
    public Response createParameterValue(ApplicationParameterCreateDTO applicationParameterCreateDTO) {
        var dto = mapper.map(applicationParameterCreateDTO);
        return Response.fromResponse(client.createParameterValue(dto)).build();
    }

    @Override
    public Response deleteParameter(String id) {
        return Response.fromResponse(client.deleteParameter(id)).build();
    }

    @Override
    public Response getAllApplicationParameters(String applicationId, String key, String name, Integer pageNumber,
            Integer pageSize, List<String> type) {
        return Response.fromResponse(client.getAllApplicationParameters(applicationId, key, name, pageNumber, pageSize, type))
                .build();
    }

    @Override
    public Response getAllApplications() {
        return Response.fromResponse(client.getAllApplications()).build();
    }

    @Override
    public Response getAllKeys(String applicationId) {
        return Response.fromResponse(client.getAllKeys(applicationId)).build();
    }

    @Override
    public Response getParameterById(String id) {
        return Response.fromResponse(client.getParameterById(id)).build();
    }

    @Override
    public Response updateParameterValue(String id, ApplicationParameterUpdateDTO applicationParameterUpdateDTO) {
        var dto = mapper.mapUpdate(applicationParameterUpdateDTO);
        return Response.fromResponse(client.updateParameterValue(id, dto)).build();
    }
}
