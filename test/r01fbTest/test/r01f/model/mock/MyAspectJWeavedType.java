package r01f.model.mock;

import java.util.Collection;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.model.mock.MyOIDs.MyTestOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.contact.ContactSocialNetwork;
import r01f.types.contact.ContactWeb;
import r01f.types.geo.GeoPosition;
import r01f.types.url.Url;

@ConvertToDirtyStateTrackable
@MarshallType(as="myAspectJWeavedType") 
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class MyAspectJWeavedType {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MyTestOID _oid;
	
	@MarshallField(as="strCol",
				   whenXml=@MarshallFieldAsXml(collectionElementName="colEl"),
				   escape=true)	
	@Getter @Setter private Collection<String> _stringCol;
	
	@MarshallField(as="langTexts")
	@Getter @Setter private LanguageTexts _langTexts;
	
	@MarshallField(as="dependent")
	@Getter @Setter private MyAspectJWeavedDependentType _dependent;
	
	@MarshallField(as="phoneChannels")
	@Getter @Setter private Collection<ContactPhone> _phones;
	
	@MarshallField(as="socialNetworkChannels")
	@Getter @Setter private Collection<ContactSocialNetwork> _socialNetworks;

	@MarshallField(as="emailChannels")
	@Getter @Setter private Collection<ContactMail> _mailAddresses;

	@MarshallField(as="webSiteChannels")
	@Getter @Setter private Collection<ContactWeb> _webSites;
	
	@MarshallField(as="geoPosition")
	@Getter @Setter private GeoPosition _geoPosition;
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	public static MyAspectJWeavedType createMock() {
		MyAspectJWeavedType out = new MyAspectJWeavedType(MyTestOID.forId("oid"),
														  Lists.newArrayList("el1","el2"),
														  new LanguageTextsMapBacked()
														  		.add(Language.ENGLISH,"test")
														  		.add(Language.SPANISH,"prueba"),
														  // dependent
														  MyAspectJWeavedDependentType.createMock(),
														  // phones
														  Lists.newArrayList(ContactPhone.createToBeUsedFor(ContactInfoUsage.OTHER)
																  						 .withNumber("5555")),
														  // social networks
														  Lists.newArrayList(ContactSocialNetwork.createToBeUsedFor(ContactInfoUsage.OTHER)
																  						 .profileAt(Url.from("http://www.socialnet.com"))),
														  // emails
														  Lists.newArrayList(ContactMail.createToBeUsedFor(ContactInfoUsage.OTHER)
																  						 .mailTo("user@company.com")),	
														  // emails
														  Lists.newArrayList(ContactWeb.createToBeUsedFor(ContactInfoUsage.OTHER)
																  						 .url(Url.from("www.company.com"))),
														  // geo position
														  null
														  );
		return out;
	}
}
