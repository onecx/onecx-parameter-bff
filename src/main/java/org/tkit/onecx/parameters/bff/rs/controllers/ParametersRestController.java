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
import org.tkit.onecx.parameters.bff.rs.mappers.ParametersMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.parameters.bff.rs.internal.ParametersApiService;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;
import gen.org.tkit.onecx.parameters.clients.api.ParametersApi;
import gen.org.tkit.onecx.parameters.clients.model.*;

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
    public Response createParameter(ParameterCreateDTO applicationParameterCreateDTO) {
        try (Response response = client.createParameter(mapper.map(applicationParameterCreateDTO))) {
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
    public Response searchParametersByCriteria(ParameterSearchCriteriaDTO criteriaDTO) {
        try (Response response = client.searchParametersByCriteria(mapper.mapCriteria(criteriaDTO))) {
            var result = mapper.map(response.readEntity(ParameterPageResult.class));
            return Response.status(response.getStatus()).entity(result).build();
        }
    }

    @Override
    public Response getAllProducts() {
        try (Response response = client.getAllProducts()) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(Product[].class)))
                    .build();
        }
    }

    @Override
    public Response getAllNames(String productName, String applicationId) {
        try (Response response = client.getAllNames(productName, applicationId)) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(NamesPageResult.class))).build();
        }
    }

    @Override
    public Response getParameterById(String id) {
        try (Response response = client.getParameterById(id)) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(Parameter.class)))
                    .build();
        }
    }

    @Override
    public Response exportParameters(ExportParameterRequestDTO exportParameterRequestDTO) {
        try (Response response = client.exportParameters(mapper.mapExport(exportParameterRequestDTO))) {
            var snapshot = response.readEntity(ParameterSnapshot.class);
            return Response.status(response.getStatus()).entity(mapper.mapSnapshot(snapshot)).build();
        }
    }

    @Override
    public Response importParameters(ParameterSnapshotDTO parameterSnapshotDTO) {
        try (Response response = client.importParameters(mapper.mapImport(parameterSnapshotDTO))) {
            var importResult = response.readEntity(ImportParameterResponse.class);
            return Response.status(response.getStatus()).entity(mapper.mapImportResult(importResult)).build();
        }
    }

    @Override
    public Response updateParameter(String id, ParameterUpdateDTO applicationParameterUpdateDTO) {
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
