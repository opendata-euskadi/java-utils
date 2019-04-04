package r01f.ejie.nora;

import java.util.Collection;
import java.util.Map;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.nora.NORAServiceResponseParser.GeoIDFactory;
import r01f.ejie.nora.NORAServiceResponseParser.GeoLocationFactory;
import r01f.types.geo.GeoLocation;
import r01f.types.geo.GeoOIDs.GeoID;

@Slf4j
abstract class NORAServiceBase<I extends GeoID,G extends GeoLocation<I>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final NORAServiceConfig _noraConfig;
	private final NORAServiceMethodInvoker _noraWSMethodInvoker;
	private final GeoIDFactory<I> _idFactory;
	private final GeoLocationFactory<I,G> _locationFactory;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NORAServiceBase(final NORAServiceConfig noraConfig,
						   final GeoIDFactory<I> idFactory,final GeoLocationFactory<I,G> locationFactory) {
		_noraConfig = noraConfig;
		_noraWSMethodInvoker = new NORAServiceMethodInvoker(_noraConfig.getWsEndpointUrl());
		_idFactory = idFactory;
		_locationFactory = locationFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	Collection<G> getAllItems(final String noraWSMethod) {
		return this.getAllItems(noraWSMethod,
								null);	// no params
	}
	Collection<G> getAllItems(final String noraWSMethod,
							  final Map<String,String> noraWSMethodParams) {
		Collection<G> outItems = Lists.newArrayList();
		try {
			SOAPMessage soapMessage = _noraWSMethodInvoker.invokeNORAMethod(noraWSMethod,
																			noraWSMethodParams);
			if (!soapMessage.getSOAPBody().hasFault()) {
				Document xmlDoc = soapMessage.getSOAPBody()
											 .getOwnerDocument();
				outItems = NORAServiceResponseParser.parseNORAWSResponseForMultipleItems(noraWSMethod,
															  			  				 xmlDoc,
															  			  				 _idFactory,_locationFactory);
			} else {
				SOAPFault fault = soapMessage.getSOAPBody().getFault();
		        log.error("SOAP Fault code={}: {}",
		        		  fault.getFaultCode(),fault.getFaultString());
			}
		} catch (XPathExpressionException xpathEx) {
			xpathEx.printStackTrace();
		} catch (SOAPException soapEx) {
			soapEx.printStackTrace();
		}
		return outItems;
	}
	public G getItem(final String noraWSMethod,
					 final Map<String,String> noraWSMethodParams) {
		G outItem = null;
		try {
			SOAPMessage soapMessage = _noraWSMethodInvoker.invokeNORAMethod(noraWSMethod,
																			noraWSMethodParams);
			if (!soapMessage.getSOAPBody().hasFault()) {
				Document xmlDoc = soapMessage.getSOAPBody()
											 .getOwnerDocument();
				outItem = NORAServiceResponseParser.parseNORAWSResponseForSingleItem(noraWSMethod,
																					 xmlDoc,
																					 _idFactory,_locationFactory);
			} else {
				SOAPFault fault = soapMessage.getSOAPBody().getFault();
		        log.error("SOAP Fault code={}: {}",
		        		  fault.getFaultCode(),fault.getFaultString());
			}
		} catch (XPathExpressionException xpathEx) {
			xpathEx.printStackTrace();
		} catch (SOAPException soapEx) {
			soapEx.printStackTrace();
		}
		return outItem;
	}
}
