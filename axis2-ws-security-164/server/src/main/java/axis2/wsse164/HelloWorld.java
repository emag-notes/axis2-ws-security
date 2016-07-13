package axis2.wsse164;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "HelloWorld")
public class HelloWorld {

  @WebMethod
  public String sayHello(@WebParam(name = "text") String text) {
    return "Hello, " + text;
  }

}
