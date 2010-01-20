/**
 * 
 */
package de.tixus.eopac.server.zones;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.xalan.xsltc.trax.SAX2DOM;
import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.tixus.eopac.domain.Isbn13;
import de.tixus.eopac.domain.MediaEntry;
import de.tixus.eopac.server.LibraryLookup;

/**
 * @author TSP
 * 
 */
public class LibraryLookupZonesImpl implements LibraryLookup {

  //  private static Log logger = LogFactory.getLog(LibraryLookupZonesImpl.class);
  static Logger logger = Logger.getLogger(LibraryLookupZonesImpl.class.getSimpleName());
  private static final String baseURIString = "alswww2.dll";

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.tixus.eopac.server.zones.LibraryLookup#findByIsbn(de.tixus.eopac.domain
   * .Isbn13)
   */
  public MediaEntry findByIsbn(final Isbn13 isbnNumber) {
    return parseHtml(isbnNumber);
  }

  private Document buildDocument(final String fileName) throws TransformerFactoryConfigurationError, TransformerException,
      FileNotFoundException {
    return parseTagSoup(new InputSource(new BufferedReader(new FileReader(fileName))));
  }

  private URI buildExpertSearchUri(final Document document, final Isbn13 isbnNumber) {
    try {
      final XPath xpath = XPathFactory.newInstance().newXPath();
      final String SEARCH_FORM_XPATH = "//form[@id='ExpertSearch']";
      final NodeList expertSearchFormNodes = (NodeList) xpath.evaluate(SEARCH_FORM_XPATH, document, XPathConstants.NODESET);

      if (expertSearchFormNodes.getLength() == 0)
        throw new IllegalStateException("Expected search form not found in the page.");

      // 
      final Node expertSearchRootNode = expertSearchFormNodes.item(0);
      final NamedNodeMap attributes = expertSearchRootNode.getAttributes();
      final Node action = attributes.getNamedItem("action");
      final String path = baseURIString + "/" + action.getNodeValue();

      //TODO add params dynamically
      final ArrayList<BasicNameValuePair> qparams = new ArrayList<BasicNameValuePair>();
      qparams.add(new BasicNameValuePair("Style", "Portal2"));
      qparams.add(new BasicNameValuePair("SubStyle", null));
      qparams.add(new BasicNameValuePair("Theme", null));
      qparams.add(new BasicNameValuePair("Lang", "GER"));
      qparams.add(new BasicNameValuePair("ResponseEncoding", "utf-8"));
      qparams.add(new BasicNameValuePair("Method", "QueryWithLimits"));
      qparams.add(new BasicNameValuePair("DB", "SearchServer"));
      qparams.add(new BasicNameValuePair("q.Query", isbnNumber.toString()));
      //    qparams.add(new BasicNameValuePair("BrowseAsHloc", "69"));
      final URI uri = URIUtils.createURI("https", "www.buecherhallen.de", -1, path, URLEncodedUtils.format(qparams, "UTF-8"), null);
      return uri;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private URI buildLocationListUri(final Document buildDocument) {
    // TODO Auto-generated method stub
    return null;
  }

  private URI buildShortTitleUri(final Document document) {
    //    <a class="darkLink " 
    //      target="_self"  
    //      href="Obj_1959221262804654?
    //    Style=Portal2&SubStyle=Advanced&Theme=&Lang=GER&ResponseEncoding=utf-8
    //    &Method=StockStatus2
    //    &Item=609891
    //    &amp;Parent=Obj_1959221262804654
    //    &SearchBrowseList=Obj_1959221262804654
    //    &SearchBrowseListItem=609891
    //    &BrowseList=Obj_1959221262804654
    //    &BrowseListItem=609891
    //    &BrowseAsHloc=69
    //    &QueryObject=Obj_1959211262804654">Bestand</a>
    try {
      final XPath xpath = XPathFactory.newInstance().newXPath();
      final String sessionObject = "ZonesObjName";
      final String SEARCH_FORM_XPATH = "//meta[@name='ZonesObjName']";
      final NodeList expertSearchFormNodes = (NodeList) xpath.evaluate(SEARCH_FORM_XPATH, document, XPathConstants.NODESET);

      if (expertSearchFormNodes.getLength() == 0)
        throw new IllegalStateException("Expected search form not found in the page.");

      // 
      final Node expertSearchRootNode = expertSearchFormNodes.item(0);
      final NamedNodeMap attributes = expertSearchRootNode.getAttributes();
      final Node action = attributes.getNamedItem("action");
      final String path = baseURIString + "/" + action.getNodeValue();

      //TODO add params dynamically
      final ArrayList<BasicNameValuePair> qparams = new ArrayList<BasicNameValuePair>();
      //      qparams.add(new BasicNameValuePair("Style", "Portal2"));
      //      qparams.add(new BasicNameValuePair("SubStyle", null));
      //      qparams.add(new BasicNameValuePair("Theme", null));
      //      qparams.add(new BasicNameValuePair("Lang", "GER"));
      //      qparams.add(new BasicNameValuePair("ResponseEncoding", "utf-8"));
      //      qparams.add(new BasicNameValuePair("Method", "QueryWithLimits"));
      //      qparams.add(new BasicNameValuePair("DB", "SearchServer"));
      final URI uri = URIUtils.createURI("https", "www.buecherhallen.de", -1, path, URLEncodedUtils.format(qparams, "UTF-8"), null);
      return uri;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  private ResponseHandler<URI> mainCatalogResponseHandler(final Isbn13 isbnNumber) throws Exception {

    return new ResponseHandler<URI>() {

      public URI handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
          try {
            final InputStream content = entity.getContent();
            final Document document = parseTagSoup(new InputSource(content));
            final URI uri = buildExpertSearchUri(document, isbnNumber);
            return uri;
          } catch (final Exception e) {
            throw new IOException(e.getCause());
          }
        } else {
          return null;
        }
      }
    };
  }

  private MediaEntry parseHtml(final Isbn13 isbnNumber) {
    final HttpClient httpClient = new DefaultHttpClient();
    try {
      final HttpGet httpGet = new HttpGet(
                                          "https://www.buecherhallen.de/alswww2.dll/APS_QUICK_SEARCH?Style=Portal2&SubStyle=Advanced&Theme=&Lang=GER&ResponseEncoding=utf-8&Style=Portal2&BrowseAsHloc=69");

      // 0 - build up session and main catalog
      final URI mainCatalogUri = httpGet.getURI();
      logger.info("main catalog uri: " + mainCatalogUri.toString());

      // 1 - build up ISBN search submission
      final URI expertSearchUriLocal = buildExpertSearchUri(
                                                            buildDocument("C:\\project\\selfstudy\\opac\\eopac\\resources\\1-maincatalog.html"),
                                                            isbnNumber);
      logger.info("1.expert search uri: " + expertSearchUriLocal.toString());

      final URI expertSearchUri = httpClient.execute(httpGet, mainCatalogResponseHandler(isbnNumber));
      logger.info("2.expert search uri: " + expertSearchUri.toString());

      // 2 - build up "Kurztitel" submission
      final URI shortTitleUriLocal = buildShortTitleUri(buildDocument("C:\\project\\selfstudy\\opac\\eopac\\resources\\2-resultpage.html"));
      logger.info("expert search uri: " + shortTitleUriLocal.toString());

      //      final URI shortTitleUri = httpClient.execute(httpGet, shortTitleResponseHandler(isbnNumber));
      //      logger.info("2.expert search uri: " + expertSearchUri.toString());

      // 3 - build up "Alle Bibliotheken im System, die über Bestand verfügen" submission
      final URI locationListUri = buildLocationListUri(buildDocument("C:\\project\\selfstudy\\opac\\eopac\\resources\\3-detailpage.html"));
      logger.info("expert search uri: " + locationListUri.toString());

      // 4 - analyse "Bestand in Zentralbibliothek"
      //      final URI singleLocationUri = buildExpertSearchUri(buildDocument("C:\\project\\selfstudy\\opac\\eopac\\resources\\4-statuspage.html"));
      //      logger.debug("expert search uri: " + expertSearchUri.toString());

    } catch (final Exception e) {
      throw new RuntimeException(e);
    } finally {
      httpClient.getConnectionManager().shutdown();
    }
    return null;
  }

  private Document parseTagSoup(final InputSource is) throws TransformerFactoryConfigurationError, TransformerException {
    final Parser parser = new Parser();
    SAX2DOM sax2dom = null;
    try {
      sax2dom = new SAX2DOM();
      parser.setContentHandler(sax2dom);
      parser.setFeature(Parser.namespacesFeature, false);
      parser.parse(is);
    } catch (final Exception e) {
      e.printStackTrace();
    }
    final Node rootNode = sax2dom.getDOM();

    return (Document) rootNode;
  }

  private ResponseHandler<URI> shortTitleResponseHandler(final Isbn13 isbnNumber) {
    return new ResponseHandler<URI>() {

      public URI handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
          try {
            final InputStream content = entity.getContent();
            final Document document = parseTagSoup(new InputSource(content));
            final URI uri = buildShortTitleUri(document);
            return uri;
          } catch (final Exception e) {
            throw new IOException(e.getCause());
          }
        } else {
          return null;
        }
      }
    };
  }

}
