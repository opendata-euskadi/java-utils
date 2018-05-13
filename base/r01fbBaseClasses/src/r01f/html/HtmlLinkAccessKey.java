package r01f.html;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;



@MarshallType(as="accessKey")
@Immutable
@Accessors(prefix="_")
public class HtmlLinkAccessKey 
  implements CanBeRepresentedAsString,
  			 Serializable {
	private static final long serialVersionUID = -3951876797922937680L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="accessKey",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _accessKey;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlLinkAccessKey() {
		// default no-args constructor
	}
	public HtmlLinkAccessKey(final String accessKey) {
		_accessKey = accessKey;
	}
	public HtmlLinkAccessKey(final HtmlLinkAccessKey other) {
		_accessKey = other.getAccessKey();
	}
	public static HtmlLinkAccessKey of(final String accessKey) {
		return Strings.isNOTNullOrEmpty(accessKey) ? new HtmlLinkAccessKey(accessKey)
											  	   : null;
	}
	public static HtmlLinkAccessKey valueOf(final String accessKey) {
		return HtmlLinkAccessKey.of(accessKey);
	}
	public static HtmlLinkAccessKey create(final String accessKey) {
		return HtmlLinkAccessKey.of(accessKey);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return this.asString();
	}
	@Override
	public String asString() {
		return _accessKey != null ? _accessKey.toString() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (o instanceof HtmlLinkAccessKey) {
			HtmlLinkAccessKey a = (HtmlLinkAccessKey)o;
			return a.getAccessKey().equals(_accessKey);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return _accessKey.hashCode();
	}
	
}
