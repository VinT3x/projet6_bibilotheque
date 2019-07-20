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
        
       - depuis une invite de commande, se positionner au niveau de l'emplacement du fichier POM (%REP_INSTALL%\biblio\front-end)
       - lancer la commande **mvn clean package**, 
       - se placer à l'emplacement du jar **biblioclient-1.0-SNAPSHOT.jar** généré sous %REP_INSTALL%\biblio\front-end\target\
       - exécuter la commande **java -jar biblioclient-1.0-SNAPSHOT.jar**
       
       
       __configuration__ 
      
       Par défaut, l'application (client et WS) fonctionne sur une même machine, la configuration n'a pas besoin d'être modifiée. L'envoi de mail de relance est programmé toutes les 2 minutes. Il est possible de changer la fréquence d'envoi, l'expéditeur et autres paramètres.
       
       Pour le web service, la configuration est sous %REP_INSTALL%\biblio\back-end\bibliows\src\main\resources\application.properties.
       Ce fichier permet de :
           Envoi des emails :  - , il est possible de changer la fréquence. Il faut modifier la valeur de la clé app.email.cron
                               - configuration de la messagerie pour l'envoi des emails. Il faut modifier les valeurs des clés app.mail.username, app.mail.password, app.mail.host et app.mail.port
                        
            Accès à la base de données, voici les clés :
                spring.datasource.url = --> exemple de valeur :  jdbc:postgresql://localhost:5432/biblio
                spring.datasource.username
                spring.datasource.password
                
            Durée d'un prêt en jour, il faut modifier la valeur de la clé delayDay_reserveBook
        
        Pour le client, le fichier de configuration se trouve sous %REP_INSTALL%\biblio\front-end\src\main\resources\application.properties. Il contient les uri d'accès aux services.
       
