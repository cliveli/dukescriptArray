package world.hello.js;

import net.java.html.js.JavaScriptBody;
import net.java.html.js.JavaScriptResource;

@JavaScriptResource(value = "registerRouter.js")
public final class Router {

  private Router() {}

  @JavaScriptBody(args = {}, body = "", javacall = true)
  public static native String registerBinding();
}

