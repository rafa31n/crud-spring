FROM openjdk:8-jdk-alpine as javajdk
WORKDIR /app
COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw  dependency:purge-local-repository -DreResolve=true -DactTransitively=false
RUN ./mvnw  package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:8-jdk-alpine

ARG DEPENDENCY=/app/target/dependency
COPY --from=javajdk ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=javajdk ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=javajdk ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","com.tutorial.crud.CrudApplication"]
