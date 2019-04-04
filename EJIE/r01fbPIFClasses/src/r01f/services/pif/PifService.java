package r01f.services.pif;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.w3c.dom.Document;

import com.ejie.y31.exception.Y31JanoServiceGenericException;
import com.ejie.y31.factory.Y31JanoServiceAbstractFactory;
import com.ejie.y31.service.Y31JanoService;
import com.ejie.y31.vo.Y31AttachmentBean;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.api.XLNetsAPI;
import r01f.exceptions.Throwables;
import r01f.file.FileNameAndExtension;
import r01f.mime.MimeTypes;
import r01f.model.pif.PifFile;
import r01f.model.pif.PifFileInfo;
import r01f.patterns.Factory;
import r01f.patterns.Memoized;
import r01f.types.Path;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Encapsulates pif services
 * IMPORTANT!!!!!
 * 		Graphical user interface URL (ui console): http://svc.integracion.jakina.ejiedes.net/y31dBoxWAR/appbox
 *
 *
 * To build a {@link PifService} a {@link PifServiceAPIData} and a {@link XLNetsAuthTokenProvider} are needed
 * Both {@link PifServiceAPIData} and {@link XLNetsAuthTokenProvider} can be built from an {@link XMLPropertiesForAppComponent}
 * <pre class='brush:java'>
 *		// Provide a new pif service api data using the provider
 *		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
 *																 .notUsingCache()
 *																 .forComponent(AppComponent.forId("test"));
 *		XLNetsAuthTokenProvider xlnetsAuthTokenProvider = new XLNetsAuthTokenProvider(props,
 *																			 		  "test");
 *		PifServiceAPIData pifApiData = new PifServiceAPIData(props,
 *															 "test");
 *
 *		// Create the pif service
 *		PifService pifService = new PifService(pifApiData,
 *											   xlnetsAuthTokenProvider);
 * </pre>
 *
 * Using guice:
 * <pre class='brush:java'>
 * 		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
 * 																 .notUsingCache()
 * 																 .forComponent(AppComponent.forId("test"));
 * 		PifServiceAPIData pifServiceApiData = new PifServiceAPIData(props,
 * 																    "test");
 * 		Injector injector = Guice.createInjector(new XLNetsGuiceModule(props,
 * 																	   "test"),
 * 												 new PifServiceGuiceModule(pifServiceApiData));
 *
 * 		PifService pifService = injector.getInstance(PifService.class);
 * </pre>
 *
 * For all this to work a properties file with the following config MUST be provided:
 * <pre class='xml'>
 * 		<pifService>
 * 			<uiConsoleUrl>http://svc.integracion.jakina.ejiedes.net/y31dBoxWAR/appbox</uiConsoleUrl>
 *		</pifService>
 * </pre>
 *
 * IMPORTANT!!!
 * ============
 * A y31.properties file MUST be present at the classpath with the following content (for dev environment):
 *
 *			#establece si se valida el xml contra xlnets o solo se inenta extraer la info
 *			STRICT_SECURITY_CHECKER = false
 *
 *
 *			Y31_JANO_REST_END_POINT=http://svc.intra.integracion.jakina.ejiedes.net/ctxapp
 *
 *			DEFAULT_HADOOP_HOST=hdfs://ejld071:8150
 *
 *			# Base de datos que se esta utilizando (true: MongoDB, false: Oracle)
 *			Y31_JANO_MONGODB=true
 *
 *			MONGO_SERVER = ejld070
 *			MONGO_PORT = 8155
 *			MONGO_DB = y31
 *
 *			#ORACLE_JNDI_NAME= y31.y31DataSource
 *
 *			QUEUE_EVENTS_PROVIDER_URL = t3://wl11vf0013.ejiedes.net:7002,wl11vf0015.ejiedes.net:7002
 *			QUEUE_EVENTS_SECURITY_PRINCIPAL = weblogic11
 *			QUEUE_EVENTS_SECURITY_CREDENTIALS = DESAweblogic11
 *
 *			QUEUE_TRACE_PROVIDER_URL = t3://wl11vf0021.ejiedes.net:7001,wl11vf0023.ejiedes.net:7001
 *			QUEUE_TRACE_SECURITY_PRINCIPAL = weblogic11
 *			QUEUE_TRACE_SECURITY_CREDENTIALS = DESAweblogic11
 *
 *
 *			# parametros que solo deberian estar en softbase, aqui estan de forma provisional hasta que tengamos pif en aplic
 *			JANO_OPERATION_SUFIX=
 *			EVENT_WHAT_VALUE=WRITE
 *
 *			###########################
 *			# pif2
 *			###########################
 *
 *			# superadmin mode; permite navegar por AppBOX y UserBOX
 *			Y31_BOX_SUPERADMIN_ENABLED=false
 *			Y31_EDIT_ON_LINE_BASE_URL=svc.integracion.jakina.ejiedes.net/y31dWebDavUserWAR
 *
 *			# explorer enabled; permite acceder a AppBOX (entornos de pruebas)
 *			Y31_BOX_EXPLORER_ENABLED=true
 *
 *			# si Y31_BOX_EXPLORER está activo, el usuario adminsitrador con el que realizar operaciones
 *			Y31_BOX_ADMIN_APP_USER=Y31
 *
 *			# Y31JanoServiceSecurityTool, carta blanca de acceso 'path;usuario' separado por ','
 *			Y31_WHITELIST=/;t17i,/;y31
 */
@Singleton
@Slf4j
public class PifService {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final PifServiceAPIData _apiData;

	private final XLNetsAPI _xlNetsApi;
	private final Memoized<Document> _xlnetsAuthToken = new Memoized<Document>() {
																		@Override
																		protected Document supply() {
																			return _xlNetsApi.getXLNetsSessionTokenDoc();
																		}
																};

	private final Factory<Y31JanoService> _clientFactory = new Factory<Y31JanoService>() {
																	@Override
																	public Y31JanoService  create() {
																		log.debug("[PifService] > creating the Y31JanoService client");
																		Y31JanoService janoService = null;
																		try {
																			janoService = Y31JanoServiceAbstractFactory.getInstance();
																		} catch (Y31JanoServiceGenericException jEx) {
																			log.error("[PifService] > Error while creating the {} service: {}",
																					  Y31JanoService.class,jEx.getMessage(),jEx);
																		}
																		if (janoService == null) throw new IllegalStateException(Throwables.message("Could NOT create a {} instance!",Y31JanoService.class));
																		return janoService;
																	}
																};
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public PifService(final PifServiceAPIData apiData,
					  final XLNetsAPI xlNetsApi) {
		_apiData = apiData;
		_xlNetsApi = xlNetsApi;
	}

/////////////////////////////////////////////////////////////////////////////////////////
// 	API
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Uploads a file to PIF.
	 * @param filetoUpdload
	 * @param pathTo
	 * @param preserveName
	 * @param timeTolive
	 * @param timeUnit
	 * @return
	 */
	public PifFileInfo uploadFile(final InputStream filetoUpdload,
							   	  final Path pathTo,
							   	  final boolean preserveName,
							   	  final Long timeTolive,final TimeUnit timeUnit) {
		log.info("[PifService] > put file in PIF");
		Y31JanoService service = _clientFactory.create();
		PifFileInfo outputData = null;
		try {
			Document xlNetsAuthToken = _xlnetsAuthToken.get();
			log.info("...got a xlnets auth token; now upload the file");
			//log.info("XLNets token:\n{}",XMLUtils.asString(xlNetsAuthToken));
			Y31AttachmentBean result = service.put(xlNetsAuthToken,
												   filetoUpdload,
												   pathTo.asAbsoluteString(),
												   preserveName,timeUnit.toMillis(timeTolive));
			outputData = new PifFileInfo(MimeTypes.forName(result.getContentType()),
										 FileNameAndExtension.of(result.getFileName()),
										 Path.from(result.getFilePath()),
										 result.getSize());
		} catch(Throwable th) {
			log.error("[PifService] > Error while calling PIF service: {}",th.getMessage(),th);
			throw new IllegalStateException(Throwables.message("[PifService] > Error while calling PIF service: {}",th.getMessage(),th));
		}
		return outputData;
	}
	/**
	 * Downloads a file from PIF.
	 * @param pathFrom
	 * @return
	 */
	public PifFile downloadFile(final Path pathFrom) {
		log.info("[PifService] > get file from PIF");
		PifFile outputData = null;
		Y31JanoService service = _clientFactory.create();
		try {
			Document xlNetsAuthToken = _xlnetsAuthToken.get();
			log.info("...got a xlnets auth token; now download the file");
			//log.info("XLNets token:\n{}",XMLUtils.asString(xlNetsAuthToken));
			InputStream result = service.get(xlNetsAuthToken,
											 pathFrom.asAbsoluteString());
			outputData = new PifFile(pathFrom,
									 result);
		} catch(Throwable th) {
			log.error("[PifService] > Error while calling PIF service: {}",th.getMessage(),th);
			throw new IllegalStateException(Throwables.message("[PifService] > Error while calling PIF service: {}",th.getMessage(),th));
		}
		return outputData;
	}
	/**
	 * Get the info of file from PIF.
	 * @param pathFrom
	 * @return
	 */
	public PifFileInfo getFileInfo(final Path pathFrom) {
		log.info("[PifService] > get file info from PIF");
		PifFileInfo outputData = null;
		Y31JanoService service = _clientFactory.create();
		try {
			Document xlNetsAuthToken = _xlnetsAuthToken.get();
			log.info("...got a xlnets auth token; now get the file info");
			//log.info("XLNets token:\n{}",XMLUtils.asString(xlNetsAuthToken));
			Y31AttachmentBean result = service.info(xlNetsAuthToken,
											  pathFrom.asAbsoluteString());
			outputData = new PifFileInfo(MimeTypes.forName(result.getContentType()),
														   FileNameAndExtension.of(result.getFileName()),
														   Path.from(result.getFilePath()),
														   result.getSize());
		} catch(Throwable th) {
			log.error("[PifService] > Error while calling PIF service: {}",th.getMessage(),th);
			throw new IllegalStateException(Throwables.message("[PifService] > Error while calling PIF service: {}",th.getMessage(),th));
		}
		return outputData;
	}
	/**
	 * Delete a file from PIF.
	 * @param pathFrom
	 * @return
	 */
	public void deleteFile(final Path pathFrom) {
		log.info("[PifService] > delete file from PIF");
		Y31JanoService service = _clientFactory.create();
		try {
			Document xlNetsAuthToken = _xlnetsAuthToken.get();
			log.info("...got a xlnets auth token; now delete the file");
			//log.info("XLNets token:\n{}",XMLUtils.asString(xlNetsAuthToken));
			service.delete(xlNetsAuthToken,
						   pathFrom.asAbsoluteString());
		} catch (Throwable th) {
			log.error("[PifService] > Error while calling PIF service: {}",th.getMessage(),th);
			throw new IllegalStateException(Throwables.message("[PifService] > Error while calling PIF service: {}",th.getMessage(),th));
		}
	}

	/**
	 * Copy a file.
	 * @param pathFrom
	 * @return
	 */
	public PifFileInfo copyFile(final Path pathFrom, final Path pathTo, final boolean preserveName) {
		log.info("[PifService] > delete file from PIF");
		PifFileInfo outputData = null;
		Y31JanoService service = _clientFactory.create();
		try {
			Document xlNetsAuthToken = _xlnetsAuthToken.get();
			log.info("...got a xlnets auth token; now delete the file");
			//log.info("XLNets token:\n{}",XMLUtils.asString(xlNetsAuthToken));
			Y31AttachmentBean result =  service.copy(xlNetsAuthToken, pathFrom.asAbsoluteString(), pathTo.asAbsoluteString(), preserveName);
			outputData = new PifFileInfo(MimeTypes.forName(result.getContentType()),
										 FileNameAndExtension.of(result.getFileName()),
										 Path.from(result.getFilePath()),
										 result.getSize());
		} catch (Throwable th) {
			log.error("[PifService] > Error while calling PIF service: {}",th.getMessage(),th);
			throw new IllegalStateException(Throwables.message("[PifService] > Error while calling PIF service: {}",th.getMessage(),th));
		}
		return outputData;
	}
	/**
	 * Move a file.
	 * @param pathFrom
	 * @return
	 */
	public PifFileInfo moveFile(final Path pathFrom, final Path pathTo, final boolean preserveName) {
		log.info("[PifService] > delete file from PIF");
		PifFileInfo outputData = null;
		Y31JanoService service = _clientFactory.create();
		try {
			Document xlNetsAuthToken = _xlnetsAuthToken.get();
			log.info("...got a xlnets auth token; now delete the file");
			//log.info("XLNets token:\n{}",XMLUtils.asString(xlNetsAuthToken));
			Y31AttachmentBean result =  service.move(xlNetsAuthToken, pathFrom.asAbsoluteString(), pathTo.asAbsoluteString(), preserveName);
			outputData = new PifFileInfo(MimeTypes.forName(result.getContentType()),
										 FileNameAndExtension.of(result.getFileName()),
										 Path.from(result.getFilePath()),
										 result.getSize());
		} catch (Throwable th) {
			log.error("[PifService] > Error while calling PIF service: {}",th.getMessage(),th);
			throw new IllegalStateException(Throwables.message("[PifService] > Error while calling PIF service: {}",th.getMessage(),th));
		}
		return outputData;
	}

	/**
	 * List files from a PIF directory.
	 * @param pathFrom
	 * @return
	 */
	public List<PifFileInfo> listFiles(final Path pathFrom) {
		log.info("[PifService] > list files from PIF");
		List<PifFileInfo> result;
		Y31JanoService service = _clientFactory.create();
		try {
			Document xlNetsAuthToken = _xlnetsAuthToken.get();
			log.info("...got a xlnets auth token; now download the file");
			//log.info("XLNets token:\n{}",XMLUtils.asString(xlNetsAuthToken));
			@SuppressWarnings("unchecked")
			List<Y31AttachmentBean> pifResult = service.list(xlNetsAuthToken, pathFrom.asAbsoluteString());
			if (pifResult.isEmpty()) {
				return null;
			}
			result = new ArrayList<PifFileInfo>();
			for (Iterator<Y31AttachmentBean> iter = pifResult.iterator(); iter.hasNext(); ) {
				Y31AttachmentBean y31File = iter.next();
				PifFileInfo pifFile = new PifFileInfo(MimeTypes.forName(y31File.getContentType()),
													  FileNameAndExtension.of(y31File.getFileName()),
													  Path.from(y31File.getFilePath()),
													  y31File.getSize());
				result.add(pifFile);
			}
		} catch(Throwable th) {
			log.error("[PifService] > Error while calling PIF service: {}",th.getMessage(),th);
			throw new IllegalStateException(Throwables.message("[PifService] > Error while calling PIF service: {}",th.getMessage(),th));
		}
		return result;
	}


}
