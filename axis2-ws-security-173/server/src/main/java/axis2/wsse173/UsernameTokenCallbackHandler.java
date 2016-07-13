package axis2.wsse173;

import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class UsernameTokenCallbackHandler implements CallbackHandler {

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    WSPasswordCallback callback = (WSPasswordCallback) callbacks[0];
    callback.setPassword("pass");
  }

}
