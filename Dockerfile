FROM --platform=linux/amd64 eclipse-temurin:21-jdk

ENV DISPLAY=host.docker.internal:0.0

RUN apt-get update && \
    apt-get install -y maven wget unzip libgtk-3-0 libgbm1 libx11-6 \
    fonts-noto-cjk \
    fonts-noto-ui-core \
    fontconfig \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

RUN fc-cache -fv

RUN apt-get update && apt-get install -y wget unzip \
 && wget https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_linux-x64_bin-sdk.zip \
 && unzip openjfx-21.0.2_linux-x64_bin-sdk.zip -d /opt

RUN apt-get update && apt-get install -y locales && \
    locale-gen ja_JP.UTF-8 && \
    update-locale LANG=ja_JP.UTF-8
ENV LANG=ja_JP.UTF-8
ENV LANGUAGE=ja_JP:ja
ENV LC_ALL=ja_JP.UTF-8

RUN cp /opt/javafx-sdk-21.0.2/lib/*.so /usr/lib/ && ldconfig

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

ENV JAVA_TOOL_OPTIONS="-Dprism.order=sw"

RUN ls -l target/

CMD ["java", "-Dprism.order=sw", \
             "-Dprism.verbose=true", \
             "-Djava.library.path=/opt/javafx-sdk-21.0.2/lib:/usr/lib", \
             "--module-path", "/opt/javafx-sdk-21.0.2/lib", \
             "--add-modules", "javafx.controls,javafx.fxml", \
             "-Djavafx.font.path=/usr/share/fonts/opentype/noto", \
             "-Dprism.fontdir=/usr/share/fonts/opentype/noto", \
             "-jar", "target/tripcost.jar"]