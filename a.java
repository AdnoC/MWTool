import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
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
public class a {
  public boolean PRINT_XML = false;

  static String user = "";
  static String pass = "";
  public Document document;
  public static void main(String[] b) {
  new a();
  }
  //String urlStr = "http://myanimelist.net/api/manga/search.xml?q=Fukashigi+Philia";
  String urlStr = "http://myanimelist.net/api/manga/search.xml?q=Aragami+Hime";
  public a() {
    request2();

  }
  protected static void addAuth(URLConnection uc) {
    String userpass = user + ":" + pass;
    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
    uc.setRequestProperty("Authorization", basicAuth);
    // Until MAL whitelists me, need to use chrome's user-agent for testing.
    //uc.setRequestProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
    uc.setRequestProperty("http.agent", "MWSync");
  }
public Document request2() {
  try{

    // Send data
      URL url = new URL(urlStr);
      HttpURLConnection conn = null;
        conn = (HttpURLConnection) url.openConnection();
        addAuth(conn);
        conn.setDoOutput(true);
        int rCode = conn.getResponseCode();
        System.out.println("CODE: " + rCode);

        if(PRINT_XML) {
          BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          StringBuilder builder = new StringBuilder();
          String aux = "";
            while ((aux = reader.readLine()) != null) {
              builder.append(aux);
            }
        }

        InputStream is = conn.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
          //builder.append(reader.readLine());
          //builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        String aux = "";
          while ((aux = reader.readLine()) != null) {
            builder.append(aux);
        }

        String fStr = builder.toString();
        System.out.println(fStr);
        fStr = fStr.replaceAll("&rsquo", "&amp;rsquo");
        is = new ByteArrayInputStream(fStr.getBytes());
        System.out.println("C");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        System.out.println("B");
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        System.out.println("D");
        Document dom = dbuilder.parse(is);
        System.out.println("A");
        this.document = dom;





            //java.net.URI uri = url.toURI();
            //Document dom = builder.parse(uri.toString());
            //Document dom = builder.parse(is);


    } catch(Exception mue) {
      mue.printStackTrace();
    }
    //@TODO: Make this return a value
    return null;
  }

public Document request() {
  try{

    // Send data
      URL url = new URL(urlStr);
      HttpURLConnection conn = null;
        conn = (HttpURLConnection) url.openConnection();
        addAuth(conn);
        conn.setDoOutput(true);
        int rCode = conn.getResponseCode();
        System.out.println("CODE: " + rCode);

        if(PRINT_XML) {
          BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          StringBuilder builder = new StringBuilder();
          String aux = "";
          System.out.println("INPUT:");
            while ((aux = reader.readLine()) != null) {
              System.out.println(aux);
            }
        }

        InputStream is = conn.getInputStream();

        StreamSource ss = new StreamSource(is);

          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document res = builder.newDocument();
          DOMResult domres = new DOMResult(res);
          
          TransformerFactory transFact = TransformerFactory.newInstance();
String xlst ="<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"
                + " version='1.0'>"
              +"<xsl:template match='/'>"
         + "<xsl:text disable-output-escaping=\"yes\">&amp;</xsl:text>"
         + "<xsl:copy-of select='.'/>"
         + "</xsl:template>"
         + "</xsl:stylesheet>";

String xlst2 ="<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"
                + " version='1.0'>"
 + "<xsl:template match=\"p/text()\">"
      + "<xsl:call-template name=\"replace-string\">"
          + "<xsl:with-param name=\"text\" select=\".\"/>"
          + "<xsl:with-param name=\"from\">'</xsl:with-param>"
          + "<xsl:with-param name=\"to\" select=\"'&amp;rsquo;'\"/>"
      + "</xsl:call-template>"
  + "</xsl:template>"
         + "</xsl:stylesheet>";
String xlst3 ="<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"
                + " version='1.0'>";
xlst3 += "<xsl:template match='/element'><xsl:value-of select=\"replace(current(), '&amp;amp;', '&amp;')\" disable-output-escaping=\"yes\"/></xsl:template>";
xlst3 += "</xsl:stylesheet>";

String xlst4 ="<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"
                + " version='2.0'>"
+"<xsl:character-map name=\"TM\">"
+  "<xsl:output-character character=\"&rsquo;\" string=\"&amp;rsquo;\"/>"
+"</xsl:character-map>"
+ "</xsl:stylesheet>";

          Transformer trans = transFact.newTransformer(new StreamSource(new java.io.StringReader(xlst4)));
          //trans.setOutputProperty("cdata-section-elements", "&rsquo");
          trans.transform(ss, domres);
          Document dom  = res;




            //java.net.URI uri = url.toURI();
            //Document dom = builder.parse(uri.toString());
            //Document dom = builder.parse(is);
            System.out.println("DOCTYPE: " + dom.getDoctype());
            this.document = dom;
            return dom;


    } catch(Exception mue) {
      mue.printStackTrace();
    }
    //@TODO: Make this return a value
    return null;
  }
  public Document requestDocument() {
    return null;
  }
}
