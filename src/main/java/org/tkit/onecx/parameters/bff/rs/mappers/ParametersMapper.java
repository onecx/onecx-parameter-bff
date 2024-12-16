package org.tkit.onecx.parameters.bff.rs.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;
import gen.org.tkit.onecx.parameters.clients.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ParametersMapper {

    @Mapping(target = "key", source = "name")
    ParameterCreate map(ParameterCreateDTO dto);

    ParameterUpdate mapUpdate(ParameterUpdateDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    HistoryPageResultDTO map(ParameterHistoryPageResult applicationParameterHistoryPageResult);

    @Mapping(target = "name", source = "key")
    HistoryDTO map(ParameterHistory applicationParameterHistory);

    HistoryCountDTO[] map(ParameterHistoryCount[] parameterHistoryCount);

    @Mapping(target = "removeStreamItem", ignore = true)
    ParameterPageResultDTO map(ParameterPageResult applicationParameterPageResult);

    @Mapping(target = "removeStreamItem", ignore = true)
    List<ProductDTO> map(Product[] products);

    @Mapping(target = "displayName", ignore = true)
    @Mapping(target = "removeApplicationsItem", ignore = true)
    ProductDTO map(Product product);

    @Mapping(target = "removeStreamItem", ignore = true)
    KeysPageResultDTO map(KeysPageResult keysPageResult);

    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "name", source = "key")
    ParameterDTO map(Parameter applicationParameter);

    @Mapping(target = "key", source = "name")
    ParameterHistoryCriteria map(HistoryCriteriaDTO criteriaDTO);

    @Mapping(target = "key", source = "name")
    ParameterHistoryCountCriteria map(HistoryCountCriteriaDTO criteriaDTO);

    @Mapping(target = "key", source = "name")
    ParameterSearchCriteria mapCriteria(ParameterSearchCriteriaDTO criteriaDTO);
}
