# notes

## What happen on HandlerUtils?

### Method Tracing

Add the following line in $CATALINA_HOME/conf/logging.properties to trace `HandlerUtils#checkMustUnderstand()`.

```
org.apache.axis2.jaxws.handler.HandlerUtils.level = FINE
```

And exec client.

#### Axis2 1.6.4

``` sh
FINE [http-nio-8080-exec-4] org.apache.axis2.jaxws.handler.HandlerUtils.checkMustUnderstand Reading UnprocessedHeaderNames from Message Context properties
FINE [http-nio-8080-exec-4] org.apache.axis2.jaxws.handler.HandlerUtils.checkMustUnderstand Adding any mustUnderstand headers based on additonal SOAP roles: []
14-Jul-2016 11:05:16.364FINE [http-nio-8080-exec-4] org.apache.axis2.jaxws.handler.HandlerUtils.checkMustUnderstand UNPROCESSED_HEADER_QNAMES not found.
```

#### Axis2 1.7.3

``` sh
FINE [http-nio-8080-exec-10] org.apache.axis2.jaxws.handler.HandlerUtils.checkMustUnderstand Reading UnprocessedHeaderNames from Message Context properties
FINE [http-nio-8080-exec-10] org.apache.axis2.jaxws.handler.HandlerUtils.checkMustUnderstand Adding any mustUnderstand headers based on additonal SOAP roles: []
FINE [http-nio-8080-exec-10] org.apache.axis2.jaxws.handler.HandlerUtils.canUnderstand Check to see if a jaxws handler is configured.
SEVERE [http-nio-8080-exec-10] org.apache.axis2.engine.AxisEngine.receive Must Understand check failed for headers: {http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security
 org.apache.axis2.AxisFault: Must Understand check failed for headers: {http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security
	at org.apache.axis2.jaxws.handler.HandlerUtils.checkMustUnderstand(HandlerUtils.java:160)
```

### Unprocessed Header

In 1.6.4, because the `unprocessed` variable is null, retuning without failure.

``` java
// https://github.com/apache/axis2-java/blob/v1.6.4/modules/jaxws/src/org/apache/axis2/jaxws/handler/HandlerUtils.java#L121
List<QName> unprocessed = (List)msgContext.getProperty(Constants.UNPROCESSED_HEADER_QNAMES);
[...]
// https://github.com/apache/axis2-java/blob/v1.6.4/modules/jaxws/src/org/apache/axis2/jaxws/handler/HandlerUtils.java#L145-L150
if(unprocessed == null || unprocessed.size() == 0){
    if(log.isDebugEnabled()){
        log.debug("UNPROCESSED_HEADER_QNAMES not found.");
    }
    return;
}
```

While 1.7.3, `unprocessed` variable is not null and HandlerUtils#canUnderstand(MessageContext) is false.

``` java
// https://github.com/apache/axis2-java/blob/v1.7.3/modules/jaxws/src/org/apache/axis2/jaxws/handler/HandlerUtils.java#L153-L161
if(!canUnderstand(msgContext)){
    QName[] qNames = unprocessed.toArray(new QName[0]);
    String[] headerNames = new String[qNames.length];
    for(int i=0; i<qNames.length; i++){
        headerNames[i] ="{" + qNames[i].getNamespaceURI()+ "}" + qNames[i].getLocalPart();
    }
    QName faultQName = envelope.getVersion().getMustUnderstandFaultCode();
    throw new AxisFault(Messages.getMessage("mustunderstandfailed2", headerNames), faultQName);
}
```

the Unprocessed Header QName is `{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security`. Why the WS-Security QName is not processed?

### Who puts the WS-Security QNAME to MessageContext.properties as unprocessed header QNames?

AxisEngine#checkMustUnderstand(MessagesContext).

https://github.com/apache/axis2-java/blob/v1.6.4/modules/kernel/src/org/apache/axis2/engine/AxisEngine.java#L83

AxisEngine#receive() calls each Phase Handler.

With Axis2 1.6.4, Security Phase has two handlers.

 * org.apache.rampart.handler.RampartReceiver
 * org.apache.rampart.handler.WSDoAllHandler

WSDoAllHandler will call SOAPHeaderBlockImpl.setProcessed() so the WS-Secrutiy QName is marked as processed.

``` #!/bin/sh
"http-nio-8080-exec-4@6717" daemon prio=5 tid=0x27 nid=NA runnable
  java.lang.Thread.State: RUNNABLE
    at org.apache.axiom.soap.impl.llom.SOAPHeaderBlockImpl.setProcessed(SOAPHeaderBlockImpl.java:120)
    at org.apache.rampart.handler.WSDoAllReceiver.processBasic(WSDoAllReceiver.java:273)
    at org.apache.rampart.handler.WSDoAllReceiver.processMessage(WSDoAllReceiver.java:85)
    at org.apache.rampart.handler.WSDoAllHandler.invoke(WSDoAllHandler.java:72)
    at org.apache.axis2.engine.Phase.invokeHandler(Phase.java:340)
    at org.apache.axis2.engine.Phase.invoke(Phase.java:313)
    at org.apache.axis2.engine.AxisEngine.invoke(AxisEngine.java:262)
    at org.apache.axis2.engine.AxisEngine.receive(AxisEngine.java:168)
    at org.apache.axis2.transport.http.HTTPTransportUtils.processHTTPPostRequest(HTTPTransportUtils.java:172)
    at org.apache.axis2.transport.http.AxisServlet.doPost(AxisServlet.java:146)
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
    - locked <0x1d8b> (a org.apache.tomcat.util.net.NioChannel)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
    at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
    at java.lang.Thread.run(Thread.java:745)
```

While 1.7.3, Security Phase has only a handler.

 * org.apache.rampart.handler.RampartReceiver

 And WSDoAllHandler doesn't exist in Rampart 1.7.0. Hmm, Should I use Rampart 1.6.4 instead of 1.7.0?
