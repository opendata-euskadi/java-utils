package r01f.ejie.nora;

import java.util.Collection;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.patterns.FactoryFrom;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCountry;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCounty;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToMunicipality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoLocation;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;
import r01f.types.geo.GeoPosition2D.GeoPositionStandad;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xml.XMLUtils;


@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class NORAServiceResponseParser {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public interface GeoIDFactory<I extends GeoID>
			 extends FactoryFrom<Long,I> {
		// just extend
	}
	public interface GeoLocationFactory<I extends GeoID,G extends GeoLocation<I>> {
		public G create();
		public G from(final I id,
					  final String officialName,final LanguageTexts nameByLang,
					  final GeoPosition2D pos2D);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Parses NORA response for spanish states
	 * Response of <pre>autonomia_getByDescResponse</pre> nora method
	 * <pre class='brush:java'>
	 *		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
	 *		   <soapenv:Header/>
	 *		   <env:Body xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" 
	 *					 xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
	 *					 xmlns:env="http://schemas.xmlsoap.org/soap/envelope/" 
	 *					 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	 *		      <m:autonomia_getByPkResponse xmlns:m="http://www.ejie.es/webServiceEJB/t17iApiWSWar">
	 *		         <n1:result xsi:type="n2:Autonomia" 
	 *							xmlns:n1="http://www.ejie.es/webServiceEJB/t17iApiWSWar" 
	 *							xmlns:n2="java:t17i.vo">
	 *		            <n2:descripcionOficial>Ceuta</n2:descripcionOficial>
	 *		            <n2:esDescripcion>Ceuta</n2:esDescripcion>
	 *		            <n2:euDescripcion xsi:nil="true"/>
	 *		            <n2:id>18</n2:id>
	 *		         </n1:result>
	 *		      </m:autonomia_getByPkResponse>
	 *		   </env:Body>
	 *		</soapenv:Envelope>
	 * </pre>
     *
	 * </pre>
	 * @param id
	 * @param methodName
	 * @param xmlDoc
	 * @throws XPathExpressionException 
	 */
	public static <I extends GeoID,
				   G extends GeoLocation<I>> G parseNORAWSResponseForSingleItem(final String methodName,
																   				final Document xmlDoc,
																   				final GeoIDFactory<I> idFactory,final GeoLocationFactory<I,G> locFactory) throws XPathExpressionException {
		String xPath = _xpath(methodName);
		if (log.isTraceEnabled()) {
			log.trace(XMLUtils.asString(xmlDoc));
			log.trace("...xpath={}",xPath);
		}
		NodeList geoItemFieldsXmlNodes = XMLUtils.nodeListByXPath(xmlDoc,
															 	  xPath);
		final G outState = _parseItemFields(geoItemFieldsXmlNodes,
											idFactory,locFactory);
		
		return outState;
	}
	/**
	 * Parses NORA response for spanish states
	 * Response of <pre>autonomia_getByDescResponse</pre> nora method
	 * <pre class='brush:java'>
	 *		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
	 *		   <soapenv:Header/>
	 *		   <env:Body xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
	 *					 xmlns:xsd="https://www.w3.org/2001/XMLSchema" 
	 *					 xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance"
	 *					 xmlns:env="http://schemas.xmlsoap.org/soap/envelope/">
	 *
	 *		      <m:autonomia_getByDescResponse xmlns:m="http://www.ejie.es/webServiceEJB/t17iApiWSWar">
	 *		         <n1:result xsi:type="n2:ArrayOfAutonomia" 
	 *							xmlns:n1="http://www.ejie.es/webServiceEJB/t17iApiWSWar" 
	 *							xmlns:n2="java:t17i.vo">
	 *		            <n2:Autonomia xsi:type="n2:Autonomia">
	 *		               <n2:descripcionOficial>Ceuta</n2:descripcionOficial>
	 *		               <n2:esDescripcion>Ceuta</n2:esDescripcion>
	 *		               <n2:euDescripcion xsi:nil="true"/>
	 *		               <n2:id>18</n2:id>
	 *		            </n2:Autonomia>
	 *				...
	 *				...
	 *			</env>
	 *		</soapenv:Envelope>
	 * </pre>
     *
	 * </pre>
	 * @param xmlDoc
	 * @throws XPathExpressionException 
	 */
	public static <I extends GeoID,
					G extends GeoLocation<I>> Collection<G> parseNORAWSResponseForMultipleItems(final String methodName,
																		  						final Document xmlDoc,
																		  						final GeoIDFactory<I> idFactory,final GeoLocationFactory<I,G> locFactory) throws XPathExpressionException {
		Collection<G> outItems = Lists.newArrayList();
		
		String xPath = _xpath(methodName);
		if (log.isTraceEnabled()) {
			log.trace(XMLUtils.asString(xmlDoc));
			log.trace("...xpath={}",xPath);
		}
		
		NodeList geoItemsXmlNodes = XMLUtils.nodeListByXPath(xmlDoc,
															 xPath);
		log.info("... found {} items",geoItemsXmlNodes.getLength());
		for (Node geoItemXmlNode : XMLUtils.nodeListIterableFrom(geoItemsXmlNodes)) {
			G geoItem = _parseResult(geoItemXmlNode,
							   		 idFactory,locFactory);
			if (geoItem != null) outItems.add(geoItem);
		}
		return outItems;
	}
	/**
	 * Parses a nora item result like 
	 * <pre class='brush:java'>
	 *	    <n2:Autonomia xsi:type="n2:Autonomia">
	 *	       <n2:descripcionOficial>Ceuta</n2:descripcionOficial>
	 *	       <n2:esDescripcion>Ceuta</n2:esDescripcion>
	 *	       <n2:euDescripcion xsi:nil="true"/>
	 *	       <n2:id>18</n2:id>
	 *	       <n2:pais xsi:nil="true"/>
	 *	    </n2:Autonomia>
	 * </pre>
	 * @param geoItemXmlNode
	 * @return
	 */
	private static <I extends GeoID,
					G extends GeoLocation<I>> G _parseResult(final Node geoItemXmlNode,
													   		 final GeoIDFactory<I> idFactory,final GeoLocationFactory<I,G> locFactory) {
		NodeList childXmlNodes = geoItemXmlNode.getChildNodes();
		
		G outItem = _parseItemFields(childXmlNodes,
									 idFactory,locFactory);
		return outItem;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Parses a nora item's fields like 
	 * <pre class='brush:java'>
	 *	    <n2:descripcionOficial>Ceuta</n2:descripcionOficial>
	 *	    <n2:esDescripcion>Ceuta</n2:esDescripcion>
	 *	    <n2:euDescripcion xsi:nil="true"/>
	 *	    <n2:id>18</n2:id>
     *      <n2:dxEd50>531968.228318682</n2:dxEd50>
     *      <n2:dxEtrs89>531862.536380481</n2:dxEtrs89>
     *      <n2:dyEd50>4778028.43698632</n2:dyEd50>
     *      <n2:dyEtrs89>4777820.29263956</n2:dyEtrs89>
	 * </pre>
	 * @param geoItemXmlNode
	 * @return
	 */
	private static <I extends GeoID,
					G extends GeoLocation<I>> G _parseItemFields(final NodeList geoItemFieldsXmlNodes,
													   			 final GeoIDFactory<I> idFactory,final GeoLocationFactory<I,G> locFactory) {
		G outItem = locFactory.create();
		if (geoItemFieldsXmlNodes == null) return outItem;
		
		GeoPosition2D ed50Pos2D = null;
		GeoPosition2D etrs89Pos2D = null;
		for (Node xmlNode : XMLUtils.nodeListIterableFrom(geoItemFieldsXmlNodes)) {
			// id
			if (xmlNode.getLocalName().equals("id")) {
				I id = idFactory.from(Long.parseLong(xmlNode.getFirstChild().getNodeValue()));
				outItem.setId(id);
			}
			// official & es | eu description
			else if (xmlNode.getLocalName().equals("descripcionOficial")) {
				String offName = xmlNode.getFirstChild().getNodeValue();
				outItem.setOfficialName(offName);
			} 
			else if (xmlNode.getLocalName().equals("esDescripcion")) {
				String nil = XMLUtils.nodeAttributeValue(xmlNode,"xsi:nil");
				if (Strings.isNullOrEmpty(nil)
				 || nil.equals("false")) {
					String esName = xmlNode.getFirstChild().getNodeValue();
					outItem.getName().add(Language.SPANISH,esName);
				}
			}
			else if (xmlNode.getLocalName().equals("euDescripcion")) {
				String nil = XMLUtils.nodeAttributeValue(xmlNode,"xsi:nil");
				if (Strings.isNullOrEmpty(nil)
				 || nil.equals("false")) {
					String euName = xmlNode.getFirstChild().getNodeValue();
					outItem.getName().add(Language.BASQUE,euName);
				}
			}
			// x,y position (spain)
			else if (xmlNode.getLocalName().equals("dxEd50")) {
				String nil = XMLUtils.nodeAttributeValue(xmlNode,"xsi:nil");
				if (Strings.isNullOrEmpty(nil)
				 || nil.equals("false")) {
					String x = xmlNode.getFirstChild().getNodeValue();
					if (ed50Pos2D == null) ed50Pos2D = new GeoPosition2D(GeoPositionStandad.ED50);
					ed50Pos2D.setX(Double.parseDouble(x));
				}
			}
			else if (xmlNode.getLocalName().equals("dyEd50")) {
				String nil = XMLUtils.nodeAttributeValue(xmlNode,"xsi:nil");
				if (Strings.isNullOrEmpty(nil)
				 || nil.equals("false")) {
					String y = xmlNode.getFirstChild().getNodeValue();
					if (ed50Pos2D == null) ed50Pos2D = new GeoPosition2D(GeoPositionStandad.ED50);
					ed50Pos2D.setY(Double.parseDouble(y));	
				}
			}
			// x,y position (google earth)
			else if (xmlNode.getLocalName().equals("dxEtrs89")) {
				String nil = XMLUtils.nodeAttributeValue(xmlNode,"xsi:nil");
				if (Strings.isNullOrEmpty(nil)
				 || nil.equals("false")) {
					String x = xmlNode.getFirstChild().getNodeValue();
					if (etrs89Pos2D == null) etrs89Pos2D = new GeoPosition2D(GeoPositionStandad.GOOGLE);
					etrs89Pos2D.setX(Double.parseDouble(x));
				}
			}
			else if (xmlNode.getLocalName().equals("dyEtrs89")) {
				String nil = XMLUtils.nodeAttributeValue(xmlNode,"xsi:nil");
				if (Strings.isNullOrEmpty(nil)
				 || nil.equals("false")) {
					String y = xmlNode.getFirstChild().getNodeValue();
					if (etrs89Pos2D == null) etrs89Pos2D = new GeoPosition2D(GeoPositionStandad.GOOGLE);
					etrs89Pos2D.setY(Double.parseDouble(y));
				}
			}
		}
		if (etrs89Pos2D != null) {
			outItem.setPosition2D(etrs89Pos2D);
		} else if (ed50Pos2D != null) {
			outItem.setPosition2D(ed50Pos2D);
		}
		
		// ------ Facet data
		List<GeoID> idHierarchy = _findGeoIds(geoItemFieldsXmlNodes);
		if (outItem instanceof GeoLocationBelongsToCountry) {
			GeoCountryID countryId = _findId(idHierarchy,GeoCountryID.class); 
			if (countryId == null) countryId = NORAGeoIDs.SPAIN;	
			outItem.asBelongsToCountry()
				   .setCountryId(countryId);
		}
		if (outItem instanceof GeoLocationBelongsToTerritory) {
			GeoTerritoryID id = _findId(idHierarchy,GeoTerritoryID.class);
			if (id != null) outItem.asBelongsToTerritory().setTerritoryId(id);
		}
		if (outItem instanceof GeoLocationBelongsToState) {
			GeoStateID id = _findId(idHierarchy,GeoStateID.class);
			if (id != null) outItem.asBelongsToState().setStateId(id);
		}
		if (outItem instanceof GeoLocationBelongsToCounty) {
			GeoCountyID id = _findId(idHierarchy,GeoCountyID.class);
			if (id != null) outItem.asBelongsToCounty().setCountyId(id);
		}
		if (outItem instanceof GeoLocationBelongsToRegion) {
			GeoRegionID id = _findId(idHierarchy,GeoRegionID.class);
			if (id != null) outItem.asBelongsToRegion().setRegionId(id);
		}
		if (outItem instanceof GeoLocationBelongsToMunicipality) {
			GeoMunicipalityID id = _findId(idHierarchy,GeoMunicipalityID.class);
			if (id != null) outItem.asBelongsToMunicipality().setMunicipalityId(id);
		}
		return outItem;
	}
	@SuppressWarnings("unchecked")
	private static <I extends GeoID> I _findId(final Collection<GeoID> geoIds,
											   final Class<I> idType) {
		if (CollectionUtils.isNullOrEmpty(geoIds)) return null;
		I outId = null;
		for (GeoID geoId : geoIds) {
			if (geoId.getClass() == idType) {
				outId = (I)geoId;
				break;
			}
		}
		return outId;
	}
	private static List<GeoID> _findGeoIds(final NodeList geoItemFieldsXmlNodes) {
		List<GeoID> outIds = Lists.newArrayList();
		if (geoItemFieldsXmlNodes == null) return outIds;
		
		for (Node xmlNode : XMLUtils.nodeListIterableFrom(geoItemFieldsXmlNodes)) {
			if (xmlNode.getLocalName().equals("Municipio")
			 || xmlNode.getLocalName().equals("provincia")
			 || xmlNode.getLocalName().equals("comarca")
			 || xmlNode.getLocalName().equals("autonomia")
			 || xmlNode.getLocalName().equals("pais")) {
				List<GeoID> ids = _findGeoIds(xmlNode);
				if (CollectionUtils.hasData(ids)) outIds.addAll(ids);
			}
		}
		return outIds;
	}
	/**
	 * Finds the geo location hierarchy bottom-up
	 * Ej:
	 * <pre class='xml'>
     *      <n2:Municipio xsi:type="n2:Municipio">
     *         <n2:comarca xsi:type="n2:Comarca">
     *            <n2:descripcionOficial>Durangaldea/Duranguesado</n2:descripcionOficial>
     *            <n2:esDescripcion>Duranguesado</n2:esDescripcion>
     *            <n2:euDescripcion>Durangaldea</n2:euDescripcion>
     *            <n2:id>03</n2:id>
     *            <n2:idProvincia>48</n2:idProvincia>
     *            <n2:provincia xsi:type="n2:Provincia">
     *               <n2:autonomia xsi:type="n2:Autonomia">
     *                  <n2:descripcionOficial>Comunidad Autónoma de Euskadi</n2:descripcionOficial>
     *                  <n2:esDescripcion>Comunidad Autónoma de Euskadi</n2:esDescripcion>
     *                  <n2:euDescripcion>Euskal Herriko Elkarte Autonomoa</n2:euDescripcion>
     *                  <n2:id>16</n2:id>
     *                  <n2:pais xsi:nil="true"/>
     *               </n2:autonomia>
     *               <n2:descripcionOficial>Bizkaia</n2:descripcionOficial>
     *               <n2:esDescripcion>Bizkaia</n2:esDescripcion>
     *               <n2:euDescripcion>Bizkaia</n2:euDescripcion>
     *               <n2:id>48</n2:id>
     *            </n2:provincia>
     *         </n2:comarca>
     *         <n2:descripcionOficial>Abadiño</n2:descripcionOficial>
     *         <n2:id>001</n2:id>
     *         <n2:idProvincia>48</n2:idProvincia>
     *         <n2:provincia xsi:type="n2:Provincia">
     *            <n2:autonomia xsi:type="n2:Autonomia">
     *               <n2:descripcionOficial>Comunidad Autónoma de Euskadi</n2:descripcionOficial>
     *               <n2:esDescripcion>Comunidad Autónoma de Euskadi</n2:esDescripcion>
     *               <n2:euDescripcion>Euskal Herriko Elkarte Autonomoa</n2:euDescripcion>
     *               <n2:id>16</n2:id>
     *               <n2:pais xsi:nil="true"/>
     *            </n2:autonomia>
     *            <n2:descripcionOficial>Bizkaia</n2:descripcionOficial>
     *            <n2:esDescripcion>Bizkaia</n2:esDescripcion>
     *            <n2:euDescripcion>Bizkaia</n2:euDescripcion>
     *            <n2:id>48</n2:id>
     *         </n2:provincia>
     *      </n2:Municipio>
     * </pr>
	 * @param node
	 * @return
	 */
	private static List<GeoID> _findGeoIds(final Node xmlNode) {	
		List<GeoID> outIds = Lists.newArrayList();
		if (xmlNode.getLocalName().equals("Municipio")
		 && xmlNode.hasChildNodes()) {			
			for (Node munXmlNode : XMLUtils.nodeListIterableFrom(xmlNode.getChildNodes())) {
				if (munXmlNode.getLocalName().equals("id")) {
					GeoMunicipalityID munId = NORAServiceForMunicipality.GEO_ID_FACTORY.from(Long.parseLong(munXmlNode.getFirstChild().getNodeValue()));
					outIds.add(munId);
				}
				if (munXmlNode.getLocalName().equals("provincia")) {
					List<GeoID> moreIds = _findGeoIds(munXmlNode);			// recursive call
					if (CollectionUtils.hasData(moreIds)) outIds.addAll(moreIds);
				}
			}
		} 
		else if (xmlNode.getLocalName().equals("comarca")
			  && xmlNode.hasChildNodes()) {
			for (Node regionXmlNode : XMLUtils.nodeListIterableFrom(xmlNode.getChildNodes())) {
				if (regionXmlNode.getLocalName().equals("id")) {
					GeoRegionID regionId = NORAServiceForRegion.GEO_ID_FACTORY.from(Long.parseLong(regionXmlNode.getFirstChild().getNodeValue()));
					outIds.add(regionId);
				}
				if (regionXmlNode.getLocalName().equals("provincia")) {
					List<GeoID> moreIds = _findGeoIds(regionXmlNode);			// recursive call
					if (CollectionUtils.hasData(moreIds)) outIds.addAll(moreIds);
				}
			}
		}
		else if (xmlNode.getLocalName().equals("provincia")
			  && xmlNode.hasChildNodes()) {
			for (Node countyXmlNode : XMLUtils.nodeListIterableFrom(xmlNode.getChildNodes())) {
				if (countyXmlNode.getLocalName().equals("id")) {
					GeoCountyID countyId = NORAServiceForCounty.GEO_ID_FACTORY.from(Long.parseLong(countyXmlNode.getFirstChild().getNodeValue()));
					outIds.add(countyId);
				}
				if (countyXmlNode.getLocalName().equals("autonomia")) {
					List<GeoID> moreIds = _findGeoIds(countyXmlNode);			// recursive call
					if (CollectionUtils.hasData(moreIds)) outIds.addAll(moreIds);
				}
			}
		}
		else if (xmlNode.getLocalName().equals("autonomia")
			  && xmlNode.hasChildNodes()) {
			for (Node stateXmlNode : XMLUtils.nodeListIterableFrom(xmlNode.getChildNodes())) {
				if (stateXmlNode.getLocalName().equals("id")) {
					GeoStateID stateId = NORAServiceForState.GEO_ID_FACTORY.from(Long.parseLong(stateXmlNode.getFirstChild().getNodeValue()));
					outIds.add(stateId);
				}
				if (stateXmlNode.getLocalName().equals("pais")) {
					List<GeoID> moreIds = _findGeoIds(stateXmlNode);			// recursive call
					if (CollectionUtils.hasData(moreIds)) outIds.addAll(moreIds);
				}
			}
		}
		else if (xmlNode.getLocalName().equals("pais")
			  && xmlNode.hasChildNodes()) {
			for (Node countryXmlNode : XMLUtils.nodeListIterableFrom(xmlNode.getChildNodes())) {
				if (countryXmlNode.getLocalName().equals("id")) {
					GeoStateID stateId = NORAServiceForState.GEO_ID_FACTORY.from(Long.parseLong(countryXmlNode.getFirstChild().getNodeValue()));
					outIds.add(stateId);
				}
			}
		}
		return outIds;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("unused")
	private static String _xpath(final String methodName) {
		// this xpath forces to use a namespace resolver
		String xpathNS = "//soapenv:Envelope" +
								"/env:Body" + 
									"/m:{}Response" +
										"/n1:result" +
											"/*";
		// this xpath is NOT usable with the xpath parser of the java sdk (Java sdk does not allow xpath 2.0: must use saxon)
		String xpathNoNS_v2 = "//*:Envelope"+	
									"/*:Body" +
										"/*:{}Response" +
											"/*:result" +
												"/*";
		// this xpath is usable with the xpath parser of the java sdk (java sdk only allow xpath 1.0)		
		String xpathNoNS_V1 = "//*[local-name()='Envelope']" +
									"/*[local-name()='Body']" +
										"/*[local-name()='{}Response']" +
											"/*[local-name()='result']" +
												"/*";
		
		String xPath = Strings.customized(xpathNoNS_V1,
										  methodName);
		return xPath;
	}
}
