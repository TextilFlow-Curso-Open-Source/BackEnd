package com.textilflow.platform.payment.acceptance.tests;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/payment")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.textilflow.platform.payment.acceptance.tests.steps")
public class CucumberTestRunner {
}
