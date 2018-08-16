package r01f.model.dokusi;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class DOKUSIOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="dokusiDocumentId")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class DOKUSIDocumentID
	     		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -8145305261344081383L;

		public DOKUSIDocumentID(final String oid) {
			super(oid);
		}
		public static DOKUSIDocumentID forId(final String id) {
			return new DOKUSIDocumentID(id);
		}
		public static DOKUSIDocumentID valueOf(final String id) {
			return DOKUSIDocumentID.forId(id);
		}
	}
	@Immutable
	@MarshallType(as="dokusiAuditID")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class DOKUSIAuditID
	     		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = 1L;
		
		public DOKUSIAuditID(final String oid) {
			super(oid);
		}
		public static DOKUSIAuditID forId(final String id) {
			return new DOKUSIAuditID(id);
		}
		public static DOKUSIAuditID valueOf(final String id) {
			return DOKUSIAuditID.forId(id);
		}
	}
}
