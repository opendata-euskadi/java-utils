package r01f.objectstreamer;

import java.io.IOException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Dates;

@Slf4j
public class TestDateFormat 
	 extends TestObjectStreamerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	EPOCH 
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="epochDate")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestEpochDateContainerBean {
		@MarshallField(as="dateAsEpoch",
					  dateFormat=@MarshallDateFormat(use=DateFormat.EPOCH),
					  whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private Date _date;
	}
	@Test
	public void testEpochDateFormat() throws IOException  {
		TestEpochDateContainerBean epochDate = new TestEpochDateContainerBean(new Date());
		_doTest(epochDate,
			    TestEpochDateContainerBean.class, 
				new MarhallTestCheck<TestEpochDateContainerBean>() {
						@Override
						public void check(final TestEpochDateContainerBean original,final TestEpochDateContainerBean readed) {
							Assert.assertEquals(Dates.formatAsEpochTimeStamp(original.getDate()),Dates.formatAsEpochTimeStamp(readed.getDate()));
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ISO8601 
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="iso8601Date")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestISODateContainerBean {
		@MarshallField(as="dateAsISO",
					  dateFormat=@MarshallDateFormat(use=DateFormat.ISO8601),
					  whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private Date _date;
	}
	@Test
	public void testISODateFormat() throws IOException  {
		TestISODateContainerBean isoDate = new TestISODateContainerBean(new Date());
		_doTest(isoDate,
			    TestISODateContainerBean.class, 
				new MarhallTestCheck<TestISODateContainerBean>() {
						@Override
						public void check(final TestISODateContainerBean original,final TestISODateContainerBean readed) {
							Assert.assertEquals(Dates.formatAsISO8601(original.getDate()),Dates.formatAsISO8601(readed.getDate()));
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MILIS TIMESTAMP
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="timeStampDate")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestTimeStampDateContainerBean {
		@MarshallField(as="dateAsTimeStamp",
					  dateFormat=@MarshallDateFormat(use=DateFormat.TIMESTAMP),
					  whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private Date _date;
	}
	@Test
	public void testTimeStampDateFormat() throws IOException  {
		TestTimeStampDateContainerBean timeStampDate = new TestTimeStampDateContainerBean(new Date());
		_doTest(timeStampDate,
			    TestTimeStampDateContainerBean.class, 
				new MarhallTestCheck<TestTimeStampDateContainerBean>() {
						@Override
						public void check(final TestTimeStampDateContainerBean original,final TestTimeStampDateContainerBean readed) {
							Assert.assertEquals(Dates.asMillis(original.getDate()),Dates.asMillis(readed.getDate()));
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="customDate")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestCustomDateContainerBean {
		@MarshallField(as="dateAsCustomFormat",
					  dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,format="yyyy/MM/dd"),
					  whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private Date _date;
	}
	@Test
	public void testCustomDateFormat() throws IOException  {
		TestCustomDateContainerBean customFormatedDate = new TestCustomDateContainerBean(new Date());
		_doTest(customFormatedDate,
			    TestCustomDateContainerBean.class, 
				new MarhallTestCheck<TestCustomDateContainerBean>() {
						@Override
						public void check(final TestCustomDateContainerBean original,final TestCustomDateContainerBean readed) {
							Assert.assertEquals(Dates.format(original.getDate(),"yyyy/MM/dd"),Dates.format(readed.getDate(),"yyyy/MM/dd"));
						}
				});
	}
}
