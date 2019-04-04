package r01f.model.security.oids;

import java.util.UUID;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.guids.PersistableObjectOID;
import r01f.objectstreamer.annotations.MarshallType;

public abstract class SecurityCommonOIDs {

/////////////////////////////////////////////////////////////////////////////////////////
// Base marker interface
/////////////////////////////////////////////////////////////////////////////////////////

	public static interface UserModelOID
					extends PersistableObjectOID, OIDTyped<String> {
		// just a marker interface
	}

/////////////////////////////////////////////////////////////////////////////////////////
// Security marker interfaces
/////////////////////////////////////////////////////////////////////////////////////////

	public static interface UserDataModelOID
					extends UserModelOID {
		// just a marker interface
	}

	public static interface UserLoginDataModelOID
					extends UserModelOID {
		// just a marker interface
	}

	public static interface UserLoginEntryModelOID
					extends UserModelOID {
		// just a marker interface
	}

	public static interface UserAuthConfigModelOID
					extends UserModelOID {
		// just a marker interface
	}

	public static interface UserAuthProfileModelOID
					extends UserModelOID {
		// just a marker interface
	}

	public static interface UserOperationModelOID
					extends UserModelOID {
		// just a marker interface
	}

/////////////////////////////////////////////////////////////////////////////////////////
// OID base
/////////////////////////////////////////////////////////////////////////////////////////

	/**
	* Base for every User oid objects
	*/
	@Immutable
	@NoArgsConstructor
	public static abstract class UserModelOIDBase
						 extends OIDBaseMutable<String>
					  implements UserModelOID {

		private static final long serialVersionUID = 7778880230670929915L;

		public UserModelOIDBase(final String id) {
			super(id);
		}

		/**
		* Generates an oid
		* @return
		*/
		protected static String supplyId() {
			UUID uuid = UUID.randomUUID();
			return uuid.toString();
		}

		public String convertToDatabaseColumn(UserModelOID attribute) {
			return attribute.asString();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
// Security Base OID's
/////////////////////////////////////////////////////////////////////////////////////////

	@Immutable
	@MarshallType(as="userDataOID")
	public static class UserDataOID
				extends UserModelOIDBase
			 implements UserDataModelOID {

		private static final long serialVersionUID = -7020929167799107328L;

		public static final UserDataOID ALL = UserDataOID.forId("*");
		public UserDataOID() {
			super();
		}
		public UserDataOID(final String oid) {
			super(oid);
		}
		public static UserDataOID valueOf(final String s) {
			return UserDataOID.forId(s);
		}
		public static UserDataOID fromString(final String s) {
			return UserDataOID.forId(s);
		}
		public static UserDataOID forId(final String id) {
			return new UserDataOID(id);
		}
		public static UserDataOID supply() {
			return UserDataOID.forId(UserModelOIDBase.supplyId());
		}
	}

	@Immutable
	@MarshallType(as="userLoginDataOID")
	public static class UserLoginDataOID
				extends UserModelOIDBase
			 implements UserLoginDataModelOID {

		private static final long serialVersionUID = -2162289370851262116L;

		public static final UserLoginDataOID ALL = UserLoginDataOID.forId("*");
		public UserLoginDataOID() {
			super();
		}
		public UserLoginDataOID(final String oid) {
			super(oid);
		}
		public static UserLoginDataOID valueOf(final String s) {
			return UserLoginDataOID.forId(s);
		}
		public static UserLoginDataOID fromString(final String s) {
			return UserLoginDataOID.forId(s);
		}
		public static UserLoginDataOID forId(final String id) {
			return new UserLoginDataOID(id);
		}
		public static UserLoginDataOID supply() {
			return UserLoginDataOID.forId(UserModelOIDBase.supplyId());
		}
	}

	@Immutable
	@MarshallType(as="userLoginEntryOID")
	public static class UserLoginEntryOID
				extends UserModelOIDBase
			 implements UserLoginEntryModelOID {

		private static final long serialVersionUID = -2162289370851262116L;

		public UserLoginEntryOID() {
			super();
		}
		public UserLoginEntryOID(final String oid) {
			super(oid);
		}
		public static UserLoginEntryOID valueOf(final String s) {
			return UserLoginEntryOID.forId(s);
		}
		public static UserLoginEntryOID fromString(final String s) {
			return UserLoginEntryOID.forId(s);
		}
		public static UserLoginEntryOID forId(final String id) {
			return new UserLoginEntryOID(id);
		}
		public static UserLoginEntryOID supply() {
			return UserLoginEntryOID.forId(UserModelOIDBase.supplyId());
		}
	}

	@Immutable
	@MarshallType(as="userAuthConfigOID")
	public static class UserAuthConfigOID
				extends UserModelOIDBase
			 implements UserAuthConfigModelOID {

		private static final long serialVersionUID = 4379814281635161260L;

		public static final UserAuthConfigOID ALL = UserAuthConfigOID.forId("*");
		public UserAuthConfigOID() {
			super();
		}
		public UserAuthConfigOID(final String oid) {
			super(oid);
		}
		public static UserAuthConfigOID valueOf(final String s) {
			return UserAuthConfigOID.forId(s);
		}
		public static UserAuthConfigOID fromString(final String s) {
			return UserAuthConfigOID.forId(s);
		}
		public static UserAuthConfigOID forId(final String id) {
			return new UserAuthConfigOID(id);
		}
		public static UserAuthConfigOID supply() {
			return UserAuthConfigOID.forId(UserModelOIDBase.supplyId());
		}
	}




	@Immutable
	@MarshallType(as="userOperationOID")
	public static class UserOperationOID
				extends UserModelOIDBase
			 implements UserOperationModelOID {

		private static final long serialVersionUID = -7370498413323628783L;

		public UserOperationOID() {
			super();
		}
		public UserOperationOID(final String oid) {
			super(oid);
		}
		public static UserOperationOID valueOf(final String s) {
			return UserOperationOID.forId(s);
		}
		public static UserOperationOID fromString(final String s) {
			return UserOperationOID.forId(s);
		}
		public static UserOperationOID forId(final String id) {
			return new UserOperationOID(id);
		}
		public static UserOperationOID supply() {
			return UserOperationOID.forId(UserModelOIDBase.supplyId());
		}
	}

}
