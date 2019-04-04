package r01f.test.html.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;

import avro.shaded.com.google.common.collect.Lists;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import r01f.html.elements.HeadHtmlEl;
import r01f.html.elements.HtmlEl;
import r01f.html.elements.HtmlElements;
import r01f.html.elements.HtmlMetas;
import r01f.html.elements.HtmlMetas.MetaHtmlEl;
import r01f.html.parser.HtmlParserToken;
import r01f.html.parser.HtmlTokenizerFlowable;
import r01f.html.parser.base.HtmlParserTokensToString;
import r01f.html.parser.starttag.HtmlStartTagParserToken;
import r01f.html.parser.starttag.HtmlStartTagTokenizerFlowable;
import r01f.util.types.Strings;

@Slf4j
public class HtmlTokenizerTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	HTML
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testHtmlParser1() throws MalformedURLException,
							  		IOException {
		String src = "<ho<la>aa<!--#include virtual='hola.shtml' -->fdsa</hol<a>";
//		String src = "<hola>aaa</hola>";
		log.warn(src);

		Flowable<HtmlParserToken> flowable = HtmlTokenizerFlowable.createFrom(src);
		String dbg = HtmlParserTokensToString.from(flowable)
							  .using(new Function<HtmlParserToken,String>() {
											@Override
											public String apply(final HtmlParserToken token) {
												return Strings.customized("> {}:\t\t[{}]\n",
																		  token.getType(),token.getText().trim());
											}
							  		 });
		log.warn("\n{}",dbg);
		
	}
	@Test
	public void testHtmlParser2() {
		String src = _createHtmlCont();
		log.warn("=================================================================");
		
		// Parse & Debug
		Flowable<HtmlParserToken> flowable1 = HtmlTokenizerFlowable.createFrom(src);
		String dbg = HtmlParserTokensToString.from(flowable1)
							  .using(new Function<HtmlParserToken,String>() {
											@Override
											public String apply(final HtmlParserToken token) {
												return Strings.customized("> {}:\t\t[{}]\n",
																		  token.getType(),token.getText().trim());
											}
							  		 });
		log.warn("\n{}",dbg);
		
		// Parse & to String
		Flowable<HtmlParserToken> flowable2 = HtmlTokenizerFlowable.createFrom(src);
		String html = HtmlParserTokensToString.from(flowable2)
											  .using(new Function<HtmlParserToken,String>() {
															@Override
															public String apply(final HtmlParserToken token) {
																return token.getText();
															}
											  		 });
		log.warn("\n{}",html);
		Assert.assertEquals(src.length(),html.length());	// The reconstructed html MUST be the same as the original one
	}
	private static String _createHtmlCont() {
		String src = "<html lang=\"<!--#echo var='LANG'-->\" var=\"<!--#echo var='VAR'-->\">" + "\n" +
					 "<!-- a comment -->" + "\n" +
					 "<!-- a comment <!--# with ssi --> ... -->" + "\n" +
					 "<head>" + "\n" +
					 	"<meta name=\"dc.description\" content=\"Eusko Jaurlaritzaren edukiak erakusteko orria\" />" + "\n" +
					 	"<script defer src=a/b.js />" + "\n" + 
					 	"<script>" + "\n" + 
					 		"document.write('<script>...</script>');" + "\n" +
					 	"</script>" + "\n" +
					 	"<script>" + "\n" + 
					 		"document.write('<' + tagName + '>')" + "\n" +
					 	"</script>" + "\n" +
					 "</head>" + "\n" +
					 "<body class='a' \n" + "\n" +
					       "id='body'>" + "\n" +
					 	"<div id='myId'>" + "\n" +
					 		"<p class='myClass'>Hello!</p>" + "\n" +
					 	"</div>" + "\n" +
					 	"<div>" + "\n" + 
					 		"<!--#include virtual='/foo/bar.html -->" + "\n" + 
					 	"</div>" + "\n" +
					 	"<not a tag< whatever>" + "\n" +
					 	"<also<s>" + "\n" +
					 "</body>" + "\n" + 
					 "</html>";
		log.warn(src);
		return src;
	}
	@Test
	public void testHtmlParser3() throws MalformedURLException,
							  		IOException {
		String src = "<head>\n" +
					 	"<!--[if gte IE 9]><!-->\n" +
					 		"<script src=\"/appcont/aa81aUI/jquery/jquery-2.1.4.min.js\"></script>\n" + 
					 	"<!--<![endif]-->\n" +
					 "</head>";
		log.warn(src);

		Flowable<HtmlParserToken> flowable = HtmlTokenizerFlowable.createFrom(src);
		String dbg = HtmlParserTokensToString.from(flowable)
							  .using(new Function<HtmlParserToken,String>() {
											@Override
											public String apply(final HtmlParserToken token) {
												return Strings.customized("> {}:\t\t[{}]\n",
																		  token.getType(),token.getText().trim());
											}
							  		 });
		log.warn("\n{}",dbg);
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	START TAG
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testHtmlStartTag() {
		String tag0 = "<body class=\"margenesBody\" onload=\"iniciar()\" style=\"pointer-events: auto;\">\r\n";
		_testTag(tag0,3);
		
		String tag1 = "<script defer defer2 src='a.js' attr1=val  kk attr2=\"a b\" attr3=<!--#echo var='a'--><!--#echo var='b' --> attr4='<!--#echo var='c'-->'>";
		_testTag(tag1,8);
		
//		String tag2 = "<script a=<!--# aa --> = 99 />";
//		_testTag(tag2,1);
		
		String tag3 = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"<!--#echo var='LANG'-->\" lang=\"<!--#echo var='LANG'-->\">";
		_testTag(tag3,3);
	}
	private void _testTag(final String tag,
						  final int expectedAttrs) {
		log.warn("> {}",tag);
		
		// Parse & degub
		Flowable<HtmlStartTagParserToken> flowable = HtmlStartTagTokenizerFlowable.createFrom(tag);
		String dbg = HtmlParserTokensToString.from(flowable)
							  .using(new Function<HtmlStartTagParserToken,String>() {
											@Override
											public String apply(final HtmlStartTagParserToken token) {
												return Strings.customized("> {}:\t\t[{}]\n",
																		  token.getType(),token.getText().trim());
											}
							  		 });
		log.warn("\n{}",dbg);
		
		// Parse & to String
		Flowable<HtmlStartTagParserToken> flowable2 = HtmlStartTagTokenizerFlowable.createFrom(tag);
		String html = HtmlParserTokensToString.from(flowable2)
											  .using(new Function<HtmlStartTagParserToken,String>() {
															@Override
															public String apply(final HtmlStartTagParserToken token) {
																String out = null;
																switch(token.getType()) {
																case TagName:
																	out = "<" + token.getText();
																	break;
																case WhiteSpace:
																case AttributeName:
																case EqualsSign:
																case AttributeValue:
																	out = token.getText();
																	break;
																default:
																	break;
																}
																return out;
															}
											  		 });
		html = html + ">";
		
		log.warn("{}",tag);
		log.warn("{}",html);
		Assert.assertEquals(tag.trim().length(),html.length());
		
		// attributes
		Map<String,String> attrs = HtmlElements.parseAttributes(tag);
		Assert.assertEquals(attrs.size(),expectedAttrs);
		log.warn("{}",HtmlElements.attributesMapToString(attrs));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HTML ELEMENT
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testHtmlElement() {
		HtmlEl htmlEl = new HtmlEl("<html lang=\"<!--#echo var='LANG'-->\">");
		System.out.println("====>" + htmlEl.asString());
		
		HtmlEl htmlEl2 = new HtmlEl("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"es\" lang=\"es\">");
		System.out.println("====>" + htmlEl2.asString());  
 
		MetaHtmlEl meta = HtmlMetas.MetaHtmlEl.from("<meta name=\"description\" lang=\"<!--#echo var='LANG'-->\" content=\"<!--#echo var=\"R01_META_DESCRIPTION\" -->\"/>");
		System.out.println("=====>" + meta.asString());		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testMetas() {
		HeadHtmlEl hd1 = new HeadHtmlEl("a title 1",
									    Lists.newArrayList( HtmlMetas.MetaHtmlEl.from("<meta http-equiv='content-type' content='text/html; charset=UTF-8' />")),
									    null);
		HeadHtmlEl hd2 = new HeadHtmlEl("a title 2",
									    Lists.newArrayList(HtmlMetas.MetaHtmlEl.from("<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1' />")),
									    null);
		HeadHtmlEl mixed = hd1.newHeadMixingWith(hd2);
		Assert.assertEquals(1,mixed.getMetas().size());
		System.out.println("====>" + mixed.asString());
	}
}
