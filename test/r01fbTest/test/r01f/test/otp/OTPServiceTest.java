package r01f.test.otp;

import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.cache.DistributedCacheConfig;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.Password;
import r01f.mail.config.JavaMailSenderConfig;
import r01f.mail.config.JavaMailSenderConfigBuilder;
import r01f.model.otp.OTPType;
import r01f.model.otp.dispatch.OTPDispatchRequest;
import r01f.model.otp.dispatch.OTPDispatchRequestBuilder;
import r01f.model.otp.dispatch.OTPDispatchResponse;
import r01f.model.otp.oids.OTPOIDs.OTPOID;
import r01f.model.otp.operations.OTPOperationExecResult;
import r01f.model.otp.request.OTPRequest;
import r01f.model.otp.request.OTPRequestBuilder;
import r01f.model.otp.request.OTPResponse;
import r01f.model.otp.validation.OTPValidationRequestBuilder;
import r01f.model.otp.validation.OTPValidationResponse;
import r01f.service.otp.OTPService;
import r01f.service.otp.OTPServiceGuiceModule;
import r01f.services.latinia.LatiniaServiceAPIData;
import r01f.types.contact.EMail;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.XMLPropertiesGuiceModule;


@Accessors(prefix="_")
@RequiredArgsConstructor
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class OTPServiceTest  {
/////////////////////////////////////////////////////////////////////////////////////////
// GUICE INJECTOR
/////////////////////////////////////////////////////////////////////////////////////////
	static Injector GUICE_INJECTOR = null;

	private static OTPService _getOTPService(){
		return GUICE_INJECTOR.getInstance(OTPService.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JUnit
/////////////////////////////////////////////////////////////////////////////////////////
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			XMLPropertiesForApp appXmlProps = XMLPropertiesBuilder.createForApp(AppCode.forId("z99"))
																	    .notUsingCache();
			XMLPropertiesForAppComponent hzXMLProps = appXmlProps.forComponent("hazelcast");
				log.debug("CFG hzXMLProps {}", hzXMLProps == null);

			DistributedCacheConfig hzCfg = DistributedCacheConfig.createFrom(hzXMLProps);

			
			XMLPropertiesForAppComponent mailSenderXmlProps = appXmlProps.forComponent("mail");
			log.debug("CFG mail {}", mailSenderXmlProps == null);
			JavaMailSenderConfig mailSenderCfg = JavaMailSenderConfigBuilder.createFrom(mailSenderXmlProps);
			
			
			XMLPropertiesForAppComponent latiniaXMLProps = appXmlProps.forComponent("latinia");
			LatiniaServiceAPIData latiniaCfg = LatiniaServiceAPIData.createFrom(latiniaXMLProps);
			
			GUICE_INJECTOR = Guice.createInjector(new XMLPropertiesGuiceModule(),
					                              new OTPServiceGuiceModule(hzCfg,
					                            		  				    mailSenderCfg,
					                            		  					latiniaCfg));
			ServicesBootstrapUtil.startServices(GUICE_INJECTOR);
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
			Throwables.throwUnchecked(ex);
		}
	}
	@AfterClass
	public static void tearDownAfterClass()  {
		// [99]-Tear things down
		try {
			ServicesBootstrapUtil.stopServices(GUICE_INJECTOR);
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
			Throwables.throwUnchecked(ex);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	//@Test @SuppressWarnings("static-method")
	public void testOTPGeneration() {
		OTPService otpService = _getOTPService();
		OTPRequest otpRequest = OTPRequestBuilder.createForApp(AppCode.forId("z99"))
					                                 .ofType(OTPType.ALPHANUMERIC)
					                                 .withSecondOfLife(1000)
					                                 .andMaxNumOfRetries(3)
				                                 .build();


		OTPOperationExecResult<OTPResponse> result = otpService.generate(otpRequest);
		OTPResponse response = result.getOrThrow();
		log.debug("......................OTP OID {}", response.getOtpOID().asString());
		log.debug("......................OTP Request OID {}",response.getOtpRequestOID().asString());

	}
	//@Test @SuppressWarnings("static-method")
	public void testOTPGenerationAndValidation() {
		OTPService otpService = _getOTPService();
		OTPRequest otpRequest = OTPRequestBuilder.createForApp(AppCode.forId("z99"))
					                                 .ofType(OTPType.DIGITS_ONLY)
					                                 .withSecondOfLife(1000)
					                                 .andMaxNumOfRetries(3)
					                                 .withLength(4)
				                                 .build();


		OTPOperationExecResult<OTPResponse> result = otpService.generate(otpRequest);
		OTPResponse response = result.getOrThrow();
		OTPOID otpOid = response.getOtpOID();
		log.debug("......................OTP OID {}", response.getOtpOID().asString());
		log.debug("......................OTP Request OID {}",response.getOtpRequestOID().asString());

        for ( int i = 0 ; i <= 4 ; i ++) {
			OTPOperationExecResult<OTPValidationResponse> validationResult = otpService.validate(OTPValidationRequestBuilder.createForApp(AppCode.forId("z99"))
																										                     .validate(otpOid)
																										                     .usingValue(Password.forId("not valid value"))
																								                           .build());
			if ( validationResult.hasSucceeded()) {
				log.debug("Is valid {}",validationResult.asOperationExecOK().getOrThrow().isValidOtp());
				log.debug("Number of Retries {}",validationResult.asOperationExecOK().getOrThrow().getOtpValidationRetriesToExpire());
			} else {
				log.debug("Validation Not Suceed {}",validationResult.asOperationExecError().debugInfo());
			}

        }
	}


    //@Test @SuppressWarnings("static-method")
	public void testOTPDispatch() {
		OTPService otpService = _getOTPService();
		OTPRequest otpRequest = OTPRequestBuilder.createForApp(AppCode.forId("z99"))
					                                 .ofType(OTPType.ALPHANUMERIC)
					                                 .withSecondOfLife(1000)
					                                 .andMaxNumOfRetries(3)
					                                 .withLength(45)
				                                 .build();

		OTPOperationExecResult<OTPResponse> result = otpService.generate(otpRequest);
		OTPResponse response = result.getOrThrow();
		log.debug("......................OTP OID {}", response.getOtpOID().asString());
		OTPDispatchRequest otpDispatchRequest = OTPDispatchRequestBuilder.createForApp(AppCode.forId("z99"))
														                 .dispatch(response.getOtpOID())
														                 .usingMail()
															                 .to(EMail.of("i-olabarria@ejie.eus"))
															                 .withSubject("OTP testOTPDispatch")
														                 .build();

		otpService.dispatch(otpDispatchRequest);
	}

	/*@Test @SuppressWarnings("static-method")
	public void testURI() {
		
		
		Url _thirdPartyProviderUrl = Url.from("http://www.google.es");
		Url url = _thirdPartyProviderUrl.joinWith(UrlQueryString.fromParams(
					                                   UrlQueryStringParam.of("subject","Esto es una prueba"),
													   UrlQueryStringParam.of("subject2","test")
												   ));
		
		
		String toStrn= UrlQueryString.fromParams(
					                 UrlQueryStringParam.of("subject","Esto es una prueba"),
									 UrlQueryStringParam.of("subject2","test")
							     ).toString()			;
		System.out.println("\n\n\n >>>>>>>>>>>>THE FROM PARAMS" +
				            toStrn);

	
	
	}*/
	@Test @SuppressWarnings("static-method")
	public void testAllServicesOfOTP() {

		/////////////////////////////// 1º GENERATE OTP
		OTPService otpService = _getOTPService();
		OTPRequest otpRequest = OTPRequestBuilder.createForApp(AppCode.forId("z99"))
					                                 .ofType(OTPType.ALPHANUMERIC)
					                                 .withSecondOfLife(10000)
					                                 .andMaxNumOfRetries(3)
					                                 .withLength(8)
				                                  .build();

		Scanner keyboard = new Scanner(System.in);
		String sendOTPto = null;
		//for (int i = 0 ; i< 2; i++) {
		OTPOperationExecResult<OTPResponse> result = otpService.generate(otpRequest);
		OTPResponse response = result.getOrThrow();
		OTPOID otpOid = response.getOtpOID();
		log.debug("......................OTP OID {}", response.getOtpOID().asString());
		log.debug("......................OTP Request OID {}",response.getOtpRequestOID().asString());

		/////////////////////////////// 2º DISPATCH OTP TO USER.



	        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Write mail to send otp: ");
	        sendOTPto = keyboard.nextLine();

			OTPDispatchRequest otpDispatchRequest = OTPDispatchRequestBuilder.createForApp(AppCode.forId("z99"))
															                 .dispatch(response.getOtpOID())
															                 .usingMail()
																                 .to(EMail.of(sendOTPto))
																                 .withSubject("OTP Dispatch")
															                 .build();
		 OTPOperationExecResult<OTPDispatchResponse> distpatchResponse =  otpService.dispatch(otpDispatchRequest);
	//	}

         log.debug("\n\n Dispatch OTP Response Result suceeded : {}",distpatchResponse.hasSucceeded() );

         if (distpatchResponse.hasSucceeded()) {
			/////////////////////////////// 3º VALIDATE USER INSERTED OTP
	        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Insert received OTP :");
	        String userInsertedOTP = keyboard.nextLine();
	        keyboard.close();
			OTPOperationExecResult<OTPValidationResponse> validationResult = otpService.validate(OTPValidationRequestBuilder.createForApp(AppCode.forId("z99"))
																										                       .validateFor(EMail.of(sendOTPto))
																										                       .usingValue(Password.forId(userInsertedOTP))
																								                           .build());
			if ( validationResult.hasSucceeded()) {
				log.debug("Is valid {}",validationResult.asOperationExecOK().getOrThrow().isValidOtp());
				log.debug("Number of Retries {}",validationResult.asOperationExecOK().getOrThrow().getOtpValidationRetriesToExpire());
			} else {
				log.debug("Validation Not Suceed {}",validationResult.asOperationExecError().debugInfo());
			}

		 } else {
			 	log.error("Error sending  OTP {}", distpatchResponse.getDetailedMessage());
		 }
	}


	//@Test @SuppressWarnings("static-method")
	public void testAllServicesOfOTPWithoutOTPId() {

		/////////////////////////////// 1º GENERATE OTP
		OTPService otpService = _getOTPService();
		OTPRequest otpRequest = OTPRequestBuilder.createForApp(AppCode.forId("z99"))
					                                 .ofType(OTPType.ALPHANUMERIC)
					                                 .withSecondOfLife(10000)
					                                 .andMaxNumOfRetries(3)
					                                 .withLength(8)
				                                  .build();

		OTPOperationExecResult<OTPResponse> result = otpService.generate(otpRequest);
		OTPResponse response = result.getOrThrow();

		log.debug("......................OTP OID {}", response.getOtpOID().asString());
		log.debug("......................OTP Request OID {}",response.getOtpRequestOID().asString());

		/////////////////////////////// 2º DISPATCH OTP TO USER.
		Scanner keyboard = new Scanner(System.in);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Write mail to send otp: ");
        String sendOTPto = keyboard.nextLine();

		OTPDispatchRequest otpDispatchRequest = OTPDispatchRequestBuilder.createForApp(AppCode.forId("z99"))
														                 .dispatch(response.getOtpOID())
														                 .usingMail()
															                 .to(EMail.of(sendOTPto))
															                 .withSubject("OTP Dispatch")
														                 .build();
		otpService.dispatch(otpDispatchRequest);
		/////////////////////////////// 3º VALIDATE USER INSERTED OTP
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Insert received OTP :");
        String userInsertedOTP = keyboard.nextLine();
        keyboard.close();
		OTPOperationExecResult<OTPValidationResponse> validationResult = otpService.validate(OTPValidationRequestBuilder.createForApp(AppCode.forId("z99"))
																										                     .validateFor(EMail.of(sendOTPto))
																										                     .usingValue(Password.forId(userInsertedOTP))
																							                           .build());
		if ( validationResult.hasSucceeded()) {
			log.debug("Is valid {}",validationResult.asOperationExecOK().getOrThrow().isValidOtp());
			log.debug("Number of Retries {}",validationResult.asOperationExecOK().getOrThrow().getOtpValidationRetriesToExpire());
		} else {
			log.debug("Validation Not Suceed {}",validationResult.asOperationExecError().debugInfo());
		}
	}







}
