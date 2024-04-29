package org.tkit.onecx.parameters.bff.rs.controllers;

import java.util.List;

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
import org.tkit.onecx.parameters.bff.rs.mappers.ParametersMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.parameters.bff.clients.api.ParametersApi;
import gen.org.tkit.onecx.parameters.bff.clients.model.*;
import gen.org.tkit.onecx.parameters.bff.rs.internal.ParametersApiService;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ApplicationParameterCreateDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ApplicationParameterUpdateDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProblemDetailResponseDTO;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class ParametersRestController implements ParametersApiService {

    @Inject
    @RestClient
    ParametersApi client;

    @Inject
    ParametersMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

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
    public Response getAllApplicationParameters(String applicationId, String productName, String key, String name,
            Integer pageNumber,
            Integer pageSize, List<String> type) {
        try (Response response = client.getAllApplicationParameters(applicationId, productName, key, name, pageNumber, pageSize,
                type)) {
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
    public Response getAllKeys(String applicationId, String productName) {
        try (Response response = client.getAllKeys(applicationId, productName)) {
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

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public Response restException(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }
}
