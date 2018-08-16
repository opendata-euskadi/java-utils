package r01f.service.otp;

import org.springframework.mail.javamail.JavaMailSender;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import lombok.EqualsAndHashCode;
import r01f.cache.DistributedCacheConfig;
import r01f.cache.DistributedCacheGuiceModule;
import r01f.mail.JavaMailSenderProvider;
import r01f.mail.config.JavaMailSenderConfig;
import r01f.service.otp.delegate.OTPServiceForDistpachImpl;
import r01f.service.otp.delegate.OTPServiceForGenerationImpl;
import r01f.service.otp.delegate.OTPServiceForValidationImpl;
import r01f.service.otp.delegate.OTPServiceImpl;
import r01f.services.latinia.LatiniaServiceAPIData;
import r01f.services.latinia.LatiniaServiceGuiceModule;

@EqualsAndHashCode				// This is important for guice modules
  public class OTPServiceGuiceModule
implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final JavaMailSenderConfig _mailSenderConfig;
	private final LatiniaServiceAPIData _latiniaCfg;
	private final DistributedCacheConfig _distributedCacheCfg;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public OTPServiceGuiceModule(final DistributedCacheConfig distributedCacheCfg,
								 final JavaMailSenderConfig mailSenderConfig,
								 final LatiniaServiceAPIData latiniaConfig) {
		_distributedCacheCfg = distributedCacheCfg;
		_mailSenderConfig = mailSenderConfig;
		_latiniaCfg = latiniaConfig;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MODULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// Install
		binder.install(new LatiniaServiceGuiceModule(_latiniaCfg));

		binder.install(new DistributedCacheGuiceModule(_distributedCacheCfg));

		// OTP Services Binding
	    binder.bind(OTPServiceForGeneration.class).to(OTPServiceForGenerationImpl.class);
	    binder.bind(OTPServiceForValidation.class).to(OTPServiceForValidationImpl.class);
	    binder.bind(OTPServiceForDistpach.class).to(OTPServiceForDistpachImpl.class);
	    binder.bind(OTPService.class).to(OTPServiceImpl.class);

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NOTIFIER SERVICE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides a {@link JavaMailSender} implementation
	 * @param props
	 * @return
	 */
	/*@Provides @Singleton	// creates a single instance of the java mail sender
	JavaMailSender _provideJavaMailSender(@XMLPropertiesComponent("mail") final XMLPropertiesForAppComponent props) {
		JavaMailSenderProvider javaMailSenderProvider = new JavaMailSenderProvider(_mailSenderConfig);
		JavaMailSender outJavaMailSender = javaMailSenderProvider.get();
		return outJavaMailSender;
	}*/
	
	@Provides @Singleton	// creates a single instance of the java mail sender
	JavaMailSender _provideJavaMailSender() {
		JavaMailSenderProvider javaMailSenderProvider = new JavaMailSenderProvider(_mailSenderConfig);
		JavaMailSender outJavaMailSender = javaMailSenderProvider.get();
		return outJavaMailSender;
	}


}
