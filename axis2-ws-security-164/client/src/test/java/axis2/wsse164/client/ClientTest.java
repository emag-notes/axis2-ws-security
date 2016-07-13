package axis2.wsse164.client;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.junit.Test;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClientTest {

  @Test
  public void test() throws Exception {
    JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
    Client client = dcf.createClient("http://localhost:8080/axis2-ws-security-164-server/services/HelloWorld?wsdl");

    Map<String, Object> outProps = new HashMap<>();
    outProps.put("action", "UsernameToken");
    outProps.put("user", "someone");
    outProps.put("passwordType", "PasswordText");
    outProps.put("passwordCallbackClass", UsernameTokenCallbackHandler.class.getName());
    client.getOutInterceptors().add(new WSS4JOutInterceptor(outProps));

    Object[] result = client.invoke("sayHello", "foo");
    assertThat(result[0], is("Hello, foo"));
  }

  public static class UsernameTokenCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      WSPasswordCallback callback = (WSPasswordCallback) callbacks[0];
      callback.setPassword("pass");
    }

  }

}