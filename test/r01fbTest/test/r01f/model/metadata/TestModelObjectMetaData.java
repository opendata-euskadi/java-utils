package r01f.model.metadata;

import org.junit.Assert;
import org.junit.Test;

import r01f.model.mock.MyOIDs.MyTestOID;
import r01f.model.mock.MyTestModelObject;

public class TestModelObjectMetaData {
	@Test
	public void testTypeMetaDataInspector() {
		// Inspect metadata
		TypeMetaDataInspector inspector = new TypeMetaDataInspector();
		inspector.inspect(MyTestModelObject.class);
		
		// get the metadata for the parent object
		TypeMetaData<?> myObjMetaData = inspector.getTypeMetaDataFor(MyTestModelObject.class);
		System.out.println(myObjMetaData.debugShortInfo());
		System.out.println("=====================================================");
		
		// get the metadata for the dependent object
		TypeMetaData<?> myDepObjMetaData = myObjMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.SUB)
														.getFieldTypeMetaData();
		System.out.println(myDepObjMetaData.debugShortInfo());
		
		
		Assert.assertEquals(myObjMetaData.getType(),MyTestModelObject.class);
//		Assert.assertEquals(myObjMetaData.getFieldsMetaData().size(),22);		// add fields at MyTestModelObjectMetaData with all the fields in it's supertypes (ie TypeMetaDataForPersistableModelObjectBase)
		
		_checkPersistableTypesFields(myObjMetaData);
		_checkTestModelObjectFields(myObjMetaData);
		
		// check nested
		TypeFieldMetaData myDepObjSubNameFieldMetaData = myObjMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.SUB,
																					 		MetaDataForMyTestDependentModelObject.SEARCHABLE_METADATA.YEAR);
		Assert.assertNotNull(myDepObjSubNameFieldMetaData);
		Assert.assertTrue(myDepObjSubNameFieldMetaData.asFieldMetaData() instanceof FieldMetaDataForYear);
		
	}
	private static void _checkTestModelObjectFields(final TypeMetaData<?> typeMetaData) {
		// get fields
		TypeFieldMetaData idField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasIDModelObject.SEARCHABLE_METADATA.ID);
		TypeFieldMetaData nameField = typeMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.NAME);
		TypeFieldMetaData langField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasLanguageModelObject.SEARCHABLE_METADATA.LANGUAGE);
		TypeFieldMetaData enumField = typeMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.ENUM);
		TypeFieldMetaData urlField = typeMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.URL);
		TypeFieldMetaData pathField = typeMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.PATH);
		TypeFieldMetaData langTextsField = typeMetaData.findFieldByIdOrThrow(MetaDataForMyTestInterface.SEARCHABLE_METADATA.TEXTS);
		TypeFieldMetaData colField = typeMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.COL);
		TypeFieldMetaData mapField = typeMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.MAP);
		TypeFieldMetaData subField = typeMetaData.findFieldByIdOrThrow(MetaDataForMyTestModelObject.SEARCHABLE_METADATA.SUB);
		
		// check field metadata existence
		Assert.assertNotNull(idField);
		Assert.assertNotNull(nameField);
		Assert.assertNotNull(langField);
		Assert.assertNotNull(enumField);
		Assert.assertNotNull(urlField);
		Assert.assertNotNull(pathField);
		Assert.assertNotNull(langTextsField);
		Assert.assertNotNull(colField);
		Assert.assertNotNull(mapField);
		Assert.assertNotNull(subField);
		
		// check FieldMetaData conversion
		FieldMetaData idFieldMetaData = idField.asFieldMetaData();
		FieldMetaData nameFieldMetaData = nameField.asFieldMetaData();
		FieldMetaData langFieldMetaData = langField.asFieldMetaData();
		FieldMetaData enumFieldMetaData = enumField.asFieldMetaData();
		FieldMetaData urlFieldMetaData = urlField.asFieldMetaData();
		FieldMetaData pathFieldMetaData = pathField.asFieldMetaData();
		FieldMetaData langTextsFieldMetaData = langTextsField.asFieldMetaData();
		FieldMetaData colFieldMetaData = colField.asFieldMetaData();
		FieldMetaData mapFieldMetaData = mapField.asFieldMetaData();
		FieldMetaData subFieldMetaData = subField.asFieldMetaData();
		
		Assert.assertTrue(idFieldMetaData instanceof FieldMetaDataForOID && idFieldMetaData.as(FieldMetaDataForOID.class).getDataType() == MyTestOID.class);
		Assert.assertTrue(nameFieldMetaData instanceof FieldMetaDataForString);
		Assert.assertTrue(langFieldMetaData instanceof FieldMetaDataForLanguage);
		Assert.assertTrue(enumFieldMetaData instanceof FieldMetaDataForEnum);
		Assert.assertTrue(urlFieldMetaData instanceof FieldMetaDataForUrl);
		Assert.assertTrue(pathFieldMetaData instanceof FieldMetaDataForPath);
		Assert.assertTrue(langTextsFieldMetaData instanceof FieldMetaDataForLanguageTexts);
		Assert.assertTrue(colFieldMetaData instanceof FieldMetaDataForCollection && colFieldMetaData.as(FieldMetaDataForCollection.class).getComponentsType() == String.class);
		Assert.assertTrue(mapFieldMetaData instanceof FieldMetaDataForMap && mapFieldMetaData.as(FieldMetaDataForMap.class).getKeyComponentsType() == Integer.class 
																		  && mapFieldMetaData.as(FieldMetaDataForMap.class).getValueComponentsType() == String.class);

		Assert.assertTrue(subFieldMetaData instanceof FieldMetaDataForDependentObject 
				       && subFieldMetaData.as(FieldMetaDataForDependentObject.class).getChildMetaData().size() == 6); // 4 fields of TypeMetaDataForModelObjectBase + 2 fields of FieldMetaDataForDependentObject
	}
	private static void _checkPersistableTypesFields(final TypeMetaData<?> typeMetaData) {
		TypeFieldMetaData oidField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID);
		TypeFieldMetaData entityVersionField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasEntityVersionModelObject.SEARCHABLE_METADATA.ENTITY_VERSION);
		TypeFieldMetaData createDateField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATE_DATE);
		TypeFieldMetaData lastUpdateDateField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATE_DATE);
		TypeFieldMetaData creatorField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.CREATOR);
		TypeFieldMetaData lastUpdatorField = typeMetaData.findFieldByIdOrThrow(HasMetaDataForHasTrackableFacetForModelObject.SEARCHABLE_METADATA.LAST_UPDATOR);

		// check field metadata existence
		Assert.assertNotNull(oidField);
		Assert.assertNotNull(entityVersionField);
		Assert.assertNotNull(createDateField);
		Assert.assertNotNull(lastUpdateDateField);
		Assert.assertNotNull(creatorField);
		Assert.assertNotNull(lastUpdatorField);
		
		// check FieldMetaData conversion
		FieldMetaData oidFieldMetaData = oidField.asFieldMetaData();
		FieldMetaData entityVersionFieldMetaData = entityVersionField.asFieldMetaData();
		FieldMetaData createDateFieldMetaData = createDateField.asFieldMetaData();
		FieldMetaData lastUpdateFieldMetaData = lastUpdateDateField.asFieldMetaData();
		FieldMetaData creatorFieldMetaData = creatorField.asFieldMetaData();
		FieldMetaData lastUpdatorFieldMetaData = lastUpdatorField.asFieldMetaData();
		
		Assert.assertTrue(oidFieldMetaData instanceof FieldMetaDataForOID && oidFieldMetaData.as(FieldMetaDataForOID.class).getDataType() == MyTestOID.class);
		Assert.assertTrue(entityVersionFieldMetaData instanceof FieldMetaDataForLong);
		Assert.assertTrue(createDateFieldMetaData instanceof FieldMetaDataForDate);
		Assert.assertTrue(lastUpdateFieldMetaData instanceof FieldMetaDataForDate);
		Assert.assertTrue(creatorFieldMetaData instanceof FieldMetaDataForOID);
		Assert.assertTrue(lastUpdatorFieldMetaData instanceof FieldMetaDataForOID);
	}
}
