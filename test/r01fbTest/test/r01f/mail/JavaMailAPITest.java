package r01f.mail;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import r01f.mail.simple.SimpleJavaMailSender;
import r01f.mime.MimeType;
import r01f.types.Path;
import r01f.types.contact.EMail;
import r01f.types.url.Host;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

public class JavaMailAPITest { 

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTANTES
/////////////////////////////////////////////////////////////////////////////////////////
	// Tipos de contenido del body del mensaje
	public static final String CONTENT_TYPE_HTML = "text/html";
	public static final String CONTENT_TYPE_TEXT = "text/plain";

/////////////////////////////////////////////////////////////////////////////////////////
//MIEMBROS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	* Nombre del host smtp
	*/
//	private static String _smtpHost = "ejgvmail.ejgvdns";
	private static String _smtpHost = "proxy2";
	/**
	* Tiempo de espera destinado en la obtencion de una sesion de correo
	*/
	private static int _timeout = 5000;
	
	/**
	* true o false en funcion de si se quiere debug o no
	*/
	private static String _debug = "false";

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {	
		try {
	    	String to = "UNAGOMEZ@pge.elkarlan.euskadi.eus";
			String from = "a-lara@ejie.eus";
			String subject = "Hola MUNDO";
			String messageText = "esto es una pruuuuuuuuuuuuuueba de envio de email";
			Path[] attachmentPaths = _attachmentFilesPaths(null);;
			
			SimpleJavaMailSender mailSender = new SimpleJavaMailSender(Host.of(_smtpHost),_timeout,Boolean.parseBoolean(_debug));
	    	Collection<EMail> tos = _emailCollectionFrom(to);

	    	mailSender.sendMessage(EMail.of(from),
	    						   tos,
	    						   subject,
	    						   new MimeType(CONTENT_TYPE_HTML),
	    						   messageText,
	    						   attachmentPaths);

			
			
			
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}

	private static Path[] _attachmentFilesPaths(final String[] attachListFileNames) {
		return CollectionUtils.hasData(attachListFileNames)
						? FluentIterable.from(attachListFileNames)
							   .transform(new Function<String,Path>() {
													@Override
													public Path apply(final String attach) {
														return Path.from(attach);
													}
							   			  })
							   .toArray(Path.class)
						: null;
		 }
	
	private static Collection<EMail> _emailCollectionFrom(final String emails) {
		return Strings.isNOTNullOrEmpty(emails) 
					? FluentIterable.from(emails.split(";"))
							  .transform(new Function<String,EMail>() {
												@Override
												public EMail apply(final String email) {
													return EMail.of(email);
												}
							  			 })
							  .toList()
						: null;
		 }

}
