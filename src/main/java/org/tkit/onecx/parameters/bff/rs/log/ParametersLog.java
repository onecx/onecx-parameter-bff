package org.tkit.onecx.parameters.bff.rs.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ApplicationParameterCreateDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ApplicationParameterHistoryCriteriaDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ApplicationParameterUpdateDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ParameterHistoryCountCriteriaDTO;

@ApplicationScoped
public class ParametersLog implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                this.item(10, ApplicationParameterCreateDTO.class,
                        x -> ApplicationParameterCreateDTO.class.getSimpleName() + "[key:"
                                + ((ApplicationParameterCreateDTO) x).getKey() + "]"),
                this.item(10, ApplicationParameterUpdateDTO.class,
                        x -> ApplicationParameterUpdateDTO.class.getSimpleName() + "[value:"
                                + ((ApplicationParameterUpdateDTO) x).getValue()
                                + "]"),
                this.item(10, ParameterHistoryCountCriteriaDTO.class,
                        x -> ParameterHistoryCountCriteriaDTO.class.getSimpleName() + "[appId:"
                                + ((ParameterHistoryCountCriteriaDTO) x).getApplicationId()
                                + "]"),
                this.item(10,
                        ApplicationParameterHistoryCriteriaDTO.class,
                        x -> ApplicationParameterHistoryCriteriaDTO.class.getSimpleName() + "[appId:"
                                + ((ApplicationParameterHistoryCriteriaDTO) x).getApplicationId()
                                + "]"));
    }
}
