# archetypes
Maven archetypes projects. 

Maven archetypes allows to create new Java project with connected libraries and some sources.
For example UiEasyStarter allows to create Java project to test WebUI.
The project contain connected Selenium2, ReportNG (with wrapper to make screenshot on test failure), slf4j with logback-classic,
PageObject example, WebDriver factory, etc.

How to rebuild:
Start point is project dir (for example UiEasyStarter placed),
mvn clean,
mvn install archetype:update-local-catalog

Eclipse run configuration:
Base dir: ${workspace_loc:/UiEasyStarter},
Goals: clean install archetype:update-local-catalog
