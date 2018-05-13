package r01f.file;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.UserGroupCode;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.Path;
import r01f.util.types.Dates;
import r01f.util.types.Strings;

@Accessors(prefix="_")
public abstract class FilePropertiesBase 
		   implements FileProperties {

	private static final long serialVersionUID = 566716441791122332L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="path")
	@Getter @Setter protected Path _path;
	
	@MarshallField(as="symlink")
	@Getter @Setter protected Path _symLink;
	
	@MarshallField(as="isFolder",
				  whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected boolean _folder;
	
	@MarshallField(as="size",
				  whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected long _size;
	
	@MarshallField(as="createTimeStamp",
				  whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected long _createTimeStamp;
	
	@MarshallField(as="modificationTimeStamp",
				  whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected long _modificationTimeStamp;
	
	@MarshallField(as="accessTimeStamp",
				  whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected long _accessTimeStamp;
	
	@MarshallField(as="owner",
				  whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected UserCode _owner;
	
	@MarshallField(as="group",
				  whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected UserGroupCode _group;
	
	@MarshallField(as="permission")
	@Getter @Setter protected FilePermission _permission;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isSymLink() {
		return this.getSymLink() != null;
	}
	@Override
	public boolean isFile() {
		return !this.isFolder();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getSizeFormatted() {
		return FilePropertiesBase.formatSize(_size);
	}
	@Override
	public Date getCreateDate() {
		return _dateFromTimeStamp(this.getCreateTimeStamp());
	}
	@Override
	public Date getModificationDate() {
		return _dateFromTimeStamp(this.getModificationTimeStamp());
	}
	@Override
	public Date getAccessDate() {
		return _dateFromTimeStamp(this.getAccessTimeStamp());
	}
	private static Date _dateFromTimeStamp(final long ts) {
		return ts > 0 ? Dates.fromEpochTimeStamp(ts)
					  : null;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Size units, from bytes to yotabytes.
     */
    public static final String[] SIZE_FORMAT_BYTES = {" b"," Kb"," Mb"," Gb"," Tb"," Pb"," Eb"," Zb"," Yb"};
	/**
     * Returns the file size with it's unit
     * <ul>
     * 		<li>If the file size is less than 1024 bytes: 'x b.'</li>
     * 		<li>If the file size is between 1024 bytes and 1048576 bytes :'x Kb.'</li>
     * 		<li>If the file size is between 1048576 bytes and 1073741824 bytes : 'x Mb.'</li>
     * 		<li>If the file size is greater than 1073741824 bytes: 'x Gb.'</li>
     * </ul>
     * @param fileBytes file size in bytes
     * @return the formatted file size 
     */
    public static String formatSize(final long fileBytes) {
        if (fileBytes <= 0) {
            return "";
        }
        // bytes
        if (fileBytes < 1024) {
            return fileBytes + SIZE_FORMAT_BYTES[0];
        }
        // incrementing "letter" while value >1023
        int i = 1;
        double d = fileBytes;
        while ((d = d / 1024) > (1024-1) ) {
            i++;
        }

        // remove symbols after coma, left only 2:
        /*long l = (long) (d * 100);
        d = (double) l / 100;*/

        d = Math.round(d*Math.pow(10,2))/Math.pow(10,2);

        if (i < SIZE_FORMAT_BYTES.length) {
            return d + SIZE_FORMAT_BYTES[i];
        }
        // if it reach here the value is big
        return String.valueOf(fileBytes);
    }	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("      Path : {}\n" +
							 	  "    symlink: {}\n" +
							 	  "   isFolder: {}\n" +
								  "       size: {}\n" +
							 	  " modif time: {}\n" + 
								  "access time: {}\n" + 
							 	  "      owner: {}\n" +
								  "      group: {}\n" + 
							 	  " permission: {}",
							 	  this.getPath(),
							 	  this.isSymLink() ? this.getSymLink() : "false",
							 	  this.isFolder(),
							 	  this.getSize(),
							 	  this.getModificationDate() != null ? Dates.formatAsISO8601(this.getModificationDate()) : null,
							 	  this.getAccessDate() != null ? Dates.formatAsISO8601(this.getAccessDate()) : null,
							 	  this.getOwner(),
							 	  this.getGroup(),
							 	  this.getPermission() != null ? this.getPermission().debugInfo() : "null");
	}
}
