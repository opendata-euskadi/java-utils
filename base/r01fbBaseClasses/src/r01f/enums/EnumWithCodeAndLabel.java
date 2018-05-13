package r01f.enums;




/**
 * Operaciones b�sicas con un Enum que tiene c�digo y descripci�n 
 * @see EnumWithCodeAndLabelWrapper
 * @param <T> el Enum concreto
 */
public interface EnumWithCodeAndLabel<C,T> 
         extends EnumWithCode<C,T> {
	/**
	 * Devuelve la descripcion del elemento del enum
	 * @return
	 */
	public String getLabel(); 
	/**
	 * Combrueba si el elemento puede ser asignado a partir de un label
	 * @param label el label
	 * @return true si se puede asignar, false en caso contrario
	 */
	public boolean canBeFrom(String label);
}
