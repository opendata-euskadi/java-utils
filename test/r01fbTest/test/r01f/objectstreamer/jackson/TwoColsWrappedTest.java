package r01f.objectstreamer.jackson;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.Path;
import r01f.util.types.collections.Lists;

public class TwoColsWrappedTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void test() throws IOException  {
		XmlMapper xmlMapper = new XmlMapper();
		TestBean myBean = new TestBean(Lists.<Path>newArrayList(Path.from("/a/b/c"),
										   						Path.from("d/e/f")),
									   Lists.<Path>newArrayList(Path.from("/foo/bar/baz"),
										   						Path.from("x/y/z")));
		System.out.println("XML: " + xmlMapper.writeValueAsString(myBean));		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@JacksonXmlRootElement(localName="bean") 
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestBean {
		@JacksonXmlElementWrapper(localName="paths") @JacksonXmlProperty(localName="path")
		@Getter @Setter private Collection<Path> _pathCol;
		
		@JacksonXmlElementWrapper(localName="otherPaths") @JacksonXmlProperty(localName="path")
		@Getter @Setter private Collection<Path> _otherPathCol;
	}
	private static TestBean _buildTestBean() {
		TestBean myBean = new TestBean(Lists.<Path>newArrayList(Path.from("/a/b/c"),
										   						Path.from("d/e/f")),
									   Lists.<Path>newArrayList(Path.from("/foo/bar/baz"),
										   						Path.from("x/y/z")));
		return myBean;
	}
}
