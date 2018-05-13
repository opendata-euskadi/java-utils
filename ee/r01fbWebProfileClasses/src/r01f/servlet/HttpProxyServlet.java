package r01f.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

import lombok.experimental.Accessors;



/**
 * A simple proxy servlet for moct / testing purposes
 * 
 * It can also be used as a reverse proxy for GWT development mode to deploy no-gwt server code at a Tomcat / Weblogic / etc 
 * app server (other than the GWT eclipse plugin embeded jetty server)
 * <pre>
 * 
 *						  [Cliente GWT JS client]
 *									|
 *									| http://localhost:8888/xxxFrontEndWar/xxxServlet	<-- the GWT js client in [Development mode] makes requests to 
 *									|														the GWT eclipse plugin Jetty server runnit at port 8888	
 *									|
 *						[GWT eclipse plugin Jetty server] (usually runs at port 8888)
 *							  [ProxyServlet]
 *							        |
 *							        | http://localhost:8080/xxxFrontEndWar/xxxServlet	<-- the Jetty server proxies all Servlet requests to the Tomcat AppServer
 *									|													    runing at
 *							        |
 *						 	  [Tomcat:8080]
 *							 [xxxFrontEndWar]
 * </pre>
 * Simply setup a ProxyServlet at the GWT plugin embedded Jetty server that delegates business logic requests to the "real" Tomcat / weblogic / etc server
 * Configure the ProxyServlet at the WAR that gets deployed at the GWT plugin embedded Jetty server (folder war/WEB-INF/web.xml in the GWT eclipse project):
 *				<servlet>
 *			        <servlet-name>xxWarPROXY</servlet-name>
 *			        <servlet-class>r01f.servlet.GenericHttpProxyServlet</servlet-class>
 *
 *					<!-- the local app server (Tomcat / Weblogic / etc) server host / port -->
 *			        <init-param>
 *			            <param-name>TargetAppServerHost</param-name>		
 *			            <param-value>127.0.0.1</param-value>
 *			        </init-param>
 *			        <init-param>
 *			            <param-name>TargetAppServerPort</param-name>
 *			            <param-value>8080</param-value> 		
 *			        </init-param>
 *
 *					<!-- When the proxied-request is a GWT client-to-RemoteServlet request, a header called X-gwtCodeRelPath
 *						 is appended to the proxied request including the GWT-compiled code path relative to the destination WAR
 *						 This relative path is the location of the policy files generated by GWT-compiler for the serialized types -->
 *			        <init-param>
 *			            <param-name>gwtCompiledCodeProxiedWarRelativePath</param-name>			<!-- path relativo al war al que se hace proxy -->
 *			            <param-value>/{static_content_alias}/gwt/xxxFrontEndGWT</param-value>	<!-- del código estático compilado de GWT -->
 *			        </init-param>
 *			    </servlet>
 *			
 *			    <servlet-mapping>
 *			        <servlet-name>xxWarPROXY</servlet-name>
 *			        <url-pattern>/xxWar/*</url-pattern>		<!-- every request to xxWar is proxied to tomcat -->
 *			    </servlet-mapping>
 */
@Singleton
@Accessors(prefix="_")
public class HttpProxyServlet 
	 extends HttpServlet {

	private static final long serialVersionUID = 4118855885724222239L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private HttpProxy _genericProxy;	
    
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////   
    @Override
    public String getServletInfo() {
        return "R01 Proxy Servlet (DO NOT USE IN PROD ENVIRONMENT -only for testing / mock purposes-";
    } 
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Initialize the <code>ProxyServlet</code>
     * @param servletConfig The Servlet configuration passed in by the servlet container
     */
    @Override
	public void init(final ServletConfig servletConfig) {
    	_genericProxy = new HttpProxy(new HttpProxyServletConfig(servletConfig));
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Performs an HTTP GET request
     * @param originalRequest  The {@link HttpServletRequest} object passed
     *                         in by the servlet engine representing the
     *                         client request to be proxied
     * @param responseToClient The {@link HttpServletResponse} object by which
     *                         we can send a proxied response to the client
     */
    @Override
	public void doGet(final HttpServletRequest originalRequest,
    				  final HttpServletResponse responseToClient) throws IOException, 
    				  													 ServletException {
    	_genericProxy.proxyGET(originalRequest,
    						   responseToClient);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	POST
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Performs an HTTP POST request
     * @param originalReqest  The {@link HttpServletRequest} object passed
     *                     	  in by the servlet engine representing the
     *                     	  client request to be proxied
     * @param responseToClient The {@link HttpServletResponse} object by which
     *                         we can send a proxied response to the client
     */
    @Override
	public void doPost(final HttpServletRequest originalReqest, 
    				   final HttpServletResponse responseToClient) throws IOException,
    				   													  ServletException {
    	_genericProxy.proxyPOST(originalReqest,
    							responseToClient);
    }
 }
