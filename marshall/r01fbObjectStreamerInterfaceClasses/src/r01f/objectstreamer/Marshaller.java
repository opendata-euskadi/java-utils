package r01f.objectstreamer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.google.common.reflect.TypeToken;

public interface Marshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//	MAIN METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	public MarshallerReadFromStream forReading();
	public MarshallerWriteToStream forWriting();

/////////////////////////////////////////////////////////////////////////////////////////
//	READ 
/////////////////////////////////////////////////////////////////////////////////////////	
	public interface MarshallerReadFromStream {
		public <T> T fromXml(final InputStream is,
							 final Class<T> type);
		
		public <T> T fromXml(final InputStream is,
							 final TypeToken<T> typeToken);
		
		public <T> T fromXml(final String xml,final Charset charset,
							 final Class<T> type);
		
		public <T> T fromXml(final String xml,
							  final Class<T> type);
		
		public <T> T fromXml(final String xml,final Charset charset,
							 final TypeToken<T> typeToken);
		
		public <T> T fromXml(final String xml,
							 final TypeToken<T> typeToken);
	
		public <T> T fromJson(final InputStream is,
							  final Class<T> type);
		
		public <T> T fromJson(final InputStream is,
							  final TypeToken<T> typeToken);
		
		public <T> T fromJson(final String json,final Charset charset,
							  final Class<T> type);
		
		public <T> T fromJson(final String json,
							  final Class<T> type);		
		
		public <T> T fromJson(final String json,final Charset charset,
							  final TypeToken<T> typeToken);
		
		public <T> T fromJson(final String json,
							  final TypeToken<T> typeToken);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WRITE	
/////////////////////////////////////////////////////////////////////////////////////////	
	public interface MarshallerWriteToStream {
		public <T> void toXml(final T obj,
							  final OutputStream os);
		
		public <T> String toXml(final T obj,final Charset charset);
		
		public <T> String toXml(final T obj);
		
		public <T> void toJson(final T obj,
							   final OutputStream os);
		
		public <T> String toJson(final T obj,final Charset charset);
		
		public <T> String toJson(final T obj);
	}
}
