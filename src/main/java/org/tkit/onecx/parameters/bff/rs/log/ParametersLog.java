package org.tkit.onecx.parameters.bff.rs.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ApplicationParameterCreateDTO;
import gen.org.tkit.onecx.parameters.bff.rs.internal.model.ApplicationParameterUpdateDTO;

@ApplicationScoped
public class ParametersLog implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                this.item(10, ApplicationParameterCreateDTO.class,
                        x -> ApplicationParameterCreateDTO.class.getSimpleName() + "[name:"
                                + ((ApplicationParameterCreateDTO) x).getKey() + "]"),
                this.item(10, ApplicationParameterUpdateDTO.class,
                        x -> ApplicationParameterUpdateDTO.class.getSimpleName() + "[name:"
                                + ((ApplicationParameterUpdateDTO) x).getValue()
                                + "]"));
    }
}
