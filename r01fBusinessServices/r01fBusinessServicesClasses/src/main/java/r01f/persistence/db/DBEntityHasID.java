package r01f.persistence.db;

public interface DBEntityHasID 
	     extends DBEntityFacet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public String getId();
	public void setId(final String id);
}
