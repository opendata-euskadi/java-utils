package r01f.xmlproperties;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesComponentDef;
import r01f.xmlproperties.XMLPropertiesComponentDefLoader;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.XMLPropertiesGuiceModule;

public class TestXMLProperties {
	@Test
	public void testXMLPropertiesComponentDefLoad() throws IOException {
		try {
			String defXml = "<componentDef name='myName'>" + 
									"<propertiesFileURI>/config/r01fb.properties.xml</propertiesFileURI>" + 
									"<numberOfPropertiesEstimation>10</numberOfPropertiesEstimation>" +
									"<resourcesLoader id='myResLoader' type='CLASSPATH'>" + 
										"<reloadControl impl='PERIODIC' enabled='true' checkInterval='2s'>" +
											"<props>" +
												"<a>a_value</a>" + 
												"<b>b_value</b>" +
											"</props>" +
										"</reloadControl>" + 
										"<props>" +
											"<a>a_value</a>" + 
											"<b>b_value</b>" +
										"</props>" +
									"</resourcesLoader>" + 
						    "</componentDef>";
			XMLPropertiesComponentDef def = XMLPropertiesComponentDefLoader.load(defXml);
			System.out.println(def.debugInfo());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}
	@Test
	public void testNoIOC() {
		// existing component definition
		XMLPropertiesForApp testProps = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
														    .notUsingCache();
		_doTest(testProps,AppComponent.forId("test"));
		// non-existing component definition
		XMLPropertiesForApp test2Props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
														     .notUsingCache();
		_doTest(testProps,AppComponent.forId("test_no_comp"));
	}
	@Test
	public void testIOC() {
		Injector injector = Guice.createInjector(new XMLPropertiesGuiceModule());
		XMLProperties props = injector.getInstance(XMLProperties.class);
		_doTest(props.forApp(AppCode.forId("r01fb")),AppComponent.forId("test"));
		
	}
	private void _doTest(final XMLPropertiesForApp appProps,
						 final AppComponent comp) {
		XMLPropertiesForAppComponent testCompProps = appProps.forComponent(comp);
		
		String attrValue = testCompProps.propertyAt("/properties/aProp/@attr")
									   .asString();
		Assert.assertEquals(attrValue,"attrValue");

		
		String propValue = testCompProps.propertyAt("/properties/aProp")
									    .asString();
		Assert.assertEquals(propValue,"propValue");		
	}
}
	