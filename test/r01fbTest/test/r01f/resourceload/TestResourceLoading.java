package r01f.resourceload;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import r01f.io.util.StringPersistenceUtils;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.types.Path;

public class TestResourceLoading {
	@Test
	public void testResourceLoading() throws IOException {
		ResourcesLoader resLoader = ResourcesLoaderBuilder.DEFAULT_RESOURCES_LOADER;
		String resTxt = StringPersistenceUtils.load(resLoader.getReader(Path.from("data/resource.txt")));
		Assert.assertEquals(resTxt,"This is a resource!");
	}
}
