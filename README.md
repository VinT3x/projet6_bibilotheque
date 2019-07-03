# biblio - SOAP webservice serveur et client

__Installation__

    __Pré-requis :__
    
    Pour installer l'application les outils suivants doivent être installés :
  
       - PostgreSQL version 8 ou supérieure https://www.postgresql.org/download/
    
       - java 8
       
       - maven
  
    __Installation et lancement de l'application :__
    
        base de données :
       - créer une base de données nommée "biblio"
       
       installer le WS et son client :
  
       - télécharger les sources et les dezipper à l'em^lacement de votre choix que l'on nommera %REP_INSTALL% 
       - depuis une invite de commande, se positionner au niveau de l'emplacement du fichier POM (%REP_INSTALL%\biblio-master\biblio-master\back-end\bibliows pour le WS)
       - lancer la commande **mvn clean package**, 
       - se placer au niveau du jar **bibliows-0.0.1-SNAPSHOT.jar** généré sous %REP_INSTALL%\biblio-master\biblio-master\back-end\bibliows\target
       - exécuter la commande **java -jar bibliows-0.0.1-SNAPSHOT.jar**
