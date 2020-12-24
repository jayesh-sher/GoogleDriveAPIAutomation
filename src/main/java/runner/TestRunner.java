package runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/main/resources/features/"
		, glue = { "stepdefinitions" }
		, dryRun = false
		, monochrome = true
		, plugin = {"pretty","html:target/Destination/report.html"}
		)
public class TestRunner {

}
