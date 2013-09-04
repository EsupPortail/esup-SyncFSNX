Génération des packages :
mvn -Dmaven.test.skip=true package 

Gestion des releases :
mvn release:prepare
mvn release:perform