package r01f.persistence.search.db;

import com.google.common.base.Function;

import r01f.facets.HasOID;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.IndexableModelObject;
import r01f.model.TrackableModelObject.HasTrackableFacet;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.persistence.db.DBEntity;
import r01f.securitycontext.SecurityContext;
import r01f.types.summary.Summary;
import r01f.types.summary.SummaryLanguageTextsBacked;
import r01f.types.summary.SummaryStringBacked;

/**
 * Base impl of {@link TransformsDBEntityToSearchResultItem}
 * @param <DB>
 * @param <I>
 */
public abstract class TransformsDBEntityToSearchResultItemBase<DB extends DBEntity,I extends SearchResultItem> 
		   implements TransformsDBEntityToSearchResultItem<DB,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns this transformer as a {@link Function}
	 * @param securityContext
	 * @return
	 */
	public Function<DB,I> asTransformFuncion(final SecurityContext securityContext,
											 final Language lang) {
		return new Function<DB,I>() {			
						@Override
						public I apply(final DB dbEntity) {
							return TransformsDBEntityToSearchResultItemBase.this.dbEntityToSearchResultItem(securityContext,
																		  									dbEntity,
																		  									lang);
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	RESULT ITEMS 
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected static <I extends SearchResultItemForModelObject<? extends IndexableModelObject>> void _setResultItemCommonFieldsFromModelObject(final IndexableModelObject modelObj,
																																			   final I item) {
		TypeMetaData<?> modelObjectMetadata = TypeMetaDataInspector.singleton()
																   .getTypeMetaDataFor(modelObj.getClass());
		
		// Model object type
		item.unsafeSetModelObjectType((Class<? extends IndexableModelObject>)modelObj.getClass());
		item.setModelObjectTypeCode(modelObjectMetadata.getTypeMetaData()
													   .modelObjTypeCode());
		
		// Tracking info
		if (modelObjectMetadata.hasFacet(HasTrackableFacet.class)) {
			HasTrackableFacet modelObjTrackable = (HasTrackableFacet)modelObj;
			item.setTrackingInfo(modelObjTrackable.getTrackingInfo());
		}
		
		// OID
		if (modelObjectMetadata.hasFacet(HasOID.class)) {
			HasOID<? extends OID> hasOidModelObj = (HasOID<? extends OID>)modelObj;
			OID oid = hasOidModelObj.getOid();
			item.unsafeSetOid(oid);
		}	
		// numeric id
		if (modelObjectMetadata.hasFacet(HasNumericID.class)) {
			item.setNumericId(modelObj.getNumericId());
		}
		// EntityVersion 
		if (modelObjectMetadata.hasFacet(HasEntityVersion.class)) {
			item.setEntityVersion(modelObj.getEntityVersion());
		}
		// Summary
		if (modelObjectMetadata.hasFacet(HasLangDependentNamedFacet.class)) {
			HasLangDependentNamedFacet modelObjHasNames = (HasLangDependentNamedFacet)modelObj;
			LanguageTexts names = modelObjHasNames.getNamesByLanguage();
			Summary summary = SummaryLanguageTextsBacked.of(names);
			item.asSummarizable()
				.setSummary(summary);
		} else {
			HasLangInDependentNamedFacet modelObjHasName = (HasLangInDependentNamedFacet)modelObj;
			String name = modelObjHasName.getName();
			Summary summary = SummaryStringBacked.of(name);
			item.asSummarizable()
				.setSummary(summary);
		}
	}
}
