Génération des packages :
mvn -Dmaven.test.skip=true package 

Gestion des releases :
mvn release:prepare -DautoVersionSubmodules
mvn -P portletDevelopment release:perform -Dgoals=package