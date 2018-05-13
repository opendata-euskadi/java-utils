package r01f.filestore.api.teamsite;

import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteArea;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteServer;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteStore;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteWorkArea;
import r01f.types.Path;

public class TeamSiteStorageObjectsPaths {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static class TeamSiteServerPath
			    extends Path {
		private static final long serialVersionUID = -6768110724685815129L;
		public TeamSiteServerPath(final TeamSiteServer server) {
			super(server);
		}
		public TeamSiteServerPath(final Path path) {
			super(path);
		}
		public static TeamSiteServerPath create(final TeamSiteServer server) {
			return new TeamSiteServerPath(server);
		}
		public static TeamSiteServerPath create(final Path path) {
			return new TeamSiteServerPath(path);
		}
	}
	public static class TeamSiteStorePath
			    extends Path {
		private static final long serialVersionUID = -3230990985476460096L;
		public TeamSiteStorePath(final TeamSiteServer server,
								  final TeamSiteStore store) {
			super(server,store);
		}
		public TeamSiteStorePath(final Path path) {
			super(path);
		}
		public static TeamSiteStorePath create(final TeamSiteServer server,
								  			   final TeamSiteStore store) {
			return new TeamSiteStorePath(server,store);
		}
		public static TeamSiteStorePath create(final Path path) {
			return new TeamSiteStorePath(path);
		}
	}
	public static class TeamSiteAreaPath
			    extends Path {
		private static final long serialVersionUID = -2427737285637822270L;
		public TeamSiteAreaPath(final TeamSiteServer server,
								final TeamSiteStore store,
								final TeamSiteArea area) {
			super(server,store,area);
		}
		public TeamSiteAreaPath(final Path path) {
			super(path);
		}
		public static TeamSiteAreaPath create(final TeamSiteServer server,
								  			  final TeamSiteStore store,
											  final TeamSiteArea area) {
			return new TeamSiteAreaPath(server,store,area);
		}
		public static TeamSiteAreaPath create(final Path path) {
			return new TeamSiteAreaPath(path);
		}
	}
	public static class TeamSiteWorkAreaPath
			    extends Path {
		private static final long serialVersionUID = -2285861419563286836L;
		public TeamSiteWorkAreaPath(final TeamSiteServer server,
								  	final TeamSiteStore store,
								    final TeamSiteArea area,
								    final TeamSiteWorkArea workArea) {
			super(server,store,"main",area,"WORKAREA",workArea);
		}
		public TeamSiteWorkAreaPath(final Path path) {
			super(path);
		}
		public static TeamSiteWorkAreaPath create(final TeamSiteServer server,
								  			      final TeamSiteStore store,
								    			  final TeamSiteArea area,
								    			  final TeamSiteWorkArea workArea) {
			return new TeamSiteWorkAreaPath(server,store,area,workArea);
		}
		public static TeamSiteWorkAreaPath create(final Path path) {
			return new TeamSiteWorkAreaPath(path);
		}
	}
	public static class TeamSiteAreaStagingPath
			    extends Path {
		private static final long serialVersionUID = -5805226093595748054L;
		public TeamSiteAreaStagingPath(final TeamSiteServer server,
								  	   final TeamSiteStore store,
									   final TeamSiteArea area) {
			super(server,store,area);
		}
		public TeamSiteAreaStagingPath(final Path path) {
			super(path);
		}
		public static TeamSiteAreaStagingPath create(final TeamSiteServer server,
								  			   		 final TeamSiteStore store,
											  		 final TeamSiteArea area) {
			return new TeamSiteAreaStagingPath(server,store,area);
		}
		public static TeamSiteAreaStagingPath create(final Path path) {
			return new TeamSiteAreaStagingPath(path);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static class TeamSiteFullPath
			    extends Path {
		private static final long serialVersionUID = -7922288263192480199L;
		public TeamSiteFullPath(final Path path) {
			super(path);
		}
		public static TeamSiteFullPath create(final Path path) {
			return new TeamSiteFullPath(path);
		}
	}
	public static class TeamSiteAreaRelativePath
			    extends Path {
		private static final long serialVersionUID = -6768110724685815129L;
		public TeamSiteAreaRelativePath(final Path path) {
			super(path);
		}
		public static TeamSiteAreaRelativePath create(final Path path) {
			return new TeamSiteAreaRelativePath(path);
		}
	}	
	public static class TeamSiteWorkAreaRelativePath
			    extends Path {
		private static final long serialVersionUID = -7081177277030530393L;
		public TeamSiteWorkAreaRelativePath(final Path path) {
			super(path);
		}
		public static TeamSiteWorkAreaRelativePath create(final Path path) {
			return new TeamSiteWorkAreaRelativePath(path);
		}
	}
}
