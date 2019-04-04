package r01f.db.search.users;

import lombok.extern.slf4j.Slf4j;
import r01f.db.entities.users.DBEntityForUserData;
import r01f.model.search.SearchResultItem;
import r01f.model.security.filters.SearchResultItemForSecurity;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBEntity;

@Slf4j
public class DBSearchConverters {

	public class ConverterFromDBEntityToSearchResultItem<O extends UserDataModelOID,
														 U extends PersistableModelForUserData<O,U>> {

/////////////////////////////////////////////////////////////////////////////////////
// ATTRIBUTTE AND CONTRUCTORS
////////////////////////////////////////////////////////////////////////////////////
		private Marshaller _marhaller;
		private Class<U> _typeClass;

		public ConverterFromDBEntityToSearchResultItem(final Marshaller marhaller,final Class<U> typeClass){
			_marhaller = marhaller;
			_typeClass = typeClass;
		}

////////////////////////////////////////////////////////////////////////////////////
//DB ENTITY TO SEARCH RESULT ITEM
////////////////////////////////////////////////////////////////////////////////////

		@SuppressWarnings("unchecked" )
		public <DB extends DBEntity,I extends SearchResultItem> I dbEntityToSearchResultItem(final DB dbEntity) {
			log.debug("\n\n>>>>>>>>>>>>>>> ConverterFromDBEntityToSearchResultItem. Converter DBEntity  {}" ,
					dbEntity.getClass());

			DBEntityForUserData dbEntityforUser = (DBEntityForUserData)dbEntity;
			SearchResultItemForSecurity  searchResultItem = new SearchResultItemForSecurity();

			searchResultItem.setUserOID(UserDataOID.forId(dbEntityforUser.getOid()));

			/////////////////////////////////////////////////////////////////////////////////////////
			//  Campos obtenidos directamente del objeto db
			/////////////////////////////////////////////////////////////////////////////////////////
			if (dbEntityforUser.getCreateDate() != null ) {
				searchResultItem.setCreationDate(dbEntityforUser.getCreateDate().getTime());
			}
			/////////////////////////////////////////////////////////////////////////////////////////
			//  Getting fields from user object
			/////////////////////////////////////////////////////////////////////////////////////////
			log.debug(dbEntityforUser.getDescriptor());

			U user = _marhaller.forReading().fromXml(dbEntityforUser.getDescriptor(),_typeClass);
			//searchResultItem.setUserID(UserID.forId(dbEntityforUser.getId()));
			searchResultItem.setName(user.getContactData().getPersonalData().getName());
			searchResultItem.setSurname(user.getContactData().getPersonalData().getSurname1());
			searchResultItem.setSurname2(user.getContactData().getPersonalData().getSurname2());

			return (I) searchResultItem;
		}
	}

}
