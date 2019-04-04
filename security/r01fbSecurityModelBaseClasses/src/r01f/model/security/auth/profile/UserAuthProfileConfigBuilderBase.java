package r01f.model.security.auth.profile;

import java.util.Arrays;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.model.security.auth.profile.functions.ModelForUserAuthFunction;
import r01f.model.security.auth.profile.targets.ModelForUserAuthTarget;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.patterns.IsBuilder;

@NoArgsConstructor
public abstract class UserAuthProfileConfigBuilderBase<AT extends ModelForUserAuthTarget,
													   AF extends ModelForUserAuthFunction,
													   O extends UserAuthProfileModelOID,
													   ID extends UserAuthProfileID,
													   A extends UserAuthProfileConfigBase<O,ID,AT,AF,A>>
		   implements IsBuilder {

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class BuilderStart {
		private final A  _authProfileConfig;

		public P12AuthTargetStep withId(final ID profileID){
			_authProfileConfig.setId(profileID);
			return new P12AuthTargetStep(_authProfileConfig);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class P12AuthTargetStep {
		private final A  _authProfileConfig;

		public FunctionsStep withAuthTarget(final AT authTarget ){
			_authProfileConfig.setTarget(authTarget);
			return new FunctionsStep(_authProfileConfig);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class FunctionsStep {
		private final A  _authConfig;

		public P12BuilderBuildStep withFunctions(final Collection<AF> functions ){
			_authConfig.setFunctions(functions);
			return new P12BuilderBuildStep(_authConfig);
		}
		@SuppressWarnings("unchecked")
		public  P12BuilderBuildStep withFunctions(final AF ... functions ){
			_authConfig.setFunctions( Arrays.asList(functions));
			return new P12BuilderBuildStep(_authConfig);
		}
		public  P12BuilderBuildStep withoutFunctions(){
			return new P12BuilderBuildStep(_authConfig);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
//	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
//	public class P12AuthPermissionsStep {
//		private final A  _authConfig;
//
//		public RolesStep withAuthPermissions(final Collection<AO> authObjects ){
//			_authConfig.setAuthObjects(authObjects);
//			return new RolesStep(_authConfig);
//		}
//		public  RolesStep withAuthPermissions(final AO... authObjectsAsArray ){
//			final Collection<AO> authObjects = Arrays.asList(authObjectsAsArray);
//			_authConfig.setAuthObjects(authObjects);
//			return new RolesStep(_authConfig);
//		}
//	}
//
//	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
//	public class RolesStep {
//		private final A  _authConfig;
//
//		public P12BuilderBuildStep withRoles(final Collection<R> roles ){
//
//			_authConfig.setRoles(roles);
//			return new P12BuilderBuildStep(_authConfig);
//		}
//		public  P12BuilderBuildStep withRoles(final R... roles ){
//			_authConfig.setRoles( Arrays.asList(roles));
//			return new P12BuilderBuildStep(_authConfig);
//		}
//		public  P12BuilderBuildStep withoutRoles(){
//			return new P12BuilderBuildStep(_authConfig);
//		}
//	}


/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class P12BuilderBuildStep {
		private final A _authProfileConfig;

		public P12BuilderBuildStep withOid(final O authProfileOid) {
			_authProfileConfig.setOid(authProfileOid);
			return this;
		}
		public A build() {
			return _authProfileConfig;
		}
	}
}
