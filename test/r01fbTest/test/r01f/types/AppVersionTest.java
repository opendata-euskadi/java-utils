package r01f.types;

import org.junit.Assert;
import org.junit.Test;


public class AppVersionTest {
	@Test
	public void testAppVersion() {
		AppVersion v1 = new AppVersion(1,0,0);
		AppVersion v2 = new AppVersion(1,0,0,"alpha");
		AppVersion v3 = new AppVersion(1,1,0);
		AppVersion v4 = new AppVersion(2,1,0);
		AppVersion v5 = AppVersion.from("2.2.1");
		
		Assert.assertTrue(v1.isSameAs(v2));
		Assert.assertTrue(v3.isGreaterThan(v1));
		Assert.assertTrue(v4.isGreaterThan(v1));
		Assert.assertTrue(v5.isGreaterThan(v4));
	}
}
