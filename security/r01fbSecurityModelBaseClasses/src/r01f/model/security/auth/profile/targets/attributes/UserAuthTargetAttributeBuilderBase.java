package r01f.model.security.auth.profile.targets.attributes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.model.security.oids.SecurityIDS.UserAuthTargetAttributeID;
import r01f.patterns.IsBuilder;


@NoArgsConstructor(access=AccessLevel.PUBLIC)
public abstract class UserAuthTargetAttributeBuilderBase<ID extends UserAuthTargetAttributeID,
														 A extends UserAuthTargetAttributeBase<ID>>
		   implements IsBuilder {

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class BuilderStart {
		private final A _function;

		public FunctionBuilderValueStep ofId(final ID id){
			_function.setId(id);
			return new FunctionBuilderValueStep(_function);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class FunctionBuilderValueStep {
		private final A  _function;

		public FunctionBuilderBuildStep withValue(final String value){
			_function.setValue(value);
			return new FunctionBuilderBuildStep(_function);
		}

	}

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class FunctionBuilderBuildStep {
		private final A _function;
		public A build() {
			return _function;
		}
	}
}
