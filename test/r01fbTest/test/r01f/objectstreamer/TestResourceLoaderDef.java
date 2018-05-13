package r01f.objectstreamer;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.resources.ResourcesLoaderDef;
import r01f.types.Path;
import r01f.xmlproperties.XMLPropertiesComponentDef;

@Slf4j
public class TestResourceLoaderDef 
	 extends TestObjectStreamerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	EPOCH 
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testEpochDateFormat() throws IOException  {
		XMLPropertiesComponentDef compDef = new XMLPropertiesComponentDef();
		compDef.setName(AppComponent.forId("myAppComponent"));
		compDef.setNumberOfPropertiesEstimation(100);
		compDef.setPropertiesFileURI(Path.from("d:/a/b/c"));
		compDef.setLoaderDef(ResourcesLoaderDef.DEFAULT);
		
		_doTest(compDef,
			    XMLPropertiesComponentDef.class, 
				new MarhallTestCheck<XMLPropertiesComponentDef>() {
						@Override
						public void check(final XMLPropertiesComponentDef original,final XMLPropertiesComponentDef readed) {
							//Assert.assertEquals(original,readed);
						}
				});
	}
}
