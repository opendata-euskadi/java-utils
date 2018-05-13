package r01f.aspects.core.freezable;


import java.lang.reflect.Field;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;

import r01f.aspects.core.util.ObjectsHierarchyModifier;
import r01f.aspects.core.util.ObjectsHierarchyModifier.StateModifierFunction;
import r01f.aspects.interfaces.freezable.Freezable;

/**
 * Utilidad para "congelar" una jerarqu�a de objetos.
 * Utiliza reflection para recorrer la jerarqu�a de objetos y "congelar" aquellos que son Freezable
 */
public class Freezer {
	/**
	 * Predicado para excluir algunos fields en los m�todos que utilizan changeObjectHierarchyState
	 * para por ejemplo comenzar a controlar cambios en el estado o ver si el objeto est� sucio
	 */
	static final Predicate<Field> _fieldAcceptCriteria = new Predicate<Field>() {
																@Override
																public boolean apply(final Field f) {
																	if (f.getDeclaringClass().getPackage().getName().startsWith("java.lang")) return false;	
																	if (f.getDeclaringClass().getPackage().getName().startsWith("com.google")) return false;
																	if (f.getName().startsWith("ajc$")) return false; 
																	if (f.getName().startsWith("_frozen")) return false;
																	return true;
																}
														};
	/**
	 * Clase para modificar el estado de un objeto Freezable
	 */
	private static class FreezeStateModifierFunction implements StateModifierFunction<Freezable> {
		private final boolean _freeze;
		public FreezeStateModifierFunction(final boolean freeze) {
			_freeze = freeze;
		}
		@Override
		public void changeState(Freezable obj) {
			obj.setFrozen(_freeze);
		}
	}
	
	/**
	 * Congela un objeto y sus objetos dependientes
	 * @param freezableObj el objeto freezable
	 */
	@SuppressWarnings("serial")
	public static void freeze(Freezable freezableObj) {
		ObjectsHierarchyModifier.<Freezable>changeObjectHierarchyState(freezableObj,new TypeToken<Freezable>() {},
																	   new FreezeStateModifierFunction(true),
																	   true,		// congelar toda la jerarqu�a de objetos
																	   _fieldAcceptCriteria);
	}
	/**
	 * Descongela un objeto y sus objetos dependientes
	 * @param freezableObj el objeto freezable
	 */
	@SuppressWarnings("serial")
	public static void unFreeze(Freezable freezableObj) {
		ObjectsHierarchyModifier.<Freezable>changeObjectHierarchyState(freezableObj,new TypeToken<Freezable>() {},
																	   new FreezeStateModifierFunction(false),
																	   true,		// congelar toda la jerarqu�a de objetos
																	   _fieldAcceptCriteria);
	}
}
