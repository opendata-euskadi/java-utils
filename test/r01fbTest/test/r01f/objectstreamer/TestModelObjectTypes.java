package r01f.objectstreamer;

import java.io.IOException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.locale.Language;
import r01f.model.metadata.IndexableFieldID;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.BooleanQueryClause.QualifiedQueryClause;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.ContainedInQueryClause;
import r01f.model.search.query.ContainsTextQueryClause;
import r01f.model.search.query.ContainsTextQueryClause.ContainedTextAt;
import r01f.model.search.query.EqualsQueryClause;
import r01f.model.search.query.HasDataQueryClause;
import r01f.model.search.query.RangeQueryClause;
import r01f.securitycontext.SecurityContext;
import r01f.securitycontext.SecurityContextBase;
import r01f.types.Color;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoBuilder;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.contact.ContactSocialNetwork;
import r01f.types.contact.ContactWeb;
import r01f.types.url.Url;
import r01f.util.types.Dates;

@Slf4j
public class TestModelObjectTypes 
	 extends TestObjectStreamerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY CONTEXT
/////////////////////////////////////////////////////////////////////////////////////////
	public static class MockSecurityContext
                extends SecurityContextBase {
		private static final long serialVersionUID = -2069938190848012416L;
	}
	private SecurityContext _buildSecurityContext() {
		return new MockSecurityContext();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CRUDResult 
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testCRUDResult() throws IOException  {
		Color color = Color.from("blue");
		CRUDResult<Color> crudResult = CRUDResultBuilder.using(_buildSecurityContext())
														.on(Color.class)
														.loaded()
														.entity(color);
		_doTest(crudResult,
			    new TypeToken<CRUDResult<Color>>() { /* nothing */ }, 
				new MarhallTestCheck<CRUDResult<Color>>() {
						@Override
						public void check(final CRUDResult<Color> original,final CRUDResult<Color> readed) {
							Assert.assertEquals(original.getOrThrow(),readed.getOrThrow());
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FindResult 
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testFindResult() throws IOException  {
		Color color1 = Color.from("blue");
		Color color2 = Color.from("red");
		FindResult<Color> findResult = FindResultBuilder.using(_buildSecurityContext())
														.on(Color.class)
														.foundEntities(Lists.newArrayList(color1,color2));
		_doTest(findResult,
			    new TypeToken<FindResult<Color>>() { /* nothing */ }, 
				new MarhallTestCheck<FindResult<Color>>() {
						@Override
						public void check(final FindResult<Color> original,final FindResult<Color> readed) {
							Assert.assertEquals(original.getOrThrow(),readed.getOrThrow());
						}
				});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONTACT INFO
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testContactInfo() {
		Marshaller marshaller = MarshallerBuilder.findTypesToMarshallAt(AppCode.forId("r01f"))
											     .build();
		
		ContactInfo contactInfo = ContactInfoBuilder.create()
													.visible()
													.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.OTHER)
																		  .withNumber("55555555"))
													.addMail(ContactMail.createToBeUsedFor(ContactInfoUsage.OTHER)
																		.mailTo("user@company.com"))
													.addWeb(ContactWeb.createToBeUsedFor(ContactInfoUsage.OTHER)
																	  .url("www.company.com"))
													.addSocialNetwork(ContactSocialNetwork.createToBeUsedFor(ContactInfoUsage.OTHER)
																						  .profileAt(Url.from("www.socialnet.com/profile")))
													.noSocialNetwork()
													.build();
		String xml = marshaller.forWriting().toXml(contactInfo);
		log.info("[Contact to XML:\n{}",xml);
		
		ContactInfo contactInfoFromXml = marshaller.forReading().fromXml(xml,
																		 ContactInfo.class);
		Assert.assertEquals(contactInfoFromXml.getPhones().size(),contactInfo.getPhones().size());
		Assert.assertEquals(contactInfoFromXml.getMailAddreses().size(),contactInfo.getMailAddreses().size());
		Assert.assertEquals(contactInfoFromXml.getWebSites().size(),contactInfo.getWebSites().size());
		Assert.assertEquals(contactInfoFromXml.getSocialNetworks().size(),contactInfo.getSocialNetworks().size());
		String xmlBack = marshaller.forWriting().toXml(contactInfo);
		log.info("[Contact readed from XML back to XML:\n{}",xml);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	QUERY CLAUSES
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testEqualsQueryClause() throws IOException {
		// string
		EqualsQueryClause<String> clause = _buildEqualsQueryClause();
		_doTest(clause,
			    new TypeToken<EqualsQueryClause<String>>() { /* nothing */ }, 
				_buildObjEqualsChecker(new TypeToken<EqualsQueryClause<String>>() { /* nothing */ }));
		// appcode
		EqualsQueryClause<AppCode> clause2 = EqualsQueryClause.forField(IndexableFieldID.forId("theValue"))
															  .of(AppCode.forId("r01fb"));
		_doTest(clause2,
			    new TypeToken<EqualsQueryClause<AppCode>>() { /* nothing */ }, 
				_buildObjEqualsChecker(new TypeToken<EqualsQueryClause<AppCode>>() { /* nothing */ }));
	}
	@Test
	public void testHasDataQueryClause() throws IOException {
		HasDataQueryClause clause = _buildHasDataQueryClause();
		_doTest(clause,
			    HasDataQueryClause.class,
				_buildObjEqualsChecker(HasDataQueryClause.class));
	}
	@Test
	public void testContainsTextQueryClause() throws IOException {
		ContainsTextQueryClause clause = _buildContainsTextQueryClause();
		_doTest(clause,
			    ContainsTextQueryClause.class, 
				_buildObjEqualsChecker(ContainsTextQueryClause.class));
	}
	@Test
	public void testContainedInQueryClause() throws IOException {
		ContainedInQueryClause<Integer> clause = _buildContainedInQueryClause();
		_doTest(clause,
			    new TypeToken<ContainedInQueryClause<Integer>>() { /* nothing */ }, 
				_buildObjEqualsChecker(new TypeToken<ContainedInQueryClause<Integer>>() { /* nothing */ }));
	}
	@Test
	public void testRangeQueryClause() throws IOException {
		RangeQueryClause<Date> clause = _buildRangeQueryClause();
		_doTest(clause,
			    new TypeToken<RangeQueryClause<Date>>() { /* nothing */ },
				_buildObjEqualsChecker(new TypeToken<RangeQueryClause<Date>>() { /* nothing */ }));
	}
	@Test
	public void testBooleanQueryClause() throws IOException {
		BooleanQueryClause clause = _buildBooleanQueryClause();
		_doTest(clause,
			    BooleanQueryClause.class,
				_buildObjEqualsChecker(BooleanQueryClause.class));
	}
	@Test
	public void testQualifiedQueryClause() throws IOException {
		QualifiedQueryClause<HasDataQueryClause> clause = _buildQualifiedQueryClause();
		_doTest(clause,
			    new TypeToken<QualifiedQueryClause<HasDataQueryClause>>() { /* empty */ },
				_buildObjEqualsChecker(new TypeToken<QualifiedQueryClause<HasDataQueryClause>>() { /* empty */ }));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	QUERY CLAUSES BUILDING
/////////////////////////////////////////////////////////////////////////////////////////
	private EqualsQueryClause<String> _buildEqualsQueryClause() {
		EqualsQueryClause<String> outClause = EqualsQueryClause.forField(IndexableFieldID.forId("theValue"))
																   .of("aha!!<adf&asp;asf");
		return outClause;
	}
	private HasDataQueryClause _buildHasDataQueryClause() {
		HasDataQueryClause outClause = HasDataQueryClause.forField(IndexableFieldID.forId("theValue"));
		return outClause;
	}
	private ContainsTextQueryClause _buildContainsTextQueryClause() {
		ContainsTextQueryClause outClause = ContainsTextQueryClause.forField(IndexableFieldID.forId("theText"))
																   .at(ContainedTextAt.CONTENT)
																   .text("aha!!<adf&asp;asf")
																   .in(Language.ENGLISH);
		return outClause;
	}
	private ContainedInQueryClause<Integer> _buildContainedInQueryClause() {
		ContainedInQueryClause<Integer> outClause = ContainedInQueryClause.<Integer>forField(IndexableFieldID.forId("theText"))
																   .within(new Integer[] {1,2,3});
		return outClause;
	}
	private RangeQueryClause<Date> _buildRangeQueryClause() {
		RangeQueryClause<Date> outClause = RangeQueryClause.forField(IndexableFieldID.forId("theDate"))
														   .closed(Dates.fromFormatedString("1971/03/25","yyyy/MM/dd"),
																   new Date());
		return outClause;
	}
	private BooleanQueryClause _buildBooleanQueryClause() {
		BooleanQueryClause boolQry = BooleanQueryClause.create()
										   			.field(IndexableFieldID.forId("myField")).must().beEqualTo(new Date())
										   			.field(IndexableFieldID.forId("myField2")).should().beInsideLast(5).minutes()
										   			.field(IndexableFieldID.forId("myField3")).mustNOT().beWithin(10D,12D,11D)
													.build();
		return boolQry;
	}
	private QualifiedQueryClause<HasDataQueryClause> _buildQualifiedQueryClause() {
		QualifiedQueryClause<HasDataQueryClause> qClause = new QualifiedQueryClause<HasDataQueryClause>(_buildHasDataQueryClause(),
																									    QueryClauseOccur.MUST);
		return qClause;
	}
}
