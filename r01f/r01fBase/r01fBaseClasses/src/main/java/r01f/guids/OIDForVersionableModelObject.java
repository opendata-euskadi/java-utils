package r01f.guids;

/**
 * Interface for composite oids of versionable model objects
 * @param <O>
 */
public interface OIDForVersionableModelObject 
		 extends CompositeOID {
	/**
	 * @return the version independent oid
	 */
	public VersionIndependentOID getOid();
//	/**
//	 * Sets the version independent oid
//	 * @param versionIndependentOid
//	 */
//	public void setOid(final VersionIndependentOID versionIndependentOid);
	/**
	 * @return the version
	 */
	public VersionOID getVersion();
//	/**
//	 * @param versionOid
//	 */
//	public void setVersion(final VersionOID versionOid);
}
