package r01f.jwt.parser;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.patterns.IsBuilder;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//JWT TOKEN VERIFIER .

/*				JWTParserBuilder p = JWTParserBuilder.createParser()
                                                         .build();
		        p.parse(myJWT);
 **/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class JWTParserBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static BuilderBuildStep createParser() {
		JWTParserBuilderInitWrapperData initData = new JWTParserBuilderInitWrapperData();

		return new JWTParserBuilder() { /* nothing */ }
					.new BuilderBuildStep(initData);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BuilderBuildStep {
		@SuppressWarnings("unused")
		private final JWTParserBuilderInitWrapperData _initData;

		public JWTParser build() {
			return new JWTParserImpl();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
// INNER WRAPPER HELPER CLASSES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * JWTInitVerifierWrapperData
	 * @author PCI
	 *
	 */
	@Accessors(prefix="_")
	public static class JWTParserBuilderInitWrapperData
	      implements Serializable {

		private static final long serialVersionUID = -7160644044519671660L;

	}

}
