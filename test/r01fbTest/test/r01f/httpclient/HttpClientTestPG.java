package r01f.httpclient;

import java.io.IOException;
import java.net.MalformedURLException;

import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.url.Host;

public class HttpClientTestPG  {

	public static void testHTTPSDirect() throws MalformedURLException, IOException {
		String uri = "https://www.euskadi.eus/p12gtWar/p12gRPCDispatcherServlet";
		HttpResponse response = HttpClient.forUrl(uri)
									      .GET()
									      .getResponse()
									      		.notUsingProxy().withoutTimeOut().noAuth();
		String responseAsString = response.loadAsString();
		System.out.println(responseAsString);
	}

	public static void testHTTPSProxy() throws MalformedURLException, IOException {
		String uri = "https://www.euskadi.eus/p12gtWar/p12gRPCDispatcherServlet";
		HttpResponse response = HttpClient.forUrl(uri)
								      .GET()
								      .getResponse()
								      		.usingProxy(Host.strict("intercon"),8080,
								      					UserCode.forId("iolabaro"),
								      					Password.forId("XXXX"))
								      		.withoutTimeOut().noAuth();
		String responseAsString = response.loadAsString();
		System.out.println(responseAsString);
	}

	public static void main (String[] args) throws MalformedURLException, IOException {
		testHTTPSDirect();
	}
}
