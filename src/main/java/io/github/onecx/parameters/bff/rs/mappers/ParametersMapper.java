package io.github.onecx.parameters.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.parameters.bff.clients.model.ApplicationParameterCreate;
import gen.io.github.onecx.parameters.bff.clients.model.ApplicationParameterUpdate;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ApplicationParameterCreateDTO;
import gen.io.github.onecx.parameters.bff.rs.internal.model.ApplicationParameterUpdateDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ParametersMapper {

    ApplicationParameterCreate map(ApplicationParameterCreateDTO dto);

    ApplicationParameterUpdate mapUpdate(ApplicationParameterUpdateDTO dto);
}
