package r01f.objectstreamer;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.MarshallerMapperForJson;
import r01f.objectstreamer.MarshallerMapperForXml;

@Slf4j
abstract class TestObjectStreamerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected static <T> void _doTest(final T obj,
									  final Class<T> objType,
									  final MarhallTestCheck<T> check) throws IOException {
		_testJsonStreamer(obj,
						  objType, 
						  check);
		_testXmlStreamer(obj,
						 objType, 
						 check);
	}
	protected static <T> void _doTest(final Collection<T> objCol,
									  final Class<T> objType,
									  final MarhallTestCheck<T> check) throws IOException {
		_testJsonStreamer(objCol,
						  objType, 
						  check);
		_testXmlStreamer(objCol,
						 objType, 
						 check);
	}
	protected static <T> void _doTest(final T obj,
									  final TypeToken<T> objType,
									  final MarhallTestCheck<T> check) throws IOException {
		_testJsonStreamer(obj,
						  objType, 
						  check);
		_testXmlStreamer(obj,
						 objType, 
						 check);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	XML
/////////////////////////////////////////////////////////////////////////////////////////
	protected static <T> void _testXmlStreamer(final T obj,
									  		   final Class<? extends T> objType,
									  		   final MarhallTestCheck<T> check) throws IOException {
		MarshallerMapperForXml mapper = new MarshallerMapperForXml();

		// write as xml
		log.warn("[INIT][XML SERIALIZER TEST of instance of {}]",objType);
		log.warn("Write as Xml:");
		String xml = mapper.writeValueAsString(obj);
		log.warn("Serialized Xml\n{}",xml);
		log.warn("[END][XML SERIALIZER TEST of instance of {}]",objType);
		
		// Read from xml
		log.warn("[INIT][XML DESERIALIZER TEST of instance of {}]",objType);
		log.warn("Read from Xml:");
		T objReadedFromXml = mapper.readValue(xml,
								  			  objType);
		log.warn("Obj readed from serialized xml: {}",
				 objReadedFromXml.getClass().getName());
		
		// checks
		check.check(obj,objReadedFromXml);
		log.warn("[END][XML DESERIALIZER TEST of instance of {}]",objType);
	}
	protected static <T> void _testXmlStreamer(final T obj,
									  		   final TypeToken<T> objType,
									  		   final MarhallTestCheck<T> check) throws IOException {
		MarshallerMapperForXml mapper = new MarshallerMapperForXml();

		// write as xml
		log.warn("[INIT][XML SERIALIZER TEST of instance of {}]",objType.getType());
		log.warn("Write as Xml:");
		String xml = mapper.writeValueAsString(obj);
		log.warn("Serialized Xml\n{}",xml);
		log.warn("[END][XML SERIALIZER TEST of instance of {}]",objType.getType());
		
		// Read from xml
		log.warn("[INIT][XML DESERIALIZER TEST of instance of {}]",objType.getType());
		log.warn("Read from Xml:");
		T objReadedFromXml = mapper.readValue(xml,
								  			  mapper.constructType(objType.getType()));
		log.warn("Obj readed from serialized xml: {}",
				 objReadedFromXml.getClass().getName());
		
		// checks
		check.check(obj,objReadedFromXml);
		log.warn("[END][XML DESERIALIZER TEST of instance of {}]",objType.getType());
	}
	protected static <T> void _testXmlStreamer(final Collection<T> objCol,
									  		   final Class<? extends T> objType,
									  		   final MarhallTestCheck<T> check) throws IOException {
		MarshallerMapperForXml mapper = new MarshallerMapperForXml();

		// write as xml
		log.warn("[INIT][XML SERIALIZER TEST of collection of {}]",objType);
		log.warn("Write as Xml:");
		String xml = mapper.writeValueAsString(objCol);
		log.warn("Serialized Xml\n{}",xml);
		log.warn("[END][XML SERIALIZER TEST of collection of {}]",objType);
		
		// Read from xml
		log.warn("[INIT][XML DESERIALIZER TEST of collection of {}]",objType);
		log.warn("Read from Xml:");
		Collection<T> objColReadedFromXml = mapper.readValue(xml,
								  			  				 new TypeReference<Collection<T>>() {/* nothing */});
		log.warn("Collection readed from serialized xml: {}",
				 objColReadedFromXml.getClass().getName());
		
		// checks
		Assert.assertEquals(objCol.size(),objColReadedFromXml.size());
		log.warn("[END][XML DESERIALIZER TEST of collection of {}]",objType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	JSON
/////////////////////////////////////////////////////////////////////////////////////////	
	protected static <T> void _testJsonStreamer(final T obj,
										 		final Class<? extends T> objType,
										 		final MarhallTestCheck<T> check) throws IOException {
		MarshallerMapperForJson mapper = new MarshallerMapperForJson();

		// write as xml
		log.warn("[INIT][JSON SERIALIZER TEST of instance of {}]",objType);
		log.warn("Write as Json:");
		String json = mapper.writeValueAsString(obj);
		log.warn("Serialized Json\n{}",json);
		log.warn("[END][JSON SERIALIZER TEST of instance of {}]",objType);
		
		// Read from xml
		log.warn("[INIT][JSON DESERIALIZER TEST of instance of {}]",objType);
		log.warn("Read from Json:");
		T objReadedFromJson = mapper.readValue(json,
								  			   objType);
		log.warn("Obj readed from serialized json: {}",
				 objReadedFromJson.getClass().getName());
		
		// checks
		check.check(obj,objReadedFromJson);
		log.warn("[END][JSON DESERIALIZER TEST of instance of {}]",objType);
	}
	protected static <T> void _testJsonStreamer(final T obj,
										 		final TypeToken<T> objType,
										 		final MarhallTestCheck<T> check) throws IOException {
		MarshallerMapperForJson mapper = new MarshallerMapperForJson();

		// write as json
		log.warn("[INIT][JSON SERIALIZER TEST of instance of {}]",objType.getType());
		log.warn("Write as Json:");
		String json = mapper.writeValueAsString(obj);
		log.warn("Serialized Json\n{}",json);
		log.warn("[END][JSON SERIALIZER TEST of instance of {}]",objType.getType());
		
		// Read from json
		log.warn("[INIT][JSON DESERIALIZER TEST of instance of {}]",objType.getType());
		log.warn("Read from Json:");
		T objReadedFromJson = mapper.readValue(json,
								  			   mapper.constructType(objType.getType()));
		log.warn("Obj readed from serialized json: {}",
				 objReadedFromJson.getClass().getName());
		// checks
		check.check(obj,objReadedFromJson);
		log.warn("[END][JSON DESERIALIZER TEST of instance of {}]",objType.getType());
	}
	protected static <T> void _testJsonStreamer(final Collection<T> objCol,
										 		final Class<? extends T> objType,
										 		final MarhallTestCheck<T> check) throws IOException {
		MarshallerMapperForJson mapper = new MarshallerMapperForJson();

		// write as json
		log.warn("[INIT][JSON SERIALIZER TEST of collection of {}]",objType);
		log.warn("Write as Json:");
		String json = mapper.writeValueAsString(objCol);
		log.warn("Serialized Json\n{}",json);
		log.warn("[END][JSON SERIALIZER TEST of collection of {}]",objType);
		
		// Read from json
		log.warn("[INIT][JSON DESERIALIZER TEST of collection of {}]",objType);
		log.warn("Read from Json:");
		Collection<T> objColReadedFromJson = mapper.readValue(json,
								  			   				  new TypeReference<Collection<T>>() {/* nothing */});
		log.warn("Collection readed from serialized json: {}",
				 objColReadedFromJson.getClass().getName());
		
		// checks
		Assert.assertEquals(objCol.size(),objColReadedFromJson.size());
		log.warn("[END][JSON DESERIALIZER TEST of instance of {}]",objType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected interface MarhallTestCheck<T> {
		public void check(final T original,final T readed);
	}
	protected static <T> MarhallTestCheck<T> _buildObjChecker(final TypeToken<T> typeRef,
															  final Function<T,Object> dataExtr) {
		return _buildObjChecker((Class<T>)typeRef.getRawType(), 
						 		dataExtr);
	}
	protected static <T> MarhallTestCheck<T> _buildObjChecker(final Class<T> type,
															  final Function<T,Object> dataExtr) {
		return new MarhallTestCheck<T>() {
							@Override
							public void check(final T original,final T readed) {
								Assert.assertEquals(dataExtr.apply(original),dataExtr.apply(readed));
							}
				   };
	}
	protected static <T> MarhallTestCheck<T> _buildObjEqualsChecker(final TypeToken<T> typeRef) {
		return _buildObjEqualsChecker((Class<T>)typeRef.getRawType());
	}
	protected static <T> MarhallTestCheck<T> _buildObjEqualsChecker(final Class<T> type) {
		return _buildObjChecker(type,
								new Function<T,Object>() {
										@Override
										public Object apply(final T obj) {
											return obj;		// just return the obj
										}
			
								});
	}
}
