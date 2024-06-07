package org.tkit.onecx.parameters.bff.rs.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.parameters.bff.clients.model.*;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ParametersMapper {

    ApplicationParameterCreate map(ApplicationParameterCreateDTO dto);

    ApplicationParameterUpdate mapUpdate(ApplicationParameterUpdateDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    ApplicationParameterHistoryPageResultDTO map(ApplicationParameterHistoryPageResult applicationParameterHistoryPageResult);

    ApplicationParameterHistoryDTO map(ApplicationParameterHistory applicationParameterHistory);

    ParameterHistoryCountDTO[] map(ParameterHistoryCount[] parameterHistoryCount);

    @Mapping(target = "removeStreamItem", ignore = true)
    ApplicationParameterPageResultDTO map(ApplicationParameterPageResult applicationParameterPageResult);

    @Mapping(target = "removeStreamItem", ignore = true)
    List<ProductDTO> map(Product[] products);

    @Mapping(target = "removeApplicationsItem", ignore = true)
    ProductDTO map(Product product);

    @Mapping(target = "removeStreamItem", ignore = true)
    KeysPageResultDTO map(KeysPageResult keysPageResult);

    @Mapping(target = "value", source = "setValue")
    ApplicationParameterDTO map(ApplicationParameter applicationParameter);

    ApplicationParameterHistoryCriteria map(ApplicationParameterHistoryCriteriaDTO criteriaDTO);

    ParameterHistoryCountCriteria map(ParameterHistoryCountCriteriaDTO criteriaDTO);

    ParameterSearchCriteria mapCriteria(ParameterSearchCriteriaDTO criteriaDTO);
}
