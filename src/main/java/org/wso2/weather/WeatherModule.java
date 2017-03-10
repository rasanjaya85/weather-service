package org.wso2.weather;

import com.google.inject.Scopes;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;

/**
 * Created by raz on 2/12/17.
 */
public class WeatherModule extends RequestScopeModule{

    public void configure() {
        bind(WeatherResource.class).in(Scopes.SINGLETON);
    }
}
