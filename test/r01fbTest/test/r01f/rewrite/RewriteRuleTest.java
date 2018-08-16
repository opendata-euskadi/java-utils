package r01f.rewrite;

import org.junit.Assert;
import org.junit.Test;

public class RewriteRuleTest {
	@Test
	public void rewriteRuleTest() {
		RewriteRule r = RewriteRule.matching("/(read|write)/triplestore/?(.*)")
								   .rewriteTo("/$1/blazegraph/$2");
		
		Assert.assertTrue(r.applyTo("/read/triplestore/sparql")
						   .equals("/read/blazegraph/sparql"));
		Assert.assertTrue(r.applyTo("/read/triplestore/")
						   .equals("/read/blazegraph/"));
		Assert.assertTrue(r.applyTo("/read/triplestore")
						   .equals("/read/blazegraph/"));
		Assert.assertTrue(r.applyTo("/writeeeee/triplestore")
						   .equals("/writeeeee/triplestore"));
	}
}
