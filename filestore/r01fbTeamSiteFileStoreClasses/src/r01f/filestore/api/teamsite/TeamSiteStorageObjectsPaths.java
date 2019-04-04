package r01f.filestore.api.teamsite;

import java.util.Collection;

import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteArea;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteServer;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteStore;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteWorkArea;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.PathFactory;

public class TeamSiteStorageObjectsPaths {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static class TeamSiteServerPath
			    extends Path {
		private static final long serialVersionUID = -6768110724685815129L;
		public TeamSiteServerPath(final Collection<String> els) {
			super(els);
		}
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
		private static PathFactory<TeamSiteServerPath> PATH_FACTORY = new PathFactory<TeamSiteServerPath>() {
																			@Override
																			public TeamSiteServerPath createPathFrom(final Collection<String> elements) {
																				return new TeamSiteServerPath(elements);
																			}
																	 };
		@Override @SuppressWarnings("unchecked")
		public <P extends IsPath> PathFactory<P> getPathFactory() {
			return (PathFactory<P>)TeamSiteServerPath.PATH_FACTORY;
		}
	}
	public static class TeamSiteStorePath
			    extends Path {
		private static final long serialVersionUID = -3230990985476460096L;
		public TeamSiteStorePath(final Collection<String> els) {
			super(els);
		}
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
		private static PathFactory<TeamSiteStorePath> PATH_FACTORY = new PathFactory<TeamSiteStorePath>() {
																			@Override
																			public TeamSiteStorePath createPathFrom(final Collection<String> elements) {
																				return new TeamSiteStorePath(elements);
																			}
																	 };
		@Override @SuppressWarnings("unchecked")
		public <P extends IsPath> PathFactory<P> getPathFactory() {
			return (PathFactory<P>)TeamSiteStorePath.PATH_FACTORY;
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
		public TeamSiteAreaPath(final Collection<String> els) {
			super(els);
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
		private static PathFactory<TeamSiteAreaPath> PATH_FACTORY = new PathFactory<TeamSiteAreaPath>() {
																			@Override
																			public TeamSiteAreaPath createPathFrom(final Collection<String> elements) {
																				return new TeamSiteAreaPath(elements);
																			}
																   };
		@Override @SuppressWarnings("unchecked")
		public <P extends IsPath> PathFactory<P> getPathFactory() {
			return (PathFactory<P>)TeamSiteAreaPath.PATH_FACTORY;
		}
	}
	public static class TeamSiteWorkAreaPath
			    extends Path {
		private static final long serialVersionUID = -2285861419563286836L;
		public TeamSiteWorkAreaPath(final Collection<String> els) {
			super(els);
		}
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
		private static PathFactory<TeamSiteWorkAreaPath> PATH_FACTORY = new PathFactory<TeamSiteWorkAreaPath>() {
																				@Override
																				public TeamSiteWorkAreaPath createPathFrom(final Collection<String> elements) {
																					return new TeamSiteWorkAreaPath(elements);
																				}
																		 };
		@Override @SuppressWarnings("unchecked")
		public <P extends IsPath> PathFactory<P> getPathFactory() {
			return (PathFactory<P>)TeamSiteWorkAreaPath.PATH_FACTORY;
		}
	}
	public static class TeamSiteAreaStagingPath
			    extends Path {
		private static final long serialVersionUID = -5805226093595748054L;
		public TeamSiteAreaStagingPath(final Collection<String> els) {
			super(els);
		}
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
		private static PathFactory<TeamSiteAreaStagingPath> PATH_FACTORY = new PathFactory<TeamSiteAreaStagingPath>() {
																					@Override
																					public TeamSiteAreaStagingPath createPathFrom(final Collection<String> elements) {
																						return new TeamSiteAreaStagingPath(elements);
																					}
																		   };
		@Override @SuppressWarnings("unchecked")
		public <P extends IsPath> PathFactory<P> getPathFactory() {
			return (PathFactory<P>)TeamSiteAreaStagingPath.PATH_FACTORY;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static class TeamSiteFullPath
			    extends Path {
		private static final long serialVersionUID = -7922288263192480199L;
		public TeamSiteFullPath(final Collection<String> els) {
			super(els);
		}
		public TeamSiteFullPath(final Path path) {
			super(path);
		}
		public static TeamSiteFullPath create(final Path path) {
			return new TeamSiteFullPath(path);
		}
		private static PathFactory<TeamSiteFullPath> PATH_FACTORY = new PathFactory<TeamSiteFullPath>() {
																			@Override
																			public TeamSiteFullPath createPathFrom(final Collection<String> elements) {
																				return new TeamSiteFullPath(elements);
																			}
																	};
		@Override @SuppressWarnings("unchecked")
		public <P extends IsPath> PathFactory<P> getPathFactory() {
			return (PathFactory<P>)TeamSiteFullPath.PATH_FACTORY;
		}
	}
	public static class TeamSiteAreaRelativePath
			    extends Path {
		private static final long serialVersionUID = -6768110724685815129L;
		public TeamSiteAreaRelativePath(final Collection<String> els) {
			super(els);	
		}
		public TeamSiteAreaRelativePath(final Path path) {
			super(path);
		}
		public static TeamSiteAreaRelativePath create(final Path path) {
			return new TeamSiteAreaRelativePath(path);
		}
		private static PathFactory<TeamSiteAreaRelativePath> PATH_FACTORY = new PathFactory<TeamSiteAreaRelativePath>() {
																					@Override
																					public TeamSiteAreaRelativePath createPathFrom(final Collection<String> elements) {
																						return new TeamSiteAreaRelativePath(elements);
																					}
																		   };
		@Override @SuppressWarnings("unchecked")
		public <P extends IsPath> PathFactory<P> getPathFactory() {
			return (PathFactory<P>)TeamSiteAreaRelativePath.PATH_FACTORY;
		}
	}	
	public static class TeamSiteWorkAreaRelativePath
			    extends Path {
		private static final long serialVersionUID = -7081177277030530393L;
		public TeamSiteWorkAreaRelativePath(final Collection<String> els) {
			super(els);
		}
		public TeamSiteWorkAreaRelativePath(final Path path) {
			super(path);
		}
		public static TeamSiteWorkAreaRelativePath create(final Path path) {
			return new TeamSiteWorkAreaRelativePath(path);
		}
		private static PathFactory<TeamSiteWorkAreaRelativePath> PATH_FACTORY = new PathFactory<TeamSiteWorkAreaRelativePath>() {
																						@Override
																						public TeamSiteWorkAreaRelativePath createPathFrom(final Collection<String> elements) {
																							return new TeamSiteWorkAreaRelativePath(elements);
																						}
																				 };
		@Override @SuppressWarnings("unchecked")
		public <P extends IsPath> PathFactory<P> getPathFactory() {
			return (PathFactory<P>)TeamSiteWorkAreaRelativePath.PATH_FACTORY;
		}
	}
}
