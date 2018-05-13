package r01f.objectstreamer;

import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.model.mock.MyAspectJWeavedType;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoBuilder;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.contact.ContactSocialNetwork;
import r01f.types.contact.ContactWeb;
import r01f.types.url.Url;

/**
 * Run:
 * 		JVM argument: -javaagent:d:/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true
 */
@Slf4j
public class TestWithAspects {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testWeavedObject() {
		Marshaller marshaller = MarshallerBuilder.build();
		
		MyAspectJWeavedType obj = MyAspectJWeavedType.createMock();
		
		String xml = marshaller.forWriting().toXml(obj);
		System.out.println(xml);
		
		MyAspectJWeavedType objFromXml = marshaller.forReading().fromXml(xml,
																		 MyAspectJWeavedType.class);
		Assert.assertEquals(objFromXml.getOid(),obj.getOid());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
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
}
