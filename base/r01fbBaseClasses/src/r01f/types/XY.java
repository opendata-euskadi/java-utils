package r01f.types;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Coordinates
 */
@Immutable
@MarshallType(as="xy")
@Accessors(prefix="_")
@RequiredArgsConstructor
public class XY 
  implements Serializable {

	private static final long serialVersionUID = -3175355518015641559L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS 
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="x",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final long _x;
	
	@MarshallField(as="y",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final long _y;
}
