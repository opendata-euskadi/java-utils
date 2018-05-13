package r01f.types.url;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.PathBase;
import r01f.types.PathFactory;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Represents a {@link Path} in a {@link URL}
 * ie: http://site:port/urlPath
 */
@Immutable
@MarshallType(as="urlPath")
public class UrlPath
	 extends Path 
  implements IsUrlPath {
	private static final long serialVersionUID = -4132364966392988245L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPath() {
		super(Lists.newArrayList());
	}
	public UrlPath(final Collection<String> pathEls) {
		super(pathEls);
	}
	public UrlPath(final Object... objs) {
		super(objs);
	}
	public UrlPath(final Object obj) {
		super(obj);
	}
	public <P extends IsPath> UrlPath(final P otherPath) {
		super(otherPath);
	}
	public UrlPath(final String... elements) {
		super(elements);
	}
	public static PathFactory<UrlPath> PATH_FACTORY = new PathFactory<UrlPath>() {
															@Override
															public UrlPath createPathFrom(final Collection<String> elements) {
																return new UrlPath(elements);
															}
												   };
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> PathFactory<P> getPathFactory() {
		return (PathFactory<P>)UrlPath.PATH_FACTORY;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACTORIES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Factory from {@link String}
	 * @param path
	 * @return
	 */
	public static UrlPath valueOf(final String path) {
		return new UrlPath(path);
	}
	/**
	 * Factory from path components
	 * @param elements 
	 * @return the {@link Path} object
	 */
	public static UrlPath from(final String... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return null;
		return new UrlPath(elements);
	}
	/**
	 * Factory from other {@link Path} object
	 * @param other 
	 * @return the new {@link Path} object
	 */
	public static <P extends IsPath> UrlPath from(final P other) {
		if (other == null) return null;
		UrlPath outPath = new UrlPath(other);
		return outPath;
	}
	/**
	 * Factory from an {@link Object} (the path is composed translating the {@link Object} to {@link String})
	 * @param obj 
	 * @return the {@link Path} object
	 */
	public static UrlPath from(final Object... obj) {
		if (obj == null) return null;
		return new UrlPath(obj);
	}
	/**
	 * Factory from a {@link URL} object 
	 * @param url
	 * @return
	 */
	@GwtIncompatible
	public static UrlPath from(final URL url) {
		if (url == null) return null;
		return new UrlPath(url.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asAbsoluteStringIncludingQueryStringEncoded(final UrlQueryString queryString) {
		return this.asAbsoluteStringIncludingQueryString(queryString,
														 true);
	}
	@Override
	public String asAbsoluteStringIncludingQueryString(final UrlQueryString queryString) {
		return this.asAbsoluteStringIncludingQueryString(queryString,
														 false);	
	}
	@Override
	public String asAbsoluteStringIncludingQueryString(final UrlQueryString queryString,
													   final boolean encodeParamValues) {
		return queryString != null ? Strings.customized("{}?{}",
												   	    this.asAbsoluteString(),queryString.asString(encodeParamValues))
								   : this.asAbsoluteString();
	}
	@Override
	public String asRelativeStringIncludingQueryStringEncoded(final UrlQueryString queryString) {
		return this.asRelativeStringIncludingQueryString(queryString,
														 true);
	}
	@Override
	public String asRelativeStringIncludingQueryString(final UrlQueryString queryString) {
		return this.asRelativeStringIncludingQueryString(queryString,
														 false);
	}
	@Override
	public String asRelativeStringIncludingQueryString(final UrlQueryString queryString,
													   final boolean encodeParamValues) {
		return queryString != null ? Strings.customized("{}?{}",
												   		this.asRelativeString(),queryString.asString(encodeParamValues))
								   : this.asRelativeString();
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public UrlPath joinedWith(final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return this;
		return (UrlPath)PathBase.join(this.getPathFactory(),
								  	  this,_sanitize(elements));
	}
	@Override @SuppressWarnings("unchecked")
	public UrlPath prependedWith(final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return this;
		return (UrlPath)PathBase.prepend(this.getPathFactory(),
								   	  	 this,_sanitize(elements));
	}
	private Collection<Object> _sanitize(final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) throw new IllegalArgumentException();
		return FluentIterable.from(elements)
					.transform(new Function<Object,Object>() {
										@Override
										public Object apply(final Object el) {
											if (el instanceof String) {
												String elStr = el.toString();
												// remove query string or anchor if present
												int pQ = elStr.indexOf('?');		
												if (pQ >= 0) elStr = elStr.substring(0,pQ);
												int pA = elStr.indexOf('#');
												if (pA >= 0) elStr = elStr.substring(0,pA);
												return elStr;
											}
											return el;
										}
							   })
					.filter(new Predicate<Object>() {
									@Override
									public boolean apply(final Object el) {
										return el instanceof String 
													? ((String)el).length() > 0
												    : true;
									}
							})
					.toList();
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the remaining path fragment begining where the given
	 * starting path ends
	 * ie: if path=/a/b/c/d
	 *     and startingPath = /a/b
	 *     ... this function will return /c/d
	 * @param startingPath
	 * @return
	 */
	public UrlPath remainingPathFrom(final UrlPath startingPath) {
		Collection<String> remainingPathEls = this.getPathElementsAfter(startingPath);
		return remainingPathEls != null ? new UrlPath(remainingPathEls)
										: null;
	}
	/**
	 * Returns the url path AFTER the given prefix
	 * ie: if path=/foo/bar/baz.hml and prefix=/foo
	 * then, the returned path=/bar/baz.html
	 * If the path does NOT starts with the given prefix, it throws an IllegalStateException
	 * @param prefix
	 * @return
	 */
	public UrlPath urlPathAfter(final UrlPath prefix) {
		return this.remainingPathFrom(prefix);
	}
}
