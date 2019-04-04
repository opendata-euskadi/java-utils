package r01f.client.servicesproxy.rest.builders.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilderBase;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilderForModelObjectPersistenceBase;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.types.url.UrlPath;

/**
 * Base types for REST resources path building
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class RESTResourcePathBuilderBase {

	public static class P12RESTServiceResourceUrlPathBuilderBase
			    extends RESTServiceResourceUrlPathBuilderBase {
		public P12RESTServiceResourceUrlPathBuilderBase(final RESTServiceEndPointUrl endPointUrl,
														final UrlPath resourceUrlPath) {
			super(endPointUrl,
				  resourceUrlPath);
		}
	}

	public static class RESTServiceResourceUrlPathBuilderForPersistenceBase<O extends OID>
				extends RESTServiceResourceUrlPathBuilderForModelObjectPersistenceBase<O> {
		public RESTServiceResourceUrlPathBuilderForPersistenceBase(final RESTServiceEndPointUrl endPointUrl,
																      final UrlPath resource) {
			super(endPointUrl,
				  resource);
		}
	}

	public static  class RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<O extends OID,ID extends OID>
				 extends RESTServiceResourceUrlPathBuilderForPersistenceBase<O> {
		public RESTServiceResourceUrlPathBuilderForEntityPersistenceBase(final RESTServiceEndPointUrl endPointUrl,
																		final UrlPath resource) {
			super(endPointUrl, resource);
		}
		public UrlPath pathOfEntityById(final ID id) {
			return this.pathOfAllEntities().joinedWith("byId",id);
		}
		public UrlPath pathOfEntityByEMail(final EMail email) {
			return this.pathOfAllEntities().joinedWith("byEMail",email);
		}
		public UrlPath pathOfEntityByPhone(final Phone phone) {
			return this.pathOfAllEntities().joinedWith("byPhone",phone);
		}

		public UrlPath pathOfEntityListByNameInLanguage(final Language lang,
										 				final String name) {
			return this.pathOfEntityList().joinedWith("byNameIn",lang,name);
		}
		public UrlPath pathOfEntityListByOids() {
			return this.pathOfEntityList().joinedWith("byOids");
		}
	}
}
