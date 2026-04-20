# Como correr el proyecto

Agregue una manera sencilla para correr el proyecto simplemente es necesario tener docker.

pararse en la raiz del proyecto earned-value-management dodne se visualiza un archivo docker-compose.yml, una vez localizado 
y que se encuentra correctamente ubicado correr el comando:

´´´
docker compose up --build
´´´

Hay que esperar unos segundos mientras levanta la base de datos, backend y front. una vez levantados se encuentran 
corriendo en las siguientes rutas:

backend: http://localhost:8080/swagger-ui/index.html
frontend: http://localhost:4200

ya queda abrir las rutas y probar su funcionamiento. 

Si desean correrlo individualmente entonces es necesario entrar y ubicarse 
en la carpeta deseada en caso de ser backend entrar hasta la ruta del proyecto de backend y una vez este en la ruta donde
exactamente se ve un archivo docker-compose.yml ejecutar el mismo comando:


´´´
docker compose up --build
´´´

genera las mismas rutas anteriores de ejecución.