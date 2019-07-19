# biblio - SOAP webservice serveur et client

__Installation__

    __Pré-requis :__
    
    Pour installer l'application les outils suivants doivent être installés :
  
       - PostgreSQL version 8 ou supérieure https://www.postgresql.org/download/
    
       - java 8
       
       - maven
  
    __Installation et lancement de l'application :__
      base de données :
       - créer une base de données portant le nom "biblio", avec comme utilisateur propriétaire "postgres" et le mot de passe "admin".
       
       installer et démarrer le WS :  
       - télécharger les sources et les dézipper à l'emplacement de votre choix que l'on nommera %REP_INSTALL%
       
       - depuis une invite de commande, se positionner au niveau de l'emplacement du fichier POM (%REP_INSTALL%\biblio\back-end\bibliows)
       
       - lancer la commande **mvn clean package**, 
       - se placer au niveau du jar **bibliows-0.0.1-SNAPSHOT.jar** généré sous %REP_INSTALL%\biblio\back-end\bibliows\target
       - exécuter la commande **java -jar bibliows-0.0.1-SNAPSHOT.jar**
       
       installer et démarrer le client :
       Par défaut la configuration fonctionne si l'installation du client et du WS est réalisée sur une même machine.
       
       Sinon, modifier le fichier application.properties présent sous %REP_INSTALL%\biblio\front-end\src\main\resources, pour adapter les valeurs des clés suivantes :            
       
            uri_anonymous=   --> exemple de valeur  : http://localhost:8080/anonymous
            uri_biblio=      --> exemple de valeur  :  http://localhost:8080/ws
            
            
       - depuis une invite de commande, se positionner au niveau de l'emplacement du fichier POM (%REP_INSTALL%\biblio\front-end)
       - lancer la commande **mvn clean package**, 
       - se placer à l'emplacement du jar **biblioclient-1.0-SNAPSHOT.jar** généré sous %REP_INSTALL%\biblio\front-end\target\
       - exécuter la commande **java -jar biblioclient-1.0-SNAPSHOT.jar**
       
       
       configuration du WS :
              - modifier le fichier application.properties présent sous %REP_INSTALL%\biblio\back-end\bibliows\src\main\resources, pour adapter les valeurs des clés suivantes :
            Pour l'envoi des mails, vous pourrez utiliser un compte gmail, il faudra définir l'adresse mail et le mot de passe de l'expéditeur.
                app.mail.username= --> exemple de valeur test@gmail.com
                app.mail.password=
            
            Pour la programmation des relances par mail automatiques :
                app.email.cron=  --> exemple de valeur 0 */2 * * * ?
            
            Pour l'accès à la base de données :
                spring.datasource.url = --> exemple de valeur :  jdbc:postgresql://localhost:5432/biblio
                spring.datasource.username = --> exemple de valeur :  postgres
                spring.datasource.password = 
       
