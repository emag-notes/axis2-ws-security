# axis2-ws-security-164

## Environment

* Axis2 and Rampart: 1.6.4
* Tomcat: 8.0.36
* JDK: java-1.8.0-openjdk-1.8.0.92-4.b14.fc23.x86_64

## Usage

### Add tools.jar to CLASSPATH

``` sh
$ cp setenv.sh <YOUR_CATALINA_HOME>/bin
```

please change JAVA_HOME to your environment.

### Create server app and deploy to Tomcat

``` sh
$ ./mvnw clean package -pl server
$ cp server/target/axis2-ws-security-164-server.war <YOUR_CATALINA_HOME>/webapps
```

And run tomcat with 8080 port.

### Access to service

#### SOAP 1.1

``` sh
$ ./mvnw clean test -pl client
```

It should work fine.