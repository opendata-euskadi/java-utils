package r01f.model;

import java.io.Serializable;

/**
 * A model object summary used when returning persistence find results
 * @param <M>
 */
public interface SummarizedModelObject<M extends ModelObject>
		 extends Serializable {
	/**
	 * Return the model object type
	 * @return
	 */
	public Class<M> getModelObjectType();
}
