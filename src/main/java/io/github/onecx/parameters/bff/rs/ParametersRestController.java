package io.github.onecx.parameters.bff.rs;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.io.github.onecx.parameters.bff.clients.api.ParametersApi;
import gen.io.github.onecx.parameters.bff.clients.model.ApplicationParameter;
import gen.io.github.onecx.parameters.bff.clients.model.ApplicationParameterPageResult;
import gen.io.github.onecx.parameters.bff.clients.model.ApplicationsPageResult;
import gen.io.github.onecx.parameters.bff.clients.model.KeysPageResult;
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
        try (Response response = client.createParameterValue(mapper.map(applicationParameterCreateDTO))) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response deleteParameter(String id) {
        try (Response response = client.deleteParameter(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAllApplicationParameters(String applicationId, String key, String name, Integer pageNumber,
            Integer pageSize, List<String> type) {
        try (Response response = client.getAllApplicationParameters(applicationId, key, name, pageNumber, pageSize, type)) {
            var result = mapper.map(response.readEntity(ApplicationParameterPageResult.class));
            return Response.status(response.getStatus()).entity(result).build();
        }
    }

    @Override
    public Response getAllApplications() {
        try (Response response = client.getAllApplications()) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(ApplicationsPageResult.class)))
                    .build();
        }
    }

    @Override
    public Response getAllKeys(String applicationId) {
        try (Response response = client.getAllKeys(applicationId)) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(KeysPageResult.class))).build();
        }
    }

    @Override
    public Response getParameterById(String id) {
        try (Response response = client.getParameterById(id)) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(ApplicationParameter.class)))
                    .build();
        }
    }

    @Override
    public Response updateParameterValue(String id, ApplicationParameterUpdateDTO applicationParameterUpdateDTO) {
        try (Response response = client.updateParameterValue(id, mapper.mapUpdate(applicationParameterUpdateDTO))) {
            return Response.status(response.getStatus()).build();
        }
    }
}
