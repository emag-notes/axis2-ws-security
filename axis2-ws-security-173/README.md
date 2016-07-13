# axis2-ws-security-173

## Environment

* Axis2: 1.7.3
* Rampart: 1.7.0
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

### Access to the service

``` sh
$ ./mvnw clean test -pl client
```

And you should see the following error in catalina.out.

``` sh
13-Jul-2016 22:04:40.500 SEVERE [http-nio-8080-exec-8] org.apache.axis2.engine.AxisEngine.receive Must Understand check failed for headers: {http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security
 org.apache.axis2.AxisFault: Must Understand check failed for headers: {http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security
	at org.apache.axis2.jaxws.handler.HandlerUtils.checkMustUnderstand(HandlerUtils.java:160)
	at org.apache.axis2.jaxws.server.EndpointController.inboundHeaderAndHandlerProcessing(EndpointController.java:336)
	at org.apache.axis2.jaxws.server.EndpointController.handleRequest(EndpointController.java:258)
	at org.apache.axis2.jaxws.server.EndpointController.invoke(EndpointController.java:101)
	at org.apache.axis2.jaxws.server.JAXWSMessageReceiver.receive(JAXWSMessageReceiver.java:165)
	at org.apache.axis2.engine.AxisEngine.receive(AxisEngine.java:169)
	at org.apache.axis2.transport.http.HTTPTransportUtils.processHTTPPostRequest(HTTPTransportUtils.java:176)
	at org.apache.axis2.transport.http.AxisServlet.doPost(AxisServlet.java:163)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:648)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:729)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:292)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:207)
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:240)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:207)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:212)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:106)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:502)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:141)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:79)
	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:616)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:88)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:528)
	at org.apache.coyote.http11.AbstractHttp11Processor.process(AbstractHttp11Processor.java:1099)
	at org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process(AbstractProtocol.java:670)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1520)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.run(NioEndpoint.java:1476)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:745)
```