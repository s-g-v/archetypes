How to rebuild:
Start point is project dir (where UiEasyStarted placed)
mvn clean
mvn install archetype:update-local-catalog

Eclipse run configuration:
Base dir: ${workspace_loc:/UiEasyStarter}
Goals: clean install archetype:update-local-catalog