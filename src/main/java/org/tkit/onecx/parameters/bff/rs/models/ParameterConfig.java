package org.tkit.onecx.parameters.bff.rs.models;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("onecx-parameter-bff.adoc")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "onecx.parameter.bff")
public interface ParameterConfig {

    /**
     * rest-client configurations
     */
    @WithName("rest-client")
    RestClientsConfig restClients();

    interface RestClientsConfig {
        /**
         * Config for iam_kc_svc client
         */
        @WithName("product-store")
        RestClientConfig productStore();
    }

    interface RestClientConfig {

        /**
         * Enable or disable client
         */
        @WithDefault("true")
        @WithName("enabled")
        boolean enabled();

        /**
         * Page size for product search.
         */
        @WithDefault("1000")
        @WithName("page-size")
        int pageSize();

        /**
         * Page number for product search.
         */
        @WithDefault("0")
        @WithName("page-number")
        int pageNumber();
    }
}
