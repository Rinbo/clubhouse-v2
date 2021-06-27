package nu.borjessons.clubhouse.integration.tests.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import nu.borjessons.clubhouse.impl.ClubhouseApplication;

public class IntegrationTestHelper {
  public static ConfigurableApplicationContext runSpringApplication() {
    ConfigurableEnvironment environment = new StandardEnvironment();
    MutablePropertySources propertySources = environment.getPropertySources();
    Map<String, Object> myMap = new HashMap<>();
    myMap.put("token.secret", "öalkdsjföasldfjaösldkfjaösdlkfjaösdlfkjasdöflkj");
    propertySources.addFirst(new MapPropertySource("CUSTOM_PROPS", myMap));

    return new SpringApplicationBuilder()
        .profiles("test")
        .environment(environment)
        .sources(ClubhouseApplication.class)
        .run();
  }
}
