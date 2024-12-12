package org.tkit.onecx.parameters.bff.rs.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ParameterCreateDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ParameterHistoryCountCriteriaDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ParameterHistoryCriteriaDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ParameterUpdateDTO;

@ApplicationScoped
public class ParametersLog implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                this.item(10, ParameterCreateDTO.class,
                        x -> ParameterCreateDTO.class.getSimpleName() + "[key:"
                                + ((ParameterCreateDTO) x).getKey() + "]"),
                this.item(10, ParameterUpdateDTO.class,
                        x -> ParameterUpdateDTO.class.getSimpleName() + "[value:"
                                + ((ParameterUpdateDTO) x).getValue()
                                + "]"),
                this.item(10, ParameterHistoryCountCriteriaDTO.class,
                        x -> ParameterHistoryCountCriteriaDTO.class.getSimpleName() + "[appId:"
                                + ((ParameterHistoryCountCriteriaDTO) x).getApplicationId()
                                + "]"),
                this.item(10,
                        ParameterHistoryCriteriaDTO.class,
                        x -> ParameterHistoryCriteriaDTO.class.getSimpleName() + "[appId:"
                                + ((ParameterHistoryCriteriaDTO) x).getApplicationId()
                                + "]"));
    }
}
