package r01f.types;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import r01f.util.types.StringCustomizeUtils;

public class StringCustomizeUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReplaceString() {
		fail("Not yet implemented");
	}

	@Test
	public void testReplaceVariableValuesStringCharProperties() {
		fail("Not yet implemented");
	}

	@Test
	public void testReplaceVariableValuesStringCharMapOfStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testReplaceVariableValuesStringCharCharMapOfStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testReplaceVariableValuesStringCharMapOfStringStringBoolean() {
		String metaDataSQL = "$METADATAFIELD$ $OPERATOR$ $METADATAVALUE$";
		Map<String,String> varValues = new HashMap<String,String>(4);
        varValues.put("METADATA_TABLE", "TABLE_NAME");
        
        // Must replace internal variable $METADATA_TABLE$
        varValues.put("METADATAFIELD", "TRANSLATE(UPPER($METADATA_TABLE$.MAIN_ALIAS_04),'аимсз','AEIOU')");
        varValues.put("OPERATOR", "LIKE");
        varValues.put("METADATAVALUE", "TRANSLATE('%TEST-IGA-222%','аимсз','AEIOU')");
        
        try {
        	metaDataSQL = StringCustomizeUtils.replaceVariableValues(metaDataSQL,'$',varValues,true);
        } catch ( Exception e ) {
        	assertFalse(e.getMessage(), true);
        	return;
        }
        
		assertTrue(true);
	}

	@Test
	public void testReplaceVariableValuesStringCharCharMapOfStringStringBoolean() {
		fail("Not yet implemented");
	}

}
