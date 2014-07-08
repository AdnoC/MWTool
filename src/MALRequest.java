import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
public class MALRequest {
  /**
   * Requests that will be used:
   *  http://myanimelist.net/api/manga/search.xml
   *   Search Manga
   * http://myanimelist.net/api/mangalist/add/id.xml
   *   Add manga
   * http://myanimelist.net/api/animelist/update/id.xml
   *   Update manga
   * http://myanimelist.net/api/account/verify_credentials.xml
   *   Verify account credentials
   */

  protected String requestURL;
  protected Map<String, String> params;
  protected RequestType type;
  protected Document document;
  protected boolean[] auth = new boolean[2];

  public enum RequestType {
    LOGIN, ADD, UPDATE, SEARCH;

    protected String[] requiredParams() {
      switch(this) {
        case LOGIN:
          return new String[0];
        case ADD:
          return new String[]{ "id", "data" };
        case UPDATE:
          return new String[]{ "id", "data" };
        case SEARCH:
          return new String[]{ "q" };
        default:
          return new String[0];
      }

    }
    protected String getURL() {
      switch(this) {
        case LOGIN:
          return  "http://myanimelist.net/api/account/verify_credentials.xml";
        case ADD:
          return "http://myanimelist.net/api/mangalist/add/id.xml";
        case UPDATE:
          return "http://myanimelist.net/api/animelist/update/id.xml";
        case SEARCH:
          return "http://myanimelist.net/api/manga/search.xml";
        default:
          return "";
      }
    }
  }

  public MALRequest() {
    this(RequestType.LOGIN);
  }
  public MALRequest(RequestType rType) {
    setType(rType);
  }

  public String addParam(String key, String value) {
    if(params == null) {
      params = new HashMap<String, String>();
    }
    return params.put(key, value);
  }
  public String removeParam(String key) {
    if(params == null) {
      params = new HashMap<String, String>();
      return null;
    }
    return params.remove(key);
  }

  protected String getFinalURL() {
    String ret = requestURL;
    if(requestURL.indexOf("id") != -1 && params != null && params.containsKey("id")) {
      ret = ret.replaceAll("id", params.get("id"));
    }
    if(params != null && !params.isEmpty()) {
      ret += "?" +  Utils.buildParamsFromMap(params);
    }
    return ret;
  }


  protected void addAuth(URLConnection uc) {
    String userpass = Config.MAL_USERNAME + ":" + Config.MAL_PASSWORD;
    String basicAuth = javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

    uc.setRequestProperty("Authorization", "Basic " + basicAuth);
    // Until MAL whitelists me, need to use chrome's user-agent for testing.
    uc.setRequestProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
    //uc.setRequestProperty("http.agent", "MWSync");
  }
  public void changeType(RequestType rType) {
    clear();
    setType(rType);
  }
  protected void setType(RequestType rType) {
    this.type = rType;
    this.requestURL = rType.getURL();
  }
  /**
   * Clears all data for this request.
   */
  protected void clear() {
    params = null;
  }

  public Document getDocument() {
    if(document == null) {
      return request();
    } else {
      return document;
    }
  }
  public Document request() throws BadRequestParamsException {
    if(! canRequest()) {
      ArrayList<String> req = new ArrayList<String>(Arrays.asList(type.requiredParams()));
      req.removeAll(params.keySet());
      throw new BadRequestParamsException(req.toArray(new String[req.size()]));
    }
    String urlStr = requestURL;
    if(params != null && !params.isEmpty()) {
      String data = Utils.buildParamsFromMap(params);
      urlStr += "?" + data;
    }

    // Send data
    try{
      URL url = new URL(urlStr);
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) url.openConnection();
        addAuth(conn);
        conn.setDoOutput(true);

        try{
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          try {
            Document dom = builder.parse(conn.getInputStream());
            this.document = dom;
            return dom;
          } catch(SAXException saxe) {
            saxe.printStackTrace();
          }
        } catch(ParserConfigurationException pce) {
          pce.printStackTrace();
        }

      } catch(IOException ioe) {
        ioe.printStackTrace();
      } finally {
        if(conn != null) {
          conn.disconnect();
        }
      }
    } catch(MalformedURLException mue) {
      mue.printStackTrace();
    }
    //@TODO: Make this return a value
    return null;
  }
          //InputStreamReader isr = new InputStreamReader(conn.getInputStream());
          ////Get the response
          //BufferedReader br = new BufferedReader(isr);
          //String str = br.readLine();
          //while(str != null && !str.equals("")) {
            //System.out.println(str);
            //str = br.readLine();
          //}
  public boolean canRequest() {
    for(String param : this.type.requiredParams()) {
      if(! params.containsKey(param)) {
        return false;
      }
    }
    return true;
  }
  public boolean isAuthorized() {
    // Set a cache of whether we are authorized so we don't have to make multiple
    // requests for authorization.
    if(auth[0]) {
      return auth[1];
    }
    int response = 401;
    try{
      String urlStr = RequestType.LOGIN.getURL();
      URL url = new URL(urlStr);
      try {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        addAuth(conn);
        conn.setDoOutput(true);

        response = conn.getResponseCode();

        conn.disconnect();
      } catch(IOException ioe) {
        ioe.printStackTrace();
      }
    } catch(MalformedURLException mue) {
      mue.printStackTrace();
    }
    return response == 200;

  }
}
