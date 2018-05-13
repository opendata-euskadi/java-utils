package r01f.geo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.Collator;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.RequiredArgsConstructor;
import r01f.httpclient.HttpClientProxySettings;
import r01f.internal.R01F;
import r01f.types.url.Url;
import r01f.util.types.Strings;

/**
 * Utilidad que contacta con el geoCoder de google para obtener las coordenadas de 
 * una direcci�n a partir de su descripci�n textual
 * El API de geo-codificacion de google (http://code.google.com/apis/maps/documentation/geocoding/)
 * es un API REST donde una petici�n tiene la forma:
 * 		http://maps.googleapis.com/maps/api/geocode/output?parameters
 * Donde:
 * 		ouput=json para obtener la respuesta en formato json: http://maps.googleapis.com/maps/api/geocode/json?parameters
 * 		ouput=xml para obtener la respuesta en formato xml: http://maps.googleapis.com/maps/api/geocode/xml?parameters
 * Los par�metros que se utilizan son:
 * 		address=12,General+Concha,Bilbao --: la direcci�n que se quiere geo-codificar
 * 		region=es
 * 		sensor=false --: indica si la peticion viene de un dispositivo con sensor (siempre false en este caso)
 * 
 * La respuesta t�pica en formato XML est� compuesta por N items <result> y lo que se busca es la localizaci�n (las coordenadas)
 *<GeocodeResponse> 
 * 	<status>OK</status> 
 * 	<result> 
 *  	<type>street_address</type> 
 *  	<formatted_address>1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA</formatted_address> 
 *  	<address_component> 
 *   		<long_name>1600</long_name> 
 *   		<short_name>1600</short_name> 
 *   		<type>street_number</type> 
 *  	</address_component> 
 *  	<address_component> 
 *   		<long_name>Amphitheatre Pkwy</long_name> 
 *   		<short_name>Amphitheatre Pkwy</short_name> 
 *   		<type>route</type> 
 *  	</address_component> 
 *  	<address_component> 
 *   		<long_name>Mountain View</long_name> 
 *  	 	<short_name>Mountain View</short_name> 
 *   		<type>locality</type> 
 *   		<type>political</type> 
 *  	</address_component> 
 *  	<address_component> 
 *   		<long_name>San Jose</long_name> 
 *   		<short_name>San Jose</short_name> 
 *   		<type>administrative_area_level_3</type> 
 *   		<type>political</type> 
 *  	</address_component> 
 *  	<address_component> 
 *   		<long_name>Santa Clara</long_name> 
 *   		<short_name>Santa Clara</short_name> 
 *  	 	<type>administrative_area_level_2</type> 
 *   		<type>political</type> 
 *  	</address_component> 
 *  	<address_component> 
 *   		<long_name>California</long_name> 
 *   		<short_name>CA</short_name> 
 *   		<type>administrative_area_level_1</type> 
 *   		<type>political</type> 
 *  	</address_component> 
 *  	<address_component> 
 *   		<long_name>United States</long_name> 
 *   		<short_name>US</short_name> 
 *   		<type>country</type> 
 *   		<type>political</type> 
 *  	</address_component> 
 *  	<address_component> 
 *   		<long_name>94043</long_name> 
 *   		<short_name>94043</short_name> 
 *   		<type>postal_code</type> 
 *  	</address_component> 
 *  	<geometry> 
 *   		<location> 
 *    			<lat>37.4217550</lat> 
 *    			<lng>-122.0846330</lng> 
 *   		</location> 
 *   		<location_type>ROOFTOP</location_type> 
 *   		<viewport> 
 *    			<southwest> 
 *     				<lat>37.4188514</lat> 
 *     				<lng>-122.0874526</lng> 
 *    			</southwest> 
 *    			<northeast> 
 *     				<lat>37.4251466</lat> 
 *     				<lng>-122.0811574</lng> 
 *    			</northeast> 
 *   		</viewport> 
 *  	</geometry> 
 * 	</result> 
 *</GeocodeResponse>   
 */
@RequiredArgsConstructor
public class GoogleGeoCoder {
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTANTES
/////////////////////////////////////////////////////////////////////////////////////////
	private static String CHARSET_NAME_GOOGLEGEOCODER = R01F.ENCODING_ISO_8859_1;
	private static Charset CHARSET_GOOGLEGEOCODER = Charset.forName(CHARSET_NAME_GOOGLEGEOCODER);
/////////////////////////////////////////////////////////////////////////////////////////
// 	ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
	private final Charset _charsetUsedByGoogleGeoCoder;
	private final String _googleAPIEndPoint;
	private final HttpClientProxySettings _proxySettings;
	private final Properties _blackList;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public GoogleGeoCoder(final String googleAPIEndPoint,final HttpClientProxySettings proxySettings,
						  final Properties blackList) {
		this(CHARSET_GOOGLEGEOCODER,
			 googleAPIEndPoint,proxySettings,
			 blackList);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene las coordenadas (latitud/longitud) en google maps a partir de 
	 * la direcci�n textual
	 * @param address direcci�n (ej: gran via, 24)
	 * @param locality localidad (ej: bilbao)
	 * @param state estado -pasar siempre null-
	 * @param country pais -pasar siempre null-
	 * @param postalCode codigo postal - se utiliza para filtrar en el caso de que google devuelva m�s de un resultado
	 * @return
	 */
	public float[] geoCodeAddress(final String address,final String locality,final String state,final String country,
								  final String postalCode) {
		String addr = _extractAddress(address,locality,state,country);
		if (addr != null) {
			String geoCoderRestURL = _googleAPIEndPoint + "?address=" + addr + "&language=es&region=es&sensor=false";
			//System.out.println(geoCoderRestURL);
			try {
				Document response = XMLParseUtils.parse(Url.from(geoCoderRestURL),
												   				 _proxySettings,
												   				 _charsetUsedByGoogleGeoCoder);
				//System.out.println(XMLUtils.write(response,null));
				float[] lat_long = _extractGeoData(response,
												   address,locality,state,country,postalCode);
				return lat_long;
			} catch(IOException ioEx) {
				ioEx.printStackTrace(System.out);				
			} catch(SAXException saxEx) {
				saxEx.printStackTrace(System.out);
			}
		}
		return null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METODOS PRIVADOS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Extrae la informaci�n de geo-localizaci�n a partir de la respuesta del servicio REST en formato XML 
     * se le pasa tambien la informaci�n de partida para la b�squeda para intentar "filtrar" el 
     * resultado m�s adecuado en caso de que se devuelva m�s de un resultado
     * @param geoCoderResponse la respuesta xml del servicio rest de geocodificacion de google
	 * @param address direcci�n (ej: gran via, 24)
	 * @param locality localidad (ej: bilbao)
	 * @param state estado (ej: vizcaya)
	 * @param country pais (ej: espa�a)
	 * @param postalCode codigo postal - se utiliza para filtrar en el caso de que google devuelva m�s de un resultado
     * @return
     */
    private static float[] _extractGeoData(final Document geoCoderResponse,
    									   final String address,final String locality,final String state,final String country,
    									   final String postalCode) {
    	float[] outGeoData = null;
    	
    	try {
	    	// Preparar XPath
	    	XPathFactory xPathFactory = XPathFactory.newInstance();
	        XPath xPath = xPathFactory.newXPath();
	        
	        // Primero ver si la llamada ha ido bien: 
			// 		- "OK" indicates that no errors occurred; the address was successfully parsed and at least one geocode was returned.
			//  	- "ZERO_RESULTS" indicates that the geocode was successful but returned no results. This may occur if the geocode was passed a non-existent address or a latlng in a remote location.
			//  	- "OVER_QUERY_LIMIT" indicates that you are over your quota.
			//  	- "REQUEST_DENIED" indicates that your request was denied, generally because of lack of a sensor parameter.
			//  	- "INVALID_REQUEST" generally indicates that the query (address or latlng) is missing.        
	        XPathExpression xPathExpr = xPath.compile("GeocodeResponse/status");
	        String status = (String)xPathExpr.evaluate(geoCoderResponse,XPathConstants.STRING);
	        if (status != null && status.equalsIgnoreCase("OK")) {
	        	// Si la respuesta es OK, hay que filtrar el resultado m�s adecuado comparando cada uno de los resultados
	        	// (pueden ser m�ltiples) con los criterios de filtro, para ello se "cuenta" el n�mero de criterios que se cumplen
	        	// ... de esta forma se puede devolver el resultado que cumple m�s criterios, siempre que se cumpla alguno de ellos.
	        	xPathExpr = xPath.compile("GeocodeResponse/result");
	        	NodeList results = (NodeList)xPathExpr.evaluate(geoCoderResponse,XPathConstants.NODESET);
	        	int[] resultItemsQuality = new int[results.getLength()];
	        	for (int i=0; i<results.getLength(); i++) {
	        		resultItemsQuality[i] = _countFilterCriteriaMatchings(xPath,results.item(i),
	        									  						  address,locality,state,country,postalCode);
	        	}
	        	// ... devolver el resultado de m�s calidad
	        	int candidateIndex = -1;
	        	int bestQuality = -1;
	        	for (int i=0; i<resultItemsQuality.length; i++) {
	        		if (resultItemsQuality[i] > bestQuality) {
	        			candidateIndex = i;
	        			bestQuality = resultItemsQuality[i];
	        		}
	        	}
	        	if (candidateIndex >= 0) {
	        		// Extraer la latitud y longitud
	        		Node candidateNode = results.item(candidateIndex);
			        xPathExpr = xPath.compile("geometry/location/lat");
			        String lat = (String)xPathExpr.evaluate(candidateNode,XPathConstants.STRING);
			        xPathExpr = xPath.compile("geometry/location/lng");
			        String lng = (String)xPathExpr.evaluate(candidateNode,XPathConstants.STRING);
			        xPathExpr = xPath.compile("formatted_address");
			        
			        if (!Strings.isNullOrEmpty(lat) && !Strings.isNullOrEmpty(lng)) {
				        outGeoData = new float[] {Float.parseFloat(lat),Float.parseFloat(lng)};
				        //String formattedAddr = (String)xPathExpr.evaluate(candidateNode,XPathConstants.STRING);
				        //System.out.println(">>>>" + outGeoData[0] + "," + outGeoData[1] + ": " +formattedAddr);
			        }
	        	}
	        }
    	} catch (XPathExpressionException xpathEx) {
    		xpathEx.printStackTrace(System.out);
    	}
        return outGeoData;
    }
    /**
     * Cuenta el n�mero de criterios de filtro que "cumple" el resultado
     * @param currResult resultado
	 * @param address direcci�n (ej: gran via, 24)
	 * @param locality localidad (ej: bilbao)
	 * @param state estado -pasar siempre null-
	 * @param country pais -pasar siempre null-
	 * @param postalCode codigo postal - se utiliza para filtrar en el caso de que google devuelva m�s de un resultado
     * @return un entero que indica el n�mero de criterios que se verifican
     */
    private static int _countFilterCriteriaMatchings(final XPath xPath,final Node currResult,
    										  		 final String address,final String locality,final String state,final String country,
    										  		 final String postalCode) {
    	// Instanciar un comparador que NO tenga en cuenta may�sculas, min�sculas, acentos, etc
		Collator collator = Collator.getInstance(new Locale("es","ES"));
		collator.setStrength(Collator.PRIMARY);   	
    	
    	int outCount = 0;
    	try {
    		if (postalCode != null) {
		        XPathExpression xPathExpr = xPath.compile("address_component[type = \"postal_code\"]/long_name");
		        String resultPostalCode = (String)xPathExpr.evaluate(currResult,XPathConstants.STRING);
		        if (resultPostalCode != null && _areAddItemsEquivalent(resultPostalCode,postalCode)) outCount++;
    		}
    		if (country != null) {
		        XPathExpression xPathExpr = xPath.compile("address_component[type = \"country\"]/long_name");
		        String resultCountry = (String)xPathExpr.evaluate(currResult,XPathConstants.STRING);
		        if (resultCountry != null && _areAddItemsEquivalent(resultCountry,country)) outCount++;    			
    		}
    		if (state != null) {
		        XPathExpression xPathExpr = xPath.compile("address_component[type = \"administrative_area_level_2\"]/long_name");
		        String resultState = (String)xPathExpr.evaluate(currResult,XPathConstants.STRING);
		        if (resultState != null && _areAddItemsEquivalent(resultState,state)) outCount++;     			
    		}
    		if (locality != null) {
		        XPathExpression xPathExpr = xPath.compile("address_component[type = \"locality\"]");
		        NodeList localities = (NodeList)xPathExpr.evaluate(currResult,XPathConstants.NODESET);
		        if (localities != null) {
		        	for (int i=0; i<localities.getLength(); i++) {
		        		Node currLoc = localities.item(i);
				        XPathExpression otherXPathExpr = xPath.compile("long_name");
				        String loc = (String)otherXPathExpr.evaluate(currLoc,XPathConstants.STRING);				       
				        if (loc != null && _areAddItemsEquivalent(loc,locality)) {
				        	outCount++; 
				        	break;
				        }
		        	}
		        }
    		}
    	} catch (XPathExpressionException xpathEx) {
    		xpathEx.printStackTrace(System.out);
    	}	        
	    return outCount;  
    }
	private static boolean _areAddItemsEquivalent(final String item1,final String item2) {
    	// eliminar los acentos utilizando la t�cnica de descomposicion canonica y pasar a may�sculas ambas cadenas
		// ver http://www.v3rgu1.com/blog/231/2010/programacion/eliminar-acentos-y-caracteres-especiales-en-java/
		Locale es_loc = new Locale("es","ES");
	    Pattern pattern = Pattern.compile("\\P{ASCII}");		
	    String item1_normalized = pattern.matcher(Normalizer.normalize(item1,Normalizer.Form.NFD)).replaceAll("").toUpperCase(es_loc);
	    String item2_normalized = pattern.matcher(Normalizer.normalize(item2,Normalizer.Form.NFD)).replaceAll("").toUpperCase(es_loc);
	    
	    // Ver si las cadenas son equivalentes (una est� contenida en la otra)
	    boolean result = false;
	    if (item1_normalized.length() > item2_normalized.length()) {
	    	result = item1_normalized.contains(item2_normalized);
	    } else {
	    	result = item2_normalized.contains(item1_normalized);
	    }
	    return result;	
	}
	/**
	 * Extrae la parte de la direcci�n que se pasa al api de geo-codificaci�n
	 * En muchas ocasiones, la direcci�n tiene el formato:
	 * 		calle, n�mero - piso/mano
	 * si se pasa el piso/mano al api de geo-codificacion NO devuelve resultados,
	 * asi que lo mejor es eliminar esta parte
	 * Adem�s de lo anterior, se cambiar el formato de la direcci�n a: n�mero, calle
	 * @param address la direcci�n original
	 * @param locality la localidad (ciudad)
	 * @param state el estado
	 * @return la parte de la direcci�n que se pasa al api de google
	 */
	private String _extractAddress(final String address,final String locality,final String state,final String country) {
		Locale esLoc = new Locale("es","ES");
		String theAddress = (new String (address)).toUpperCase(esLoc);		// Pasar a may�sculas todo
		
		// Eliminar palabras como "calle", "kalea", "c/", etc
		if (_blackList != null) {
			for(Object blackListItem : _blackList.values()) {
				String itemStr = ((String)blackListItem).toUpperCase(esLoc); 
				if (theAddress.contains(itemStr)) theAddress = theAddress.replaceAll(itemStr,"");
			}
		}
	    // Extraer el n�mero de la calle de la direccion
		String regEx = "([^\\,]*)\\,?\\s*([0-9]*).*";
		String outAddr = null;
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(theAddress);
		if (m.matches()) {
			String street = null;
			String number = null;
			if (m.groupCount() == 2) {			// se pasa la calle y el numero: invertir el orden (numero, calle)
				number = m.group(2);
				street = m.group(1);
			} else if (m.groupCount() == 1) {	// se pasa solo la calle, devolver simplemente la calle
				street = m.group(1);
			}
		    street = _normalizeEscapeAndEncode(street);
			outAddr = (!Strings.isNullOrEmpty(number) ? number + ",+" : "") + street;			
		} 		
		if (outAddr != null && locality != null) {
			String theLocality = _normalizeEscapeAndEncode(locality);			
			if (theLocality.contains("-")) {
				outAddr = outAddr + ",+" + theLocality.split("-")[0];	// ej: Vitoria-Gasteiz o Donostia-San Sebastian
			} else {
				outAddr = outAddr + ",+" + theLocality;
			}
		}
		if (outAddr != null && state != null) {
			String theState = _normalizeEscapeAndEncode(state);
			outAddr = outAddr + ",+" + theState;
		}
		if (outAddr != null && country != null) {
			String theCountry = _normalizeEscapeAndEncode(country);
			outAddr = outAddr + ",+" + theCountry;
		}

		return outAddr;
	}
    private static String _normalizeEscapeAndEncode(final String str) {
	    String outStr = null;
	    
	    // 1: Normalizar --> Eliminar acentos y pasar a mayusculas
		// En todos los textos eliminar los acentos utilizando la t�cnica de descomposicion canonica y pasar a may�sculas ambas cadenas
		// ver http://www.v3rgu1.com/blog/231/2010/programacion/eliminar-acentos-y-caracteres-especiales-en-java/			
		Locale es_loc = new Locale("es","ES");
	    Pattern pattern = Pattern.compile("\\P{ASCII}");
	    outStr = pattern.matcher(Normalizer.normalize(str,Normalizer.Form.NFD)).replaceAll("").toUpperCase(es_loc);
	    // 2: escapar caracteres HTML
	    outStr = StringEscapeUtils.escapeHtml4(str);
	    // 3: Codificar en la URL
	    try {
	    	outStr = URLEncoder.encode(outStr,Charset.defaultCharset().name());
		} catch(UnsupportedEncodingException encEx) {
			// No hacer nada... ignorar el error, al menos devuelve lo codificado hasta ahora
		}
	    return outStr;
    }
}

