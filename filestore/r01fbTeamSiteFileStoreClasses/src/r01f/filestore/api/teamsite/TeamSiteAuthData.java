package r01f.filestore.api.teamsite;

import java.io.Serializable;

import com.interwoven.cssdk.factory.CSFactory;
import com.interwoven.cssdk.factory.CSSOAPFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.UserRole;

/**
 * Interwoven - Team Site Content Services access info.
 */
@NoArgsConstructor
@AllArgsConstructor
@Accessors(prefix = "_")
@ToString(callSuper = true,
		  includeFieldNames = true,
		  doNotUseGetters = true)
public class TeamSiteAuthData
  implements Debuggable,
  			 Serializable {

	private static final long serialVersionUID = 7726760855286734077L;

///////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * The app code that connects to TeamSite
     */
	@Setter @Getter private String _appCode = "aa88";
    /**
     * The TeamSite's content service url
     */
	@Setter @Getter private String _csServiceUrl = "http://iwdes01.ejgvdns:80";
    /**
     * The content services factory interface type
     */
	@Setter @Getter private Class<? extends CSFactory> _csSDKFactoryInterfaceType = CSFactory.class; 	// CSFactory.class;
	/**
	 * The content services factory imple type
	 */
	@Getter @Setter private Class<? extends CSFactory> _csSDKFactoryImplType = CSSOAPFactory.class; 	//CSSOAPFactory.class;
    /**
     * User identifier
     */
	@Setter @Getter private UserCode _loginUser = UserCode.forId("r01d");
    /**
     * User role
     */
	@Setter @Getter private UserRole _loginUserRole = UserRole.forId("Editor");
    /**
     * User password
     */
	@Setter @Getter private Password _loginUserPassword = Password.forId("r01d");

/////////////////////////////////////////////////////////////////////////////////////////
//	FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	public TeamSiteAuthData login(final UserCode user,
								  final Password password,
								  final UserRole userRole) {
		_loginUser = user;
		_loginUserPassword = password;
		_loginUserRole = userRole;

		return this;
	}
	public TeamSiteAuthData forContentServicesUrl(final String webServicesUrl) {
		_csServiceUrl = webServicesUrl;
		return this;
	}
	public TeamSiteAuthData forApp(final String appCode) {
		_appCode = appCode;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("         appCode: ").append(_appCode).append("\n\t")
		  .append("  cs service url: ").append(_csServiceUrl).append("\n\t")
		  .append("cs factory iface: ").append(_csSDKFactoryInterfaceType).append("\n\t")
		  .append(" cs factory impl: ").append(_csSDKFactoryImplType).append("\n\t")
		  .append("            user: ").append(_loginUser).append("\n\t")
		  .append("        password: ").append("****").append("\n\t")
		  .append("            role: ").append(_loginUserRole).append("\n\t");
		return sb.toString();
	}
}
