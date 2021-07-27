package nu.borjessons.clubhouse.integration.tests.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.ClubhouseApplication;

public class IntegrationTestHelper {
  public static ConfigurableApplicationContext runSpringApplication(int dbPort) {
    ConfigurableEnvironment environment = new StandardEnvironment();
    String dbUrl = String.format("jdbc:postgresql://localhost:%d/postgres", dbPort);
    MutablePropertySources propertySources = environment.getPropertySources();
    Map<String, Object> propertyMap = new HashMap<>();
    propertyMap.put("token.secret", "öalkdsjföasldfjaösldkfjaösdlkfjaösdlfkjasdöflkj");
    propertyMap.put("token.expiration", "604800");
    propertyMap.put("server.port", "8081");
    propertyMap.put("spring.datasource.url", dbUrl);
    propertyMap.put("spring.datasource.username", "postgres");
    propertyMap.put("spring.datasource.password", "postgres");
    propertyMap.put("spring.jpa.database", "POSTGRESQL");
    propertyMap.put("spring.jpa.generate-ddl", "true");
    propertyMap.put("spring.jpa.hibernate.ddl-auto", "create");
    propertySources.addFirst(new MapPropertySource("CUSTOM_PROPS", propertyMap));

    return new SpringApplicationBuilder()
        .profiles("test")
        .environment(environment)
        .sources(ClubhouseApplication.class)
        .run();
  }

  public static ConfigurableApplicationContext runSpringApplication() {
    ConfigurableEnvironment environment = new StandardEnvironment();
    MutablePropertySources propertySources = environment.getPropertySources();
    Map<String, Object> propertyMap = new HashMap<>();
    propertyMap.put("token.secret", "öalkdsjföasldfjaösldkfjaösdlkfjaösdlfkjasdöflkj");
    propertyMap.put("token.expiration", "604800");
    propertyMap.put("server.port", "8081");
    propertySources.addFirst(new MapPropertySource("CUSTOM_PROPS", propertyMap));

    return new SpringApplicationBuilder()
        .profiles("test")
        .environment(environment)
        .sources(ClubhouseApplication.class)
        .run();
  }

  public static EmbeddedPostgres startEmbeddedPostgres() throws IOException {
    return EmbeddedPostgres.builder().setLocaleConfig("locale", "C").start();
  }
}
