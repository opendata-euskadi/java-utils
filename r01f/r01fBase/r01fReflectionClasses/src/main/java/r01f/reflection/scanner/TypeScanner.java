package r01f.reflection.scanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * TypeScanner is used to locate classes that are available in the/a class path and meet
 * arbitrary conditions. 
 * The two most common conditions are that:
 * <ul>
 * 		<li> class implements/extends</li>
 * 		<li>another class, or that is it annotated with a specific annotation.</li>
 * </ul> 
 * However, through the use of the {@link TypeScannerTest} class it is possible to search using arbitrary conditions.
 *
 * A ClassLoader is used to locate all locations (directories and jar files) in the class
 * path that contain classes within certain packages, and then to load those classes and
 * check them. By default the ClassLoader returned by {@code Thread.currentThread().getContextClassLoader()} is used, 
 * but this can be overridden by calling {@link #setClassLoader(ClassLoader)} prior to invoking any of the {@code find()}
 * methods.
 *
 * General searches are initiated by calling the {@link #find(TypeScannerTest, String)} method and supplying
 * a package name and a Test instance. 
 * This will cause the named package <b>and all sub-packages</b> to be scanned for classes that meet the test. 
 * There are also utility methods for the common use cases of scanning multiple packages for extensions of particular classes, 
 * or classes annotated with a specific annotation.
 *
 * The standard usage pattern for the ResolverUtil class is as follows:
 * <pre class='brush:java'>
 * 		TypeScanner<MyType> scanner = new TypeScanner<MyType>();
 * 		scanner.findImplementation(MyType.class,
 * 								   "com.mypackage1","com.mypackage2");
 * 		Collection<MyType> types = scanner.getClasses();
 * </pre>
 * 
 * @see Stripes framework: https://github.com/StripesFramework/stripes/blob/master/stripes/src/main/java/net/sourceforge/stripes/util/ResolverUtil.java
 */

@Slf4j
public class TypeScanner<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * A simple interface that specifies how to test classes to determine if they
     * are to be included in the results produced by the ResolverUtil.
     */
    public static interface TypeScannerTest {
        /**
         * Will be called repeatedly with candidate classes. Must return True if a class
         * is to be included in the results, false otherwise.
         */
        boolean matches(final Class<?> type);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * A Test that checks to see if each class is assignable to the provided class. Note
     * that this test will match the parent type itself if it is presented for matching.
     */
    public static class IsA 
    		 implements TypeScannerTest {
    	
        private Class<?> _parent;

        /** 
         * Constructs an IsA test using the supplied Class as the parent class/interface. 
         */
        public IsA(final Class<?> parentType) { _parent = parentType; }
        
        @Override
		public boolean matches(final Class<?> type) {
            return type != null && _parent.isAssignableFrom(type);
        }
        @Override 
        public String toString() {
            return "is assignable to " + _parent.getSimpleName();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * A Test that checks to see if each class is annotated with a specific annotation. 
     * If it is, then the test returns true, otherwise false.
     */
    public static class AnnotatedWith 
    		 implements TypeScannerTest {
    	
        private Class<? extends Annotation> _annotation;

        /** 
         * Constructs an AnnotatedWith test for the specified annotation type. 
         */
        public AnnotatedWith(final Class<? extends Annotation> annotation) { 
        	_annotation = annotation; 
        }
        @Override
		public boolean matches(final Class<?> type) {
            return type != null && type.isAnnotationPresent(_annotation);
        }
        @Override 
        public String toString() {
            return "annotated with @" + _annotation.getSimpleName();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * The set of matches being accumulated.
     */
    private Set<Class<? extends T>> _matches = new HashSet<Class<?extends T>>();

    /**
     * The ClassLoader to use when looking for classes. If null then the ClassLoader returned
     * by Thread.currentThread().getContextClassLoader() will be used.
     */
    private ClassLoader _classloader;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Provides access to the classes discovered so far. If no calls have been made to
     * any of the {@code find()} methods, this set will be empty.
     * @return the set of classes that have been discovered.
     */
    public Set<Class<? extends T>> getClasses() {
        return _matches;
    }
    /**
     * Returns the class loader that will be used for scanning for classes. If no explicit
     * ClassLoader has been set by the calling, the context class loader will be used.
     *
     * @return the ClassLoader that will be used to scan for classes
     */
    public ClassLoader getClassLoader() {
        return _classloader == null ? Thread.currentThread().getContextClassLoader() 
        						    : _classloader;
    }
    /**
     * Sets an explicit ClassLoader that should be used when scanning for classes. If none
     * is set then the context class loader will be used.
     *
     * @param classloader a ClassLoader to use when scanning for classes
     */
    public void setClassLoader(final ClassLoader classloader) { 
    	this._classloader = classloader; 
    }
    /**
     * Attempts to discover classes that are assignable to the type provided. In the case
     * that an interface is provided this method will collect implementations. In the case
     * of a non-interface class, subclasses will be collected.  Accumulated classes can be
     * accessed by calling {@link #getClasses()}.
     *
     * @param parent the class of interface to find subclasses or implementations of
     * @param packageNames one or more package names to scan (including subpackages) for classes
     */
    public TypeScanner<T> findImplementations(final Class<?> parent,
    										  final String... packageNames) {
        if (packageNames == null) return this;

        TypeScannerTest test = new IsA(parent);
        for (String pkg : packageNames) {
            this.find(test,pkg);
        }
        return this;
    }
    /**
     * Attempts to discover classes that are annotated with the annotation. Accumulated
     * classes can be accessed by calling {@link #getClasses()}.
     *
     * @param annotation the annotation that should be present on matching classes
     * @param packageNames one or more package names to scan (including subpackages) for classes
     */
    public TypeScanner<T> findAnnotated(final Class<? extends Annotation> annotation,
    									final String... packageNames) {
        if (packageNames == null) return this;

        TypeScannerTest test = new AnnotatedWith(annotation);
        for (String pkg : packageNames) {
            this.find(test,pkg);
        }
        return this;
    }
    /**
     * Scans for classes starting at the package provided and descending into subpackages.
     * Each class is offered up to the Test as it is discovered, and if the Test returns
     * true the class is retained.  Accumulated classes can be fetched by calling
     * {@link #getClasses()}.
     *
     * @param test an instance of {@link TypeScannerTest} that will be used to filter classes
     * @param packageName the name of the package from which to start scanning for
     *        classes, e.g. {@code net.sourceforge.stripes}
     */
    public TypeScanner<T> find(final TypeScannerTest test,
    						   final String packageName) {
        String path = _getPackagePath(packageName);
        try {
            Collection<String> children = VFS.getInstance()
            								 .list(path);
            for (String child : children) {
                if (child.endsWith(".class")) _addIfMatching(test,child);
            }
        } catch (IOException ioe) {
            log.error("Could not read package: {}",packageName,ioe);
        }
        return this;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Converts a Java package name to a path that can be looked up with a call to
     * {@link ClassLoader#getResources(String)}.
     * 
     * @param packageName The Java package name to convert to a path
     */
    protected static String _getPackagePath(final String packageName) {
        return packageName == null ? null 
        						   : packageName.replace('.', '/');
    }
    /**
     * Add the class designated by the fully qualified class name provided to the set of
     * resolved classes if and only if it is approved by the Test supplied.
     *
     * @param test the test used to determine if the class matches
     * @param fqn the fully qualified name of a class
     */
    @SuppressWarnings("unchecked")
	protected void _addIfMatching(final TypeScannerTest test,
								  final String fqn) {
        try {
            String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/','.');
            ClassLoader loader = this.getClassLoader();
            log.trace("Checking to see if class {} matches criteria [{}]",
            		  externalName,test);

            Class<?> type = loader.loadClass(externalName);
            if (test.matches(type) ) {
                _matches.add( (Class<T>) type);
            }
        } catch (Throwable t) {
            log.warn("Could not examine class '{}' due to a {} with message: {}",
            		 fqn,t.getClass().getName(),t.getMessage());
        }
    }
}
