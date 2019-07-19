# biblio - SOAP webservice serveur et client

__Installation__

    __Pré-requis :__
    
    Pour installer l'application les outils suivants doivent être installés :
  
       - PostgreSQL version 8 ou supérieure https://www.postgresql.org/download/
    
       - java 8
       
       - maven
  
    __Installation et lancement de l'application :__
    
        base de données :
       - créer une base de données portant le nom "biblio".
       
       installer et démarrer le WS :  
       - télécharger les sources et les dézipper à l'emplacement de votre choix que l'on nommera %REP_INSTALL%
       
       - modifier le fichier application.properties présent sous %REP_INSTALL%\biblio-master\biblio-master\back-end\bibliows\src\main\resources, pour adapter les valeurs des clés suivantes :
            Pour l'envoi des mails, vous pourrez utiliser un compte gmail, il faudra définir l'adresse mail et le mot de passe de l'expéditeur.
                app.mail.username= --> exemple de valeur test@gmail.com
                app.mail.password=
            
            Pour la programmation des relances par mail automatiques :
                app.email.cron=  --> exemple de valeur 0 */2 * * * ?
            
            Pour l'accès à la base de données :
                spring.datasource.url = --> exemple de valeur :  jdbc:postgresql://localhost:5432/biblio
                spring.datasource.username = --> exemple de valeur :  postgres
                spring.datasource.password = 
       
       - depuis une invite de commande, se positionner au niveau de l'emplacement du fichier POM (%REP_INSTALL%\biblio-master\biblio-master\back-end\bibliows)
       
       - lancer la commande **mvn clean package**, 
       - se placer au niveau du jar **bibliows-0.0.1-SNAPSHOT.jar** généré sous %REP_INSTALL%\biblio-master\biblio-master\back-end\bibliows\target
       - exécuter la commande **java -jar bibliows-0.0.1-SNAPSHOT.jar**
       
       installer et démarrer le client :
       - modifier le fichier application.properties présent sous %REP_INSTALL%\biblio-master\biblio-master\front-end\src\main\resources\resources, pour adapter les valeurs des clés suivantes :
            
            Pour définir le port. Il devra être différent que le webservice, si le client et le serveur son installés sur la même machine
            server.port=      --> exemple de valeur :  8081
            
            Si le WS se trouve sur une autre machine il faudra modifier les url des accès aux endpoints
            uri_anonymous=   --> exemple de valeur  : http://localhost:8080/anonymous
            uri_biblio=      --> exemple de valeur  :  http://localhost:8080/ws
            
          - depuis une invite de commande, se positionner au niveau de l'emplacement du fichier POM (%REP_INSTALL%\biblio-master\biblio-master\front-end)
       
       - lancer la commande **mvn clean package**, 
       - se placer à l'emplacement du jar **biblioclient-1.0-SNAPSHOT.jar** généré sous %REP_INSTALL%\biblio-master\biblio-master\front-end\target\
       - exécuter la commande **java -jar biblioclient-1.0-SNAPSHOT.jar**
       
