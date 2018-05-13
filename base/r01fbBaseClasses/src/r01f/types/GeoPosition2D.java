package r01f.types;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Coordenadas de una posici�n en dos dimensiones: latitud y longitud
 * <code>
 * 		GeoPosition2D geo = GeoPosition2D.usingStandard(GOOGLE)
 * 										 .setLocation(lat,lon);
 * </code>
 */
@Immutable
@MarshallType(as="geoPosition2D")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class GeoPosition2D
  implements Serializable {
	
	private static final long serialVersionUID = 3126318415213511386L;
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="standard",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoPositionStandad _standard;
	
	@MarshallField(as="latitude",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private double _latitude;
	
	@MarshallField(as="longitude",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private double _longitude;
/////////////////////////////////////////////////////////////////////////////////////////
//  ENUM DE ESTANDARES DE MEDICION DE lat/long
/////////////////////////////////////////////////////////////////////////////////////////
	public static enum GeoPositionStandad {
		GOOGLE,
		ISO;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API: FACTOR�A
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoPosition2DWithoutCoords usingStandard(final GeoPositionStandad standard) {
		return new GeoPosition2DWithoutCoords(standard);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API CLASE AUXILIAR
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor
	public static class GeoPosition2DWithoutCoords {
		private final GeoPositionStandad _theStandard;
		public GeoPosition2D setLocation(final double latitude,final double longitude) {
			return new GeoPosition2D(_theStandard,latitude,longitude);
		}
	}
}
