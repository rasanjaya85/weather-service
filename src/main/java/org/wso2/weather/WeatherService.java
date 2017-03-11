package org.wso2.weather;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.typesafe.config.Config;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;
import org.wso2.microservices.Microservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by raz on 2/12/17.
 */
public class WeatherService extends Microservice{

    public static void main(String[] args) {
        new WeatherService().run();
    }

//    @Override
//    public Module[] getModules(){
//        return new Module[] { new WeatherModule() };
//    }


    @Inject
        private Config config;

    private Connection getConnection() throws SQLException {

        Connection connection;
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (final ClassNotFoundException e) {
            System.out.println("   - ERROR:" + e.getMessage());
        }
//        connection = DriverManager.getConnection("jdbc:derby:memory:myDB;create=true", "APP", "my-secret-password");
        connection = DriverManager.getConnection("jdbc:derby:memory:myDB;create=true", "APP",config.getString("database.password"));
        return connection;
    }

    private String getAnswersByOwner(final String owner) {

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            final String query = "SELECT answer FROM T_ADVICE WHERE owner= '" + owner + "'";
            statement = getConnection().createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getString("answer");
            }
        } catch (final SQLException e) {
            System.out.println("   - ERROR:" + e.getMessage());
        } finally {

            if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (final SQLException e) {
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                } catch (final SQLException e) {
                }
            }
        }

        return null;
    }



    @Override
    public Module[] getModules() {
        return new Module[] {
                new RequestScopeModule() {
                    @Override
                    protected void configure() {
                        System.out.println("Weather RequestScopeModule()");
                        bind(ConfigTest.class).in(Scopes.SINGLETON);
                    }
                }
        };
    }

    @Path("/")
    public static class ConfigTest {

        @Inject
        private Config config;

        @GET
        @Produces("application/json")
        public Map<String, String>  getResources() {
             return ImmutableMap.of("weather.name",config.getString("weather.config"),"microservice-name",config.getString("microservice.name"));
        }
    }

}
