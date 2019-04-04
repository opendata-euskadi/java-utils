package r01f.services.dokusi;

import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.model.dokusi.DOKUSIRetrievedDocument;
import r01f.patterns.IsBuilder;
import r01f.services.pif.PifService;
import r01f.types.Path;

@Slf4j
@Accessors(prefix="_")
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class DOKUSIRetrievedDocumentBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern PIF_FILE_PATH_PATTERN = Pattern.compile("<pifId>([^<>]+)</pifId>");
	private static final Pattern PIF_FILE_LENGTH_PATTERN = Pattern.compile("<lenght>([^<>]+)</lenght>");	// lenght!!!! WTF!!! it's a mistake i hope!
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static DOKUSIRetrievedDocumentLoadBuilderStep from(final SOAPBodyElement dokusiWebServiceResponse) {
		// the soap body contains a pif path where the DOKUSI retrieved document was left
		// ... an xml as: 
		//		<pifId>([^<>]+)</pifId>
		//		<lenght>([^<>]+)</lenght>	
		String xml = _soapBodyAsString(dokusiWebServiceResponse);
		
		
		Matcher dP = PIF_FILE_PATH_PATTERN.matcher(xml);
		Path docPath = dP.find() ? Path.from(dP.group(1)) : null;
		
		Matcher mL = PIF_FILE_LENGTH_PATTERN.matcher(xml);
		int docSize = mL.find() ? Integer.valueOf(mL.group(1)) : 0;
		
		return new DOKUSIRetrievedDocumentBuilder() { /* nothing */ }
						.new DOKUSIRetrievedDocumentLoadBuilderStep(docPath,docSize);
	}
	@RequiredArgsConstructor
	public final class DOKUSIRetrievedDocumentLoadBuilderStep {
		private final Path _path;
		private final int _length;
		
		public DOKUSIRetrievedDocument  loadUsing(final PifService pifService) {
			log.info("\t... retrieved document at PIF file location {} ({} bytes)",
					  _path,_length);
			InputStream is = pifService.downloadFile(_path)
									   .asInputStream();
			DOKUSIRetrievedDocument outDoc = new DOKUSIRetrievedDocument(_path,_length,
																		 is);	
			return outDoc;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _soapBodyAsString(final SOAPBodyElement soapBodyElement) {
		StringBuilder outBodyStr = new StringBuilder(300);			
		Iterator<?> iterator = soapBodyElement.getChildElements();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof SOAPElement) {
				SOAPElement soapEl = (SOAPElement)obj;				
				outBodyStr.append(soapEl.getValue());
			}
		}
		return outBodyStr.toString();
	}
}
