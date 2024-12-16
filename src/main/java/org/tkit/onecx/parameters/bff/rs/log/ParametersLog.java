package org.tkit.onecx.parameters.bff.rs.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.HistoryCountCriteriaDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.HistoryCriteriaDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ParameterCreateDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ParameterUpdateDTO;

@ApplicationScoped
public class ParametersLog implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                this.item(10, ParameterCreateDTO.class,
                        x -> ParameterCreateDTO.class.getSimpleName() + "[key:"
                                + ((ParameterCreateDTO) x).getName() + "]"),
                this.item(10, ParameterUpdateDTO.class,
                        x -> ParameterUpdateDTO.class.getSimpleName() + "[value:"
                                + ((ParameterUpdateDTO) x).getValue()
                                + "]"),
                this.item(10, HistoryCountCriteriaDTO.class,
                        x -> HistoryCountCriteriaDTO.class.getSimpleName() + "[appId:"
                                + ((HistoryCountCriteriaDTO) x).getApplicationId()
                                + "]"),
                this.item(10,
                        HistoryCriteriaDTO.class,
                        x -> HistoryCriteriaDTO.class.getSimpleName() + "[appId:"
                                + ((HistoryCriteriaDTO) x).getApplicationId()
                                + "]"));
    }
}
