FROM eclipse-temurin:21-jdk

ENV DISPLAY=host.docker.internal:0.0

# Install only required libraries (NO MAVEN HERE)
RUN apt-get update && \
    apt-get install -y maven wget unzip libgtk-3-0 libgbm1 libx11-6 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Download JavaFX SDK 21
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-aarch64_bin-sdk.zip -O /tmp/openjfx.zip && \
    unzip /tmp/openjfx.zip -d /opt && \
    rm /tmp/openjfx.zip

RUN cp /opt/javafx-sdk-21/lib/*.so /usr/lib/ && ldconfig


WORKDIR /app

# Copy project
COPY pom.xml .
COPY src ./src

# ✅ NOW Maven works correctly
RUN mvn clean package -DskipTests

# Debug
RUN ls -l target/

CMD ["java", "-Dprism.order=sw", \
                  "-Dprism.verbose=true", \
                  "-Djava.library.path=/opt/javafx-sdk-21/lib:/usr/lib", \
                  "--module-path", "--module-path", "/opt/javafx-sdk-21/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "target/tripcost.jar"]