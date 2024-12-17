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
    HistoryPageResultDTO map(HistoryPageResult applicationParameterHistoryPageResult);

    HistoryDTO map(History applicationParameterHistory);

    HistoryCountDTO[] map(HistoryCount[] parameterHistoryCount);

    @Mapping(target = "removeStreamItem", ignore = true)
    ParameterPageResultDTO map(ParameterPageResult applicationParameterPageResult);

    @Mapping(target = "removeStreamItem", ignore = true)
    List<ProductDTO> map(Product[] products);

    @Mapping(target = "removeApplicationsItem", ignore = true)
    ProductDTO map(Product product);

    @Mapping(target = "removeStreamItem", ignore = true)
    KeysPageResultDTO map(KeysPageResult keysPageResult);

    ParameterDTO map(Parameter applicationParameter);

    HistoryCriteria map(HistoryCriteriaDTO criteriaDTO);

    HistoryCountCriteria map(HistoryCountCriteriaDTO criteriaDTO);

    ParameterSearchCriteria mapCriteria(ParameterSearchCriteriaDTO criteriaDTO);
}
