package org.tkit.onecx.parameters.bff.rs.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;
import gen.org.tkit.onecx.parameters.clients.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ParametersMapper {

    ParameterCreate map(ParameterCreateDTO dto);

    ParameterUpdate mapUpdate(ParameterUpdateDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    ParameterHistoryPageResultDTO map(ParameterHistoryPageResult applicationParameterHistoryPageResult);

    ParameterHistoryDTO map(ParameterHistory applicationParameterHistory);

    ParameterHistoryCountDTO[] map(ParameterHistoryCount[] parameterHistoryCount);

    @Mapping(target = "removeStreamItem", ignore = true)
    ParameterPageResultDTO map(ParameterPageResult applicationParameterPageResult);

    @Mapping(target = "removeStreamItem", ignore = true)
    List<ProductDTO> map(Product[] products);

    @Mapping(target = "displayName", ignore = true)
    @Mapping(target = "removeApplicationsItem", ignore = true)
    ProductDTO map(Product product);

    @Mapping(target = "removeStreamItem", ignore = true)
    KeysPageResultDTO map(KeysPageResult keysPageResult);

    @Mapping(target = "value", source = "value")
    ParameterDTO map(Parameter applicationParameter);

    ParameterHistoryCriteria map(ParameterHistoryCriteriaDTO criteriaDTO);

    ParameterHistoryCountCriteria map(ParameterHistoryCountCriteriaDTO criteriaDTO);

    ParameterSearchCriteria mapCriteria(ParameterSearchCriteriaDTO criteriaDTO);
}
