package r01f.filestore.api.teamsite;

import r01f.guids.OIDBaseImmutable;

public class TeamSiteStorageObjects {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static class TeamSiteServer
			    extends OIDBaseImmutable<String> {
		private static final long serialVersionUID = -5176084377576589826L;
		public TeamSiteServer(final String oid) {
			super(oid);
		}
		public static TeamSiteServer forId(final String id) {
			return new TeamSiteServer(id);
		}
		public static final TeamSiteServer IWMNT = TeamSiteServer.forId("iwmnt");
	}
	public static class TeamSiteStore
			    extends OIDBaseImmutable<String> {
		private static final long serialVersionUID = 6727018376027001253L;
		
		public TeamSiteStore(final String oid) {
			super(oid);
		}
		public static TeamSiteStore forId(final String id) {
			return new TeamSiteStore(id);
		}
	}
	public static class TeamSiteArea
			    extends OIDBaseImmutable<String> {
		private static final long serialVersionUID = -4445824095050484678L;
		public TeamSiteArea(final String oid) {
			super(oid);
		}
		public static TeamSiteArea forId(final String id) {
			return new TeamSiteArea(id);
		}
	}
	public static class TeamSiteWorkArea
			    extends OIDBaseImmutable<String> {
		private static final long serialVersionUID = -5382136871225890828L;
		public TeamSiteWorkArea(final String oid) {
			super(oid);
		}
		public static TeamSiteWorkArea forId(final String id) {
			return new TeamSiteWorkArea(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
}
