package r01f.guid.dispenser;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

import r01f.guid.dispenser.GUIDDispenser;
import r01f.guid.dispenser.GUIDDispenserDef;
import r01f.guid.dispenser.HighLowBBDDGUIDPersist;
import r01f.guid.dispenser.HighLowGUIDPersist;
import r01f.guid.dispenser.HighLowMemoryGUIDPersist;
import r01f.guid.dispenser.GuiceManagedHighLowGUIDDispenser.HighLowGUIDDispenserFactory;
import r01f.guid.dispenser.GuiceManagedJavaGUIDDispenser.JavaGUIDDispenserFactory;
import r01f.guid.dispenser.GuiceManagedSimpleGUIDDispenser.SimpleGUIDDispenserFactory;

/**
 * GUICE Module form the {@link GUIDDispenser}
 * 
 * IMPORTANT!	If a new dispenser type is created, is mandatory to add HERE a new binding
 * 
 * HOW THIS WORKS:
 * --------------
 * The {@link GUIDDispenserManager} uses the dispenser definition in XML format which it's transformed to a {@link GUIDDispenserDef} java object
 * In the dispenser definition, the 'factoryBindingId' properties states what the dispenser type is to be provided:
 * <pre class='brush:xml'>
 *			<sequence name='default'>
 *				<factoryBindingId>simpleGUIDDispenser</factoryBindingId>
 *				<uniqueId>desa</uniqueId>   <!-- loc=lc,sb_des=sd,sb_pru=sp,des=ds,pru=pr,pro=pd -->
 * 				<length>36</length>
 *			</sequence>
 * </pre>
 * The concrete guid factory to be binded is ONLY know at run-time (NOT at compile-time) so GUICE cannot be used in the usual way (static bindings)
 * <pre class='brush:java'>
 *		binder.bind(GuidDispenser.class).to(what to bind ??)
 * </pre>
 *	
 * The solution is to use {@link MapBinder}, which allows to bind IDs to instances in a way that:
 * <ul>
 *		<li>
 *		The mapping could be something like:
 *			<pre class='brush:java'>
 *				MapBinder<String,GUIDDispenserFactory> guidDispenserFactoryMapBinder = MapBinder.newMapBinder(binder,
 *																											  String.class,GUIDDispenserFactory.class);
 *				guidDispenserFactoryMapBinder.addBinding("simpleGUIDDispenser").to(SimpleGUIDDispenserFactory.class).in(Singleton.class);
 *				guidDispenserFactoryMapBinder.addBinding("highLowGUIDDispenser").to(HighLowGUIDDispenserFactory.class).in(Singleton.class);
 *			</pre>
 *		</li>
 *		<li>
 *		At run-time, using the GUIDDispenserID, a concrete instance can be obtained using a GUIDDispenser objects factory 
 *			<pre class='brush:java'>
 *				String guidDispenserImplId = "simpleGUIDDispenser";
 *				Key<Map<String,GUIDDispenserFactory>> guidDispenserFactoriesMap = Key.get(new TypeLiteral<Map<String,GUIDDispenserFactory>>() {});
 *				GUIDDispenserFactory factory = injector.getInstance(guidDispenserFactoriesMap)
 *													   .get(guidDispenserImplId);
 *				outDispenser = factory.createDispenser(...);
 *			</pre>
 *		    ... but this implementation requires to have access to the GUICE injector from {@link GUIDDispenserManager} 
 *			    (that's NOT a good design option), also, the code is complex, so a better option is let GUICE inject the 
 *				Map of {@link GUIDDispenser} factories to {@link GUIDDispenserManager}:
 *			<pre class='brush:java'>
 *				public class GUIDDispenserManager {
 *					@Inject
 *					private final Map<String,GUIDDispenserFactory> _guidDispensersFactories;
 *					...
 *				}
 *			</pre>
 */
public class GUIDDispenserGuiceModule
  implements Module {
	
	@Override
	public void configure(Binder binder) {
		binder.bind(GUIDDispenserManager.class)
			  .in(Singleton.class);
		
		// The following config is complex because two GUICE mechanisms are involved:
		//		A.- MapBinder to get a GUIDDispenserFlavourFactory at run-time by means of the factory ID 
		//			(ej: simpleGUIDDispenser o highLowGUIDDispenser)
		//
		//		B.- AssistedInject to hand the GUIDDispenserDef parameter to the GUIDDispenserFlavourFactory at injection time  
		//			NOTE:	A GUIDDispenserDef object must be provided to the GUIDDispenser in the constructor, but this GUIDDispenserDef
		//					is only know at run-time
		//					... also it could be necessary to inject another object to the GUIDDispenser
		// So it's necessary to:
		//		[1] - Config AssistedInject to create the GUIDDispenser using a GUIDDispenserDef-based constructor
		//		[2] - Config MapBinder to bind the GUIDDispenserFlavourFactory from the previous step [1] to any identifier

		
		// [1] AssistedInject config: it's used to hand the dispenser definition (GUIDDispenserDef) at injection time
		//	   AssistedInject works by letting Guice implements GUIDDispenserFactory interface at run-time 
		//	   It's important to note tahe every GUIDDispenser factory (SimpleGUIDDispenserFactory, HighLowGUIDDispenserFactory...)
		//	   are NOT real types; they're only INTERFACES.
		//	   Guice FactoryModuleBuilder is in charge of building a type that implements GUIDDispenser interface
		// -- DO NOT MOVE --
		Module assistedModuleForJavaGUIDDispenser = new FactoryModuleBuilder().implement(GUIDDispenser.class,
																						 GuiceManagedJavaGUIDDispenser.class)
												 		   						.build(JavaGUIDDispenserFactory.class);
		Module assistedModuleForSimpleGUIDDispenser = new FactoryModuleBuilder().implement(GUIDDispenser.class,
																						   GuiceManagedSimpleGUIDDispenser.class)
												 		   						.build(SimpleGUIDDispenserFactory.class);
		Module assistedModuleForHighLowGUIDDispenser = new FactoryModuleBuilder().implement(GUIDDispenser.class,
																							GuiceManagedHighLowGUIDDispenser.class)
												 		   						 .build(HighLowGUIDDispenserFactory.class);
		binder.install(assistedModuleForJavaGUIDDispenser);
		binder.install(assistedModuleForSimpleGUIDDispenser);	
		binder.install(assistedModuleForHighLowGUIDDispenser);
		
		// [2] MapBinders config: it's used to get the GUIDDispenserFlavourFactory at run-time from it's id
		//		- MapBinder of GUIDs factories to get the guidDispenser at run-time
		MapBinder<String,GUIDDispenserFlavourFactory> guidDispenserFactoryMapBinder = MapBinder.newMapBinder(binder,String.class,GUIDDispenserFlavourFactory.class);
		guidDispenserFactoryMapBinder.addBinding("javaGUIDDispenser")
									 .to(JavaGUIDDispenserFactory.class)
									 .in(Singleton.class);
		guidDispenserFactoryMapBinder.addBinding("simpleGUIDDispenser")
									 .to(SimpleGUIDDispenserFactory.class)
									 .in(Singleton.class);
		guidDispenserFactoryMapBinder.addBinding("highLowGUIDDispenser")
									 .to(HighLowGUIDDispenserFactory.class)
									 .in(Singleton.class);
		
		// [3] MapBinders config: it's used to get the HighLowGUIDPersist instance at run-time from it's id
		//	   (this is only used in HighLowGUIDDispensers)
		//		- Mapbinder of HighKeys persistence to get the HighLowGUIDDispenser at run-time
		MapBinder<String,HighLowGUIDPersist> highLowGUIDPersistMapBinder = MapBinder.newMapBinder(binder,String.class,HighLowGUIDPersist.class);
		highLowGUIDPersistMapBinder.addBinding("inMemoryHighKeyPersist")
								   .to(HighLowMemoryGUIDPersist.class);
		highLowGUIDPersistMapBinder.addBinding("bbddHighKeyPersist")
								   .to(HighLowBBDDGUIDPersist.class);
	}
}
