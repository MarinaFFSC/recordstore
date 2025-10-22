package br.com.recordstore.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.recordstore.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary")

// 👇 ESSAS DUAS SÃO AS MAIS IMPORTANTES
@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfig {
}
