package org.tkit.onecx.parameters.bff.rs.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProductDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProductStorePageResultDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ProductStoreSearchCriteriaDTO;
import gen.org.tkit.onecx.product.store.clients.model.MicroserviceAbstractPSV1;
import gen.org.tkit.onecx.product.store.clients.model.ProductItemLoadSearchCriteriaPSV1;
import gen.org.tkit.onecx.product.store.clients.model.ProductsAbstractPSV1;
import gen.org.tkit.onecx.product.store.clients.model.ProductsLoadResultPSV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductsMapper {
    @Mapping(target = "productNames", ignore = true)
    ProductItemLoadSearchCriteriaPSV1 map(ProductStoreSearchCriteriaDTO productStoreSearchCriteriaDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductStorePageResultDTO maps(ProductsLoadResultPSV1 productsLoadResultPSV1);

    @Mapping(target = "removeApplicationsItem", ignore = true)
    @Mapping(target = "productName", source = "name")
    @Mapping(target = "applications", source = "microservices")
    ProductDTO map(ProductsAbstractPSV1 psProduct);

    default List<String> map(List<MicroserviceAbstractPSV1> apps) {
        List<String> appIds = new ArrayList<>();
        if (!apps.isEmpty()) {
            apps.forEach(microserviceAbstractPSV1 -> appIds.add(microserviceAbstractPSV1.getAppId()));
        }
        return appIds;
    }
}
