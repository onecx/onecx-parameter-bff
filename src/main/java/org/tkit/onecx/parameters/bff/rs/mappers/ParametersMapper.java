package org.tkit.onecx.parameters.bff.rs.mappers;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.*;
import gen.org.tkit.onecx.parameters.clients.model.*;
import gen.org.tkit.onecx.product.store.clients.model.MicroserviceAbstractPSV1;
import gen.org.tkit.onecx.product.store.clients.model.ProductsAbstractPSV1;
import gen.org.tkit.onecx.product.store.clients.model.ProductsLoadResultPSV1;

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

    default ProductWrapperDTO createWrapper(Product[] usedProducts) {
        ProductWrapperDTO result = new ProductWrapperDTO();
        result.setUsedProducts(map(usedProducts));
        return result;
    }

    default ProductWrapperDTO maps(ProductWrapperDTO wrapper, ProductsLoadResultPSV1 data) {
        if (data != null && !data.getStream().isEmpty()) {
            var map = wrapper.getUsedProducts().stream().collect(toMap(ProductDTO::getProductName, p -> p));
            List<ProductDTO> list = new ArrayList<>(data.getStream().size());
            for (ProductsAbstractPSV1 product : data.getStream()) {
                var item = map(product);
                var p = map.get(item.getProductName());
                if (p != null) {
                    p.setDisplayName(item.getDisplayName());
                }
                list.add(item);
            }
            wrapper.setProducts(list);
        }
        return wrapper;
    }

    List<ProductDTO> maps(List<ProductsAbstractPSV1> data);

    @Mapping(target = "removeApplicationsItem", ignore = true)
    @Mapping(target = "productName", source = "name")
    @Mapping(target = "applications", source = "microservices")
    ProductDTO map(ProductsAbstractPSV1 psProduct);

    default List<String> map(List<MicroserviceAbstractPSV1> apps) {
        List<String> appIds = new ArrayList<>();
        if (apps != null) {
            apps.forEach(microserviceAbstractPSV1 -> appIds.add(microserviceAbstractPSV1.getAppId()));
        }
        return appIds;
    }

    @Mapping(target = "displayName", ignore = true)
    @Mapping(target = "removeApplicationsItem", ignore = true)
    ProductDTO map(Product product);

    @Mapping(target = "removeStreamItem", ignore = true)
    NamesPageResultDTO map(NamesPageResult keysPageResult);

    ParameterDTO map(Parameter applicationParameter);

    HistoryCriteria map(HistoryCriteriaDTO criteriaDTO);

    HistoryCountCriteria map(HistoryCountCriteriaDTO criteriaDTO);

    ParameterSearchCriteria mapCriteria(ParameterSearchCriteriaDTO criteriaDTO);

    ExportParameterRequest mapExport(ExportParameterRequestDTO exportParameterRequestDTO);

    ParameterSnapshot mapImport(ParameterSnapshotDTO parameterSnapshotDTO);

    @Mapping(target = "products", source = "products")
    Map<String, List<EximParameter>> map(Map<String, List<EximParameterDTO>> map);

    default List<EximParameter> mapEximParameterList(List<EximParameterDTO> dtoList) {
        return dtoList.stream()
                .map(this::map).toList();
    }

    @Mapping(target = "products", source = "products")
    Map<String, List<EximParameterDTO>> mapToDTOMap(Map<String, List<EximParameter>> map);

    default List<EximParameterDTO> mapEximParameterDTOList(List<EximParameter> dtoList) {
        return dtoList.stream()
                .map(this::mapToDTO).toList();
    }

    EximParameter map(EximParameterDTO eximParameter);

    EximParameterDTO mapToDTO(EximParameter eximParameter);

    @Mapping(target = "removeProductsItem", ignore = true)
    ParameterSnapshotDTO mapSnapshot(ParameterSnapshot snapshot);

    @Mapping(target = "removeParametersItem", ignore = true)
    ImportParameterResponseDTO mapImportResult(ImportParameterResponse importResult);

}
