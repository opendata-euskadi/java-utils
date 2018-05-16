package r01f.ejie.xlnets.servlet;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guid.dispenser.GUIDDispenser;
import r01f.guid.dispenser.GUIDDispenserDef;
import r01f.guid.dispenser.SimpleGUIDDispenser;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.guids.SuppliesOID;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Appointments service identifiers definitions.
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class XLNetsOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    private final static GUIDDispenser GUID_DISPENSER = SimpleGUIDDispenser.create(new GUIDDispenserDef(AppCode.forId("r01f")));	// default sequence
/////////////////////////////////////////////////////////////////////////////////////////
//	OIDs
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface XLNetsModelObjectOID
					extends OIDTyped<String>,
							SuppliesOID {
		/* a marker interface */
	}
	@Immutable
	private static abstract class XLNetsModelObjectOIDBase
	              		  extends OIDBaseMutable<String> 	// normally this should extend OIDBaseImmutable BUT it MUST have a default no-args constructor to be serializable
					   implements XLNetsModelObjectOID {
		private static final long serialVersionUID = -1535472178694265985L;
		public XLNetsModelObjectOIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public XLNetsModelObjectOIDBase(final String id) {
			super(id);
		}
		/**
		 * Generates an oid
		 * @return the generated oid
		 */
		protected static String supplyId() {
			return GUID_DISPENSER.generateGUID();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ORGANIZATION / DIVISION / SERVICE 
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="xlnetsOrgOid")
	@NoArgsConstructor
	public static class XLNetsOrganizationOID
				extends XLNetsModelObjectOIDBase {
		private static final long serialVersionUID = 1797728634898350173L;
		public XLNetsOrganizationOID(final String oid) {
			super(oid);
		}
		public static XLNetsOrganizationOID valueOf(final String s) {
			return XLNetsOrganizationOID.forId(s);
		}
		public static XLNetsOrganizationOID fromString(final String s) {
			return XLNetsOrganizationOID.forId(s);
		}
		public static XLNetsOrganizationOID forId(final String id) {
			return new XLNetsOrganizationOID(id);
		}
		public static XLNetsOrganizationOID supply() {
			return XLNetsOrganizationOID.forId(XLNetsModelObjectOIDBase.supplyId());
		}
	}
	@Immutable
	@MarshallType(as="xlnetsOrgDivisionOid")
	@NoArgsConstructor
	public static class XLNetsOrgDivisionOID
				extends XLNetsModelObjectOIDBase {
		private static final long serialVersionUID = -6238420865427110997L;
		public XLNetsOrgDivisionOID(final String oid) {
			super(oid);
		}
		public static XLNetsOrgDivisionOID valueOf(final String s) {
			return XLNetsOrgDivisionOID.forId(s);
		}
		public static XLNetsOrgDivisionOID fromString(final String s) {
			return XLNetsOrgDivisionOID.forId(s);
		}
		public static XLNetsOrgDivisionOID forId(final String id) {
			return new XLNetsOrgDivisionOID(id);
		}
		public static XLNetsOrgDivisionOID supply() {
			return XLNetsOrgDivisionOID.forId(XLNetsModelObjectOIDBase.supplyId());
		}
	}
	@Immutable
	@MarshallType(as="xlnetsOrgDivisionServiceOid")
	@NoArgsConstructor
	public static class XLNetsOrgDivisionServiceOID
				extends XLNetsModelObjectOIDBase {
		private static final long serialVersionUID = 83200432891416102L;
		public XLNetsOrgDivisionServiceOID(final String oid) {
			super(oid);
		}
		public static XLNetsOrgDivisionServiceOID valueOf(final String s) {
			return XLNetsOrgDivisionServiceOID.forId(s);
		}
		public static XLNetsOrgDivisionServiceOID fromString(final String s) {
			return XLNetsOrgDivisionServiceOID.forId(s);
		}
		public static XLNetsOrgDivisionServiceOID forId(final String id) {
			return new XLNetsOrgDivisionServiceOID(id);
		}
		public static XLNetsOrgDivisionServiceOID supply() {
			return XLNetsOrgDivisionServiceOID.forId(XLNetsModelObjectOIDBase.supplyId());
		}
	}
}
