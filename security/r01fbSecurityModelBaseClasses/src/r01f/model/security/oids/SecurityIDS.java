package r01f.model.security.oids;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

public abstract class SecurityIDS {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface UserModelID
					extends OIDTyped<String> {
		// just a marker interface
	}

	/**
	 * Base for every oid objects
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class UserModelIDBase
						 extends OIDBaseMutable<String>
					  implements UserModelID {

		private static final long serialVersionUID = 4162366466990455545L;

		public UserModelIDBase(final String id) {
			super(id);
		}
	}


	/**
	 * UserID
	 */
	@Immutable
	@MarshallType(as="userID")
	@NoArgsConstructor
	public static class UserID
				extends UserModelIDBase {

		private static final long serialVersionUID = -9115600118533916472L;

		public UserID(final String oid) {
			super(oid);
		}
		public static UserID forId(final String id) {
			return new UserID(id);
		}
	}


/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * User Auth Profile ID
	 */
	@Immutable
	@MarshallType(as="userAuthProfileID")
	@NoArgsConstructor
	public static class UserAuthProfileID
				extends UserModelIDBase {

		private static final long serialVersionUID = 1903568727183390224L;

		public UserAuthProfileID(final String oid) {
			super(oid);
		}
		public static UserAuthProfileID forId(final String id) {
			return new UserAuthProfileID(id);
		}
	}

	@Immutable
	@MarshallType(as="userAuthTargetID")
	@NoArgsConstructor
	public static class UserAuthTargetID
				extends UserModelIDBase {

		private static final long serialVersionUID = -2451304008463652481L;

		public UserAuthTargetID(final String oid) {
			super(oid);
		}
		public static UserAuthTargetID forId(final String id) {
			return new UserAuthTargetID(id);
		}
	}

	@Immutable
	@MarshallType(as="userAuthFunctionID")
	@NoArgsConstructor
	public static class UserAuthFunctionID
				extends UserModelIDBase {

		private static final long serialVersionUID = 7582794478598394265L;

		public UserAuthFunctionID(final String oid) {
			super(oid);
		}
		public static UserAuthFunctionID forId(final String id) {
			return new UserAuthFunctionID(id);
		}
	}

	@Immutable
	@MarshallType(as="userAuthTargetAttributeID")
	@NoArgsConstructor
	public static class UserAuthTargetAttributeID
				extends UserModelIDBase {

		private static final long serialVersionUID = -4481663988059402571L;

		public UserAuthTargetAttributeID(final String oid) {
			super(oid);
		}
		public static UserAuthTargetAttributeID forId(final String id) {
			return new UserAuthTargetAttributeID(id);
		}
	}

	@Immutable
	@MarshallType(as="operationResorceID")
	@NoArgsConstructor
	public static class UserOperationResourceID
				extends UserModelIDBase {

		private static final long serialVersionUID = 2926835817661913873L;

		public UserOperationResourceID(final String oid) {
			super(oid);
		}
		public static UserOperationResourceID forId(final String id) {
			return new UserOperationResourceID(id);
		}
	}


}
