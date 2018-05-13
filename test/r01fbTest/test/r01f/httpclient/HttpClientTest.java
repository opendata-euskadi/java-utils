package r01f.httpclient;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.url.Host;
import r01f.types.url.Url;


@Accessors(prefix="_")
@RequiredArgsConstructor
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class HttpClientTest  {

/////////////////////////////////////////////////////////////////////////////////////////
//  JUnit
/////////////////////////////////////////////////////////////////////////////////////////
	@BeforeClass
	public static void setUpBeforeClass() {

	}
	@AfterClass
	public static void tearDownAfterClass()  {

	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@Test @SuppressWarnings("static-method")
	public void testHTTPDirect() throws IOException {
		int responseCode = HttpClient.forUrl(Url.from("www.google.com"))
									  .GET()
									  		.getConnection()
									  				.directNoAuthConnected()
									  		.getResponseCode();
	}
	//@Test @SuppressWarnings("static-method")
	public void testHTTPProxy() {


	}
	//@Test @SuppressWarnings("static-method")
	public void testHTTPSDirect() throws MalformedURLException, IOException {
		String uri = "https://portal.kutxabank.es/cs/Satellite/kb/es/particulares";
		HttpResponse response = HttpClient.forUrl(uri)
									      .GET()
									      .getResponse()
									      		.notUsingProxy().withoutTimeOut().noAuth();
		String responseAsString = response.loadAsString();
		log.debug(responseAsString);

	}
//	@Test @SuppressWarnings("static-method")
	public void testHTTPSProxy() throws MalformedURLException, IOException {
		String uri = "https://portal.kutxabank.es/cs/Satellite/kb/es/particulares";
		HttpResponse response = HttpClient.forUrl(uri)
								      .GET()
								      .getResponse()
								      		.usingProxy(Host.strict("intercon"),8080,
								      					UserCode.forId("iolabaro"),
								      					Password.forId("XXXXXXXX"))
								      		.withoutTimeOut().noAuth();
		String responseAsString = response.loadAsString();
		log.debug(responseAsString);
	}
}
