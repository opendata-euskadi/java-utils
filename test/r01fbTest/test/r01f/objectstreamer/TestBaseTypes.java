package r01f.objectstreamer;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.file.FileName;
import r01f.file.FileNameAndExtension;
import r01f.file.FilePermission;
import r01f.guids.AppAndComponent;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserAndPassword;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.OID;
import r01f.html.MediaQuery;
import r01f.html.MediaQuery.MediaQueryDevice;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.mime.MimeType;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.AppVersion;
import r01f.types.Color;
import r01f.types.Path;
import r01f.types.Range;
import r01f.types.TagList;
import r01f.types.TimeLapse;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoBuilder;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactSocialNetwork;
import r01f.types.contact.ContactSocialNetworkType;
import r01f.types.contact.ContactWeb;
import r01f.types.contact.EMail;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.Person;
import r01f.types.contact.PersonBuilder;
import r01f.types.contact.PersonID;
import r01f.types.contact.PersonSalutation;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.DayOfWeek;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.SecondOfMinute;
import r01f.types.datetime.Time;
import r01f.types.datetime.Year;
import r01f.types.summary.Summary;
import r01f.types.summary.SummaryLanguageTextsBacked;
import r01f.types.summary.SummaryStringBacked;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryStringParam;

@Slf4j
public class TestBaseTypes 
	 extends TestObjectStreamerBase {	
/////////////////////////////////////////////////////////////////////////////////////////
// 	Taglist
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testTagListContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestTagListContainerBean {
		@MarshallField(as="theTagList")
		@Getter @Setter private TagList _tagList;
	}
	@Test
	public void testTagList() throws IOException  {
		TagList tagList1 = new TagList("tag1","tag2");
		TagList tagList2 = new TagList("tagA","tagB");	
		
		// alone
		_doTest(tagList1,
			    TagList.class, 
				_buildObjEqualsChecker(TagList.class));
		
		// contained
		TestTagListContainerBean tagListContainer = new TestTagListContainerBean(tagList1);
		_doTest(tagListContainer,
			    TestTagListContainerBean.class, 
				new MarhallTestCheck<TestTagListContainerBean>() {
						@Override
						public void check(final TestTagListContainerBean original,final TestTagListContainerBean readed) {
							Assert.assertEquals(original.getTagList(),readed.getTagList());
						}
				});
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  AppVersion
////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testAppVersionContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestAppVersionContainerBean {
		@MarshallField(as="theAppVersion")
		@Getter @Setter private AppVersion _appVersion;
	}
	@Test
	public void testAppVersion() throws IOException  {
		AppVersion appVersion1 = AppVersion.from("1.0.1");
		AppVersion appVersion2 = AppVersion.from("1.1.2");
		
		// alone
		_doTest(appVersion1,
			    AppVersion.class, 
				_buildObjEqualsChecker(AppVersion.class));
		// collection
		_doTest(Lists.<AppVersion>newArrayList(appVersion1,appVersion2),
				AppVersion.class,
				_buildObjEqualsChecker(AppVersion.class));
		
		// contained
		TestAppVersionContainerBean appVersionContainer = new TestAppVersionContainerBean(appVersion1);
		_doTest(appVersionContainer,
			    TestAppVersionContainerBean.class, 
				new MarhallTestCheck<TestAppVersionContainerBean>() {
						@Override
						public void check(final TestAppVersionContainerBean original,final TestAppVersionContainerBean readed) {
							Assert.assertEquals(original.getAppVersion(),readed.getAppVersion());
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LanguageTexts 
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testLangTextsContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestLangTextsContainerBean {
		@MarshallField(as="theLangTexts")
		@Getter @Setter private LanguageTexts _langTexts;
	}
	@Test
	public void testLanguageTexts() throws IOException  {
		// alone
		LanguageTexts langTexts = new LanguageTextsMapBacked()
											.add(Language.SPANISH,"Hola")
											.add(Language.ENGLISH,"hello");
		// alone
		_doTest(langTexts,
			    LanguageTexts.class, 
				new MarhallTestCheck<LanguageTexts>() {
						@Override
						public void check(final LanguageTexts original,final LanguageTexts readed) {
							Assert.assertEquals(original.getFor(Language.SPANISH),readed.getFor(Language.SPANISH));
							Assert.assertEquals(original.getFor(Language.ENGLISH),readed.getFor(Language.ENGLISH));
						}
				});
		// contained
		TestLangTextsContainerBean langTextsContainer = new TestLangTextsContainerBean(langTexts);
		_doTest(langTextsContainer,
			    TestLangTextsContainerBean.class, 
				new MarhallTestCheck<TestLangTextsContainerBean>() {
						@Override
						public void check(final TestLangTextsContainerBean original,final TestLangTextsContainerBean readed) {
							Assert.assertEquals(original.getLangTexts().getFor(Language.SPANISH),readed.getLangTexts().getFor(Language.SPANISH));
							Assert.assertEquals(original.getLangTexts().getFor(Language.ENGLISH),readed.getLangTexts().getFor(Language.ENGLISH));
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SUMMARIES
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testSummariesContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestSummariesContainerBean {
		@MarshallField(as="theSummary")
		@Getter @Setter private Summary _summary;
	}
	@Test 
	public void testSummaryStringBacked() throws IOException {
		SummaryStringBacked summ = new SummaryStringBacked("__summary text__");
		// alone
		_doTest(summ,
			    SummaryStringBacked.class, 
				_buildObjEqualsChecker(SummaryStringBacked.class));
		// contained
		TestSummariesContainerBean summaryContainer = new TestSummariesContainerBean(summ);
		_doTest(summaryContainer,
			    TestSummariesContainerBean.class, 
				new MarhallTestCheck<TestSummariesContainerBean>() {
						@Override
						public void check(final TestSummariesContainerBean original,final TestSummariesContainerBean readed) {
							Assert.assertEquals(original.getSummary(),readed.getSummary());
						}
				});
	}
	@Test 
	public void testSummaryLanguageTextsBacked() throws IOException {
		SummaryLanguageTextsBacked summ = new SummaryLanguageTextsBacked(new LanguageTextsMapBacked()
																				.add(Language.SPANISH,"Hola")
																				.add(Language.ENGLISH,"hello"));
		// alone
		_doTest(summ,
			    SummaryLanguageTextsBacked.class, 
				_buildObjEqualsChecker(SummaryLanguageTextsBacked.class));
		// contained
		TestSummariesContainerBean summaryContainer = new TestSummariesContainerBean(summ);
		_doTest(summaryContainer,
			    TestSummariesContainerBean.class, 
				new MarhallTestCheck<TestSummariesContainerBean>() {
						@Override
						public void check(final TestSummariesContainerBean original,final TestSummariesContainerBean readed) {
							Assert.assertEquals(original.getSummary(),readed.getSummary());
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OIDs
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testOidContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestGenericOIDContainerBean<O extends OID> {
		@MarshallField(as="theOid")
		@Getter @Setter private O _oid;
	}
	@Test
	public void testOids() throws IOException {
		// app code
		AppCode appCode = AppCode.forId("r01f");
		_testOID(appCode,
				 AppCode.class);
		// app component
		AppComponent appComponent = AppComponent.forId("r01f");
		_testOID(appComponent,
				 AppComponent.class);		
		// app and component
		AppAndComponent appAndComponent = AppAndComponent.composedBy(AppCode.forId("r01f"),AppComponent.forId("test"));
		_testOID(appAndComponent,
				 AppAndComponent.class);
	}
	private <O extends OID> void _testOID(final O oid,
										  final Class<O> oidType) throws IOException  {
		// test the oid object marshalling
		_doTest(oid,
				oidType, 
				_buildObjEqualsChecker(oidType));
		
		// test the oid contained in another object marshalling
		TestGenericOIDContainerBean<O> contained = new TestGenericOIDContainerBean<O>(oid);
		_doTest(contained,
				new TypeToken<TestGenericOIDContainerBean<O>>() { /* nothing */ }, 
				_buildObjChecker(new TypeToken<TestGenericOIDContainerBean<O>>() { /* nothing */ },
								 new Function<TestGenericOIDContainerBean<O>,Object>() {
										@Override
										public Object apply(final TestGenericOIDContainerBean<O> container) {
											return container.getOid();
										}
			  					 }));
	}
	@MarshallType(as="testOidContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestOIDContainerBean {
		@MarshallField(as="theOid")
		@Getter @Setter private AppCode _appCode;
	}
	@Test
	public void testOid() throws IOException {
		AppCode appCode = AppCode.forId("r01f");
		
		// alone
		_doTest(appCode,
				AppCode.class, 
				_buildObjEqualsChecker(AppCode.class));
		// contained
		TestOIDContainerBean contained = new TestOIDContainerBean(appCode);
		_doTest(contained,
				TestOIDContainerBean.class, 
				new MarhallTestCheck<TestOIDContainerBean>() {
						@Override
						public void check(final TestOIDContainerBean original,final TestOIDContainerBean readed) {
							Assert.assertEquals(original.getAppCode(),readed.getAppCode());
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testRangeContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestRangeContainerBean<T extends Comparable<T>> {
		@MarshallField(as="theRange",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private Range<T> _range;
	}
	@Test 
	public void testRange() throws IOException {
		// alone
		Range<Integer> intRange = Range.closed(2,5);
		_doTest(intRange,
				new TypeToken<Range<Integer>>() { /* nothing */ },
				_buildObjEqualsChecker(new TypeToken<Range<Integer>>() { /* nothing */ }));
		// contained
		_doTest(new TestRangeContainerBean<Integer>(intRange),
				new TypeToken<TestRangeContainerBean<Integer>>() { /* nothing */ },
				_builRangeContainerBeanCheck(new TypeToken<TestRangeContainerBean<Integer>>() { /* nothing */ }));
	}
	private static <T extends Comparable<T>> MarhallTestCheck<TestRangeContainerBean<T>> _builRangeContainerBeanCheck(final TypeToken<TestRangeContainerBean<T>> typeRef) {
		return _buildObjChecker(typeRef,
								new Function<TestRangeContainerBean<T>,Object>() {
										@Override
										public Object apply(final TestRangeContainerBean<T> container) {
											return container.getRange();
										}
								});
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	FILE NAME
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testFileName() throws IOException {
		FileName fileName = FileName.of("d:/data/a.txt");
		MarhallTestCheck<FileName> check = _buildObjChecker(FileName.class,
															new Function<FileName,Object>() {
																	@Override
																	public Object apply(final FileName fn) {
																		return fn.getFileName();
																	}
															});  
		_doTest(fileName,
				FileName.class, 
				check);
	}
	@Test
	public void testFileNameAndExtension() throws IOException  {
		FileNameAndExtension fileNameAndExtension = FileNameAndExtension.of("d:/data/a.txt");
		MarhallTestCheck<FileNameAndExtension> check = _buildObjChecker(FileNameAndExtension.class,
																		new Function<FileNameAndExtension,Object>() {
																				@Override
																				public Object apply(final FileNameAndExtension fn) {
																					return fn.getNameWithExtension();
																				}
																		}); 
		_doTest(fileNameAndExtension,
				FileNameAndExtension.class, 
				check);
	}
	@Test
	public void testFilePermissions() throws IOException  {
		FilePermission filePermissions = FilePermission.createFromUNIXPermissionString("rw-rw-rwx");
		_doTest(filePermissions,
				FilePermission.class, 
				_buildObjEqualsChecker(FilePermission.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	User & Password
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testUserAndPassword() throws IOException  {
		UserAndPassword usrAndPwd = new UserAndPassword(UserCode.forId("anUser"),Password.forId("mypass"));
		_doTest(usrAndPwd,
				UserAndPassword.class, 
				new MarhallTestCheck<UserAndPassword>() {
						@Override
						public void check(final UserAndPassword original,final UserAndPassword readed) {
							Assert.assertEquals(original.getUser(),readed.getUser());
							Assert.assertEquals(original.getPassword(),readed.getPassword());
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Url
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testUrlContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestUrlContainerBean {
		@MarshallField(as="theUrl")
		@Getter @Setter private Url _url;
	}
	@Test
	public void testUrl() throws IOException  {
		Url url = Url.from("http://www.google.com");
		
		// alone
		_doTest(url,
			    Url.class, 
				_buildObjEqualsChecker(Url.class));
		// contained
		TestUrlContainerBean urlContainer = new TestUrlContainerBean(url);
		_doTest(urlContainer,
			    TestUrlContainerBean.class, 
				new MarhallTestCheck<TestUrlContainerBean>() {
						@Override
						public void check(final TestUrlContainerBean original,final TestUrlContainerBean readed) {
							Assert.assertEquals(original.getUrl(),readed.getUrl());
						}
				});
	}
	@Test
	public void testUrlQueryStringParam() throws IOException  {
		UrlQueryStringParam urlQueryStringParam = UrlQueryStringParam.of("paramName","paramValue");
		
		// alone
		_doTest(urlQueryStringParam,
			    UrlQueryStringParam.class, 
				_buildObjEqualsChecker(UrlQueryStringParam.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Path
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testPathContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestPathContainerBean {
		@MarshallField(as="thePath")
		@Getter @Setter private Path _path;
	}
	@Test
	public void testPath() throws IOException  {
		Path path = Path.from("d:/a/b/c");
		
		// alone
		_doTest(path,
			    Path.class, 
				_buildObjEqualsChecker(Path.class));
		// contained
		TestPathContainerBean pathContainer = new TestPathContainerBean(path);
		_doTest(pathContainer,
			    TestPathContainerBean.class, 
				new MarhallTestCheck<TestPathContainerBean>() {
						@Override
						public void check(final TestPathContainerBean original,final TestPathContainerBean readed) {
							Assert.assertEquals(original.getPath(),readed.getPath());
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COLOR
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testColorContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestColorContainerBean {
		@MarshallField(as="theColor")
		@Getter @Setter private Color _color;
	}
	@Test
	public void testColor() throws IOException  {
		Color color1 = Color.from("red");
		Color color2 = Color.from("blue");
		
		// alone
		_doTest(color1,
			    Color.class, 
				_buildObjEqualsChecker(Color.class));
		// collection
		_doTest(Lists.<Color>newArrayList(color1,color2),
				Color.class,
				_buildObjEqualsChecker(Color.class));
		
		// contained
		TestColorContainerBean colorContainer = new TestColorContainerBean(color1);
		_doTest(colorContainer,
			    TestColorContainerBean.class, 
				new MarhallTestCheck<TestColorContainerBean>() {
						@Override
						public void check(final TestColorContainerBean original,final TestColorContainerBean readed) {
							Assert.assertEquals(original.getColor(),readed.getColor());
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MediaQuery
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testMediaQuery() throws IOException  {
		MediaQuery mediaQuery = MediaQuery.createForDevice(MediaQueryDevice.AURAL)
												.aspectMaxRatio("10")
												.bitsPerColorMax(20);
		_doTest(mediaQuery,
			    MediaQuery.class, 
				new MarhallTestCheck<MediaQuery>() {
						@Override
						public void check(final MediaQuery original,final MediaQuery readed) {
							Assert.assertEquals(original.getDevice(),readed.getDevice());
							Assert.assertEquals(original.getValues().size(),readed.getValues().size());
						}
				});
	}		
/////////////////////////////////////////////////////////////////////////////////////////
//	MIMETYPE
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testMimeType() throws IOException  {
		MimeType mime = new MimeType("text/xml");
		_doTest(mime,
				MimeType.class, 
				_buildObjChecker(MimeType.class,
								 new Function<MimeType,Object>() {
											@Override
											public Object apply(final MimeType mime) {
												return mime.getName();
											}
								}));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE / TIME
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="timeLapseContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestTimeLapseContainerBean {
		@MarshallField(as="theTimeLapse")
		@Getter @Setter private TimeLapse _timeLapse;
	}
	@Test
	public void testTimeLapse() throws IOException {
		TimeLapse timeLapse = TimeLapse.of("1d");
		// alone
		_doTest(timeLapse,
			    TimeLapse.class,
			    _buildObjEqualsChecker(TimeLapse.class));
		
		// contained
		TestTimeLapseContainerBean timeLapseContainer = new TestTimeLapseContainerBean(timeLapse);
		_doTest(timeLapseContainer,
			    TestTimeLapseContainerBean.class, 
				new MarhallTestCheck<TestTimeLapseContainerBean>() {
						@Override
						public void check(final TestTimeLapseContainerBean original,final TestTimeLapseContainerBean readed) {
							Assert.assertEquals(original.getTimeLapse(),readed.getTimeLapse());
						}
				});
	}
	@Test
	public void testDateTimeTypes() throws IOException  {
		// Year
		Year year = new Year(2017);
		_doTest(year,
			    Year.class,
			    _buildObjEqualsChecker(Year.class));
		_doTest(new TestDateTimeContainerBean<Year>(year),
				new TypeToken<TestDateTimeContainerBean<Year>>() {/* nothing */},
				_buildDateTimeContainerBeanCheck(new TypeToken<TestDateTimeContainerBean<Year>>() {/* nothing */}));
		// Month of year
		MonthOfYear monthOfYear = new MonthOfYear(10);
		_doTest(monthOfYear,
				MonthOfYear.class,
				_buildObjEqualsChecker(MonthOfYear.class));
		_doTest(new TestDateTimeContainerBean<MonthOfYear>(monthOfYear),
				new TypeToken<TestDateTimeContainerBean<MonthOfYear>>() {/* nothing */},
				_buildDateTimeContainerBeanCheck(new TypeToken<TestDateTimeContainerBean<MonthOfYear>>() {/* nothing */}));
		// day of months
		DayOfMonth dayOfMonth = new DayOfMonth(1);
		_doTest(dayOfMonth,
				DayOfMonth.class,
				_buildObjEqualsChecker(DayOfMonth.class));
		_doTest(new TestDateTimeContainerBean<DayOfMonth>(dayOfMonth),
				new TypeToken<TestDateTimeContainerBean<DayOfMonth>>() {/* nothing */},
				_buildDateTimeContainerBeanCheck(new TypeToken<TestDateTimeContainerBean<DayOfMonth>>() {/* nothing */}));
		// day of week
		DayOfWeek dayOfWeek = new DayOfWeek(1);
		_doTest(dayOfWeek,
				DayOfWeek.class,
				_buildObjEqualsChecker(DayOfWeek.class));
		_doTest(new TestDateTimeContainerBean<DayOfWeek>(dayOfWeek),
				new TypeToken<TestDateTimeContainerBean<DayOfWeek>>() {/* nothing */},
				_buildDateTimeContainerBeanCheck(new TypeToken<TestDateTimeContainerBean<DayOfWeek>>() {/* nothing */}));
		// time
		Time time = new Time(12,59);
		_doTest(time,
				Time.class,
				_buildObjEqualsChecker(Time.class));
		_doTest(new TestDateTimeContainerBean<Time>(time),
				new TypeToken<TestDateTimeContainerBean<Time>>() {/* nothing */},
				_buildDateTimeContainerBeanCheck(new TypeToken<TestDateTimeContainerBean<Time>>() {/* nothing */}));
		// Hour of day
		HourOfDay hourOfDay = new HourOfDay(12);
		_doTest(hourOfDay,
			    HourOfDay.class,
			    _buildObjEqualsChecker(HourOfDay.class));
		_doTest(new TestDateTimeContainerBean<HourOfDay>(hourOfDay),
				new TypeToken<TestDateTimeContainerBean<HourOfDay>>() {/* nothing */},
				_buildDateTimeContainerBeanCheck(new TypeToken<TestDateTimeContainerBean<HourOfDay>>() {/* nothing */}));
		// Minute of hour
		MinuteOfHour minuteOfHour = new MinuteOfHour(59);
		_doTest(minuteOfHour,
				MinuteOfHour.class,
				_buildObjEqualsChecker(MinuteOfHour.class));
		_doTest(new TestDateTimeContainerBean<MinuteOfHour>(minuteOfHour),
				new TypeToken<TestDateTimeContainerBean<MinuteOfHour>>() {/* nothing */},
				_buildDateTimeContainerBeanCheck(new TypeToken<TestDateTimeContainerBean<MinuteOfHour>>() {/* nothing */}));
		// Second of minute
		SecondOfMinute secondOfMinute = new SecondOfMinute(10);
		_doTest(secondOfMinute,
				SecondOfMinute.class,
				_buildObjEqualsChecker(SecondOfMinute.class));
		_doTest(new TestDateTimeContainerBean<SecondOfMinute>(secondOfMinute),
				new TypeToken<TestDateTimeContainerBean<SecondOfMinute>>() {/* nothing */},
				_buildDateTimeContainerBeanCheck(new TypeToken<TestDateTimeContainerBean<SecondOfMinute>>() {/* nothing */}));
	}
	@MarshallType(as="testDateTimeContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestDateTimeContainerBean<T> {
		@MarshallField(as="theDataTimeField")
		@Getter @Setter private T _dateTime;
	}	
	private static <T> MarhallTestCheck<TestDateTimeContainerBean<T>> _buildDateTimeContainerBeanCheck(final TypeToken<TestDateTimeContainerBean<T>> typeRef) {
		return _buildObjChecker(typeRef,
								new Function<TestDateTimeContainerBean<T>,Object>() {
										@Override
										public Object apply(final TestDateTimeContainerBean<T> container) {
											return (T)container.getDateTime();
										}
								});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PERSON & CONTACT INFO
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="testPersonIdContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestPersonIdContainerBean {
		@MarshallField(as="thePersonId")
		@Getter @Setter private PersonID _personId;
	}
	@Test
	public void testNIF() throws IOException  {
		NIFPersonID personId = NIFPersonID.forId("11111111H");
		
		// alone
		_doTest(personId,
			    NIFPersonID.class, 
			    _buildObjEqualsChecker(NIFPersonID.class));
		// contained
		TestPersonIdContainerBean personIdContainer = new TestPersonIdContainerBean(personId);
		_doTest(personIdContainer,
			    TestPersonIdContainerBean.class, 
				new MarhallTestCheck<TestPersonIdContainerBean>() {
						@Override
						public void check(final TestPersonIdContainerBean original,final TestPersonIdContainerBean readed) {
							Assert.assertEquals(original.getPersonId(),readed.getPersonId());
						}
				});
	}
	
	
	@MarshallType(as="testPersonContainerBean")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class TestPersonContainerBean {
		@MarshallField(as="thePerson")
		@Getter @Setter private Person<? extends PersonID> _person;
	}
	@Test
	public void testPerson() throws IOException  {
		Person<NIFPersonID> person = PersonBuilder.createPersonWithId(NIFPersonID.forId("11111111H"))
													 .withName("John")
													 .withSurname("doe")
													 .useSalutation(PersonSalutation.DR)
													 .preferredLanguage(Language.SPANISH)
													 .withDetails("a test person <>")
													 .build();
		
		// alone
		_doTest(person,
			    new TypeToken<Person<NIFPersonID>>() { /* nothing */ }, 
			    _buildObjEqualsChecker(new TypeToken<Person<NIFPersonID>>() { /* nothing */ }));
		// contained
		TestPersonContainerBean personContainer = new TestPersonContainerBean(person);
		_doTest(personContainer,
			    TestPersonContainerBean.class, 
				new MarhallTestCheck<TestPersonContainerBean>() {
						@Override
						public void check(final TestPersonContainerBean original,final TestPersonContainerBean readed) {
							Assert.assertEquals(original.getPerson().getId(),readed.getPerson().getId());
						}
				});
	}
	@Test
	public void testContactInfo() throws IOException  {
		ContactInfo contact = ContactInfoBuilder.create()
												.visible()
												.addMail(ContactMail.createToBeUsedFor(ContactInfoUsage.COMPANY)
																	.mailTo(EMail.of("my@trashmail.com")))
												.addWeb(ContactWeb.createToBeUsedFor(ContactInfoUsage.PERSONAL)
																  .url(Url.from("http://mysite.com")))
												.addSocialNetwork(ContactSocialNetwork.createToBeUsedFor(ContactInfoUsage.WORK)
																					  .forNetwork(ContactSocialNetworkType.FACEBOOK)
																					  .profileAt(Url.from("http://facebook.com/myprofile"))
																					  .user(UserCode.forId("myfacebookuser")))
												.contactIn(Language.ENGLISH)
												.build();
		
		// alone
		_doTest(contact,
			    ContactInfo.class, 
			    new MarhallTestCheck<ContactInfo>() {
						@Override
						public void check(final ContactInfo original,final ContactInfo readed) {
						}
				});
	}	
}
