FROM maven:3.3.9-jdk-8

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY . /usr/src/app

RUN mvn clean install -Dmaven.test.skip=true

VOLUME /usr/src/app

CMD ["/bin/echo", "Maven install executed successfully"]