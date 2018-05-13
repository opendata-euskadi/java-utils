package r01f.geo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.Cleanup;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpClientProxySettings;
import r01f.types.Path;
import r01f.types.url.Url;
import r01f.xml.XMLUtils;


/**
 * XML utilities.
 */
public final class XMLParseUtils {
///////////////////////////////////////////////////////////////////////////////////////////
//  PARSING
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Loads a XML File, parses it and returns a DOM {@link Document}
     * @param filePath 
     * @return 
     * @throws SAXException
     */
    public static Document parse(final Path filePath,
    							 final String... ignoredEntities) throws IOException,
    																	 SAXException {
    	return XMLUtils.parse(new FileInputStream(filePath.asAbsoluteString()),
    						  ignoredEntities);
    }
    /**
     * Loads a XML File, parses it and returns a DOM {@link Document}
     * @param file
     * @return 
     * @throws IOException
     * @throws SAXException
     */
    public static Document parse(final File file) throws IOException,
    													 SAXException {
        return XMLUtils.parse(new FileInputStream(file));
    }
    /**
     * Loads a XML from a remote url, parses it and returns a DOM {@link Document}
     * @param url 
     * @param charset 
     * @param cookies cookies to send in the remote http request (every element is a two-position array: 0=cookieName,1=cookieValue)
     * @return 
     * @throws SAXException 
     * @throws IOException 
     */
    public static Document parse(final Url url,
    					  		 final String[]... cookies) throws IOException,
    															   SAXException {
    	return XMLParseUtils.parse(url,
    						  	   Charset.defaultCharset(),
    						  	   cookies);
    }
    /**
     * Loads a XML from a remote url, parses it and returns a DOM {@link Document}
     * @param url 
     * @param charset response encoding
     * @param cookies cookies to send in the remote http request (every element is a two-position array: 0=cookieName,1=cookieValue)
     * @return 
     * @throws SAXException 
     * @throws IOException 
     */
    public static Document parse(final Url url,
    					  		 final Charset charset,
    					  		 final String[]... cookies) throws IOException,
    					  		 								   SAXException {
    	return XMLParseUtils.parse(url,
    							   null,		// no proxy
    					  	  	   charset,
    					  	  	   cookies);
    }
    /**
     * Loads a XML from a remote url, parses it and returns a DOM {@link Document}
     * @param url 
     * @param proxySettings proxy info
     * @param cookies cookies to send in the remote http request (every element is a two-position array: 0=cookieName,1=cookieValue)
     * @return 
     * @throws SAXException 
     * @throws IOException 
     */
    public static Document parse(final Url url,final HttpClientProxySettings proxySettings,
    					  		 final String[]... cookies) throws IOException,
    														  	   SAXException {
    	return XMLParseUtils.parse(url,proxySettings,
    					  	  	   Charset.defaultCharset(),
    					  	  	   cookies);
    }
    /**
     * Loads a XML from a remote url, parses it and returns a DOM {@link Document}
     * @param url 
     * @param proxySettings proxy info
     * @param charset response encoding
     * @param cookies cookies to send in the remote http request (every element is a two-position array: 0=cookieName,1=cookieValue)
     * @return 
     * @throws SAXException 
     * @throws IOException 
     */
	@SuppressWarnings("resource")
	public static Document parse(final Url url,
								 final HttpClientProxySettings proxySettings,
    					  		 final Charset charset,
    					  		 final String[]... cookies) throws IOException,
    														  	   SAXException {
        final HttpURLConnection conx = _getURLConnection(url,
        												 proxySettings,
        										   		 charset,
        										   		 cookies);
        @Cleanup InputStream is = conx.getInputStream();
        Document outDoc = XMLUtils.parse(is);
        conx.disconnect();
        return outDoc;
    }
    /**
     * Opens an url connection
     * @param url target url
     * @param proxySettings proxy info
     * @param charset response encoding
     * @param cookies cookies to send in the remote http request (every element is a two-position array: 0=cookieName,1=cookieValue)
     * @return 
     * @throws IOException 
     */
    private static HttpURLConnection _getURLConnection(final Url url,
    												   final HttpClientProxySettings proxySettings,
    												   final Charset charset,
    												   final String[]... cookies) throws IOException {
    	//System.setProperty("javax.net.debug","all");

        // Set cookies
    	Map<String,String> cookiesMap = null;
        if (cookies != null) {
        	cookiesMap = new HashMap<String,String>(cookies.length);
        	for (String[] cookie : cookies) {
        		if (cookie != null) cookiesMap.put(cookie[0],cookie[1]);
        	}
        }
    	// Open connection
        HttpURLConnection conx = null;
        if (proxySettings != null && proxySettings.isEnabled()) {
        	conx = HttpClient.forUrl(url).usingCharset(charset)
        					 .settingCookies(cookiesMap)
        					 .GET()
        					 .getConnection()
        					 	 .usingProxy(proxySettings)
	        					 .withTimeOut(10000)		// timeout de 10 sg
	        					 .noAuth();
        } else {
        	conx = HttpClient.forUrl(url).usingCharset(charset)
        					 .settingCookies(cookiesMap)
        					 .GET()
        					 .getConnection()
        					 	 .usingProxy(proxySettings)
	        					 .withTimeOut(10000)		// timeout de 10 sg
	        					 .noAuth();
        }
        return conx;
    }
    /**
     * Gets a DOM {@link Document} from a XML
     * @param xml 
     * @param ignoredEntities EXTRENAL entities to be ignored
     *          - Internal entities: <!ENTITY entityname "replacement text">
     *          - External entities: <!ENTITY entityname [PUBLIC "public-identifier"] SYSTEM "system-identifier">
     *        (this is used for example to avid DTD validation set at DOCTYPE entity
     *         <!DOCTYPE record SYSTEM "dcr4.5.dtd">)
     *        The IGNORED external entities are provided in an array like publicId:systemId
     * @return 
     * @throws SAXException 
     */
    public static Document parse(final String xml,
    							 final String... ignoredEntityes) throws SAXException {
    	return XMLUtils.parse(new ByteArrayInputStream(xml.getBytes()),ignoredEntityes);
    }

 }