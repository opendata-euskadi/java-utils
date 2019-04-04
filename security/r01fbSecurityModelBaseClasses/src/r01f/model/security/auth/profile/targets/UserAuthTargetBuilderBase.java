package r01f.model.security.auth.profile.targets;

import java.util.Arrays;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.model.security.auth.profile.targets.attributes.UserAuthTargetAttributeBase;
import r01f.model.security.oids.SecurityIDS.UserAuthTargetAttributeID;
import r01f.model.security.oids.SecurityIDS.UserAuthTargetID;
import r01f.patterns.IsBuilder;


@NoArgsConstructor(access=AccessLevel.PUBLIC)
public abstract class UserAuthTargetBuilderBase< A extends UserAuthTargetBase<ID,IA,C>,
												ID extends UserAuthTargetID,
												 C extends UserAuthTargetAttributeBase<IA>,
												IA extends UserAuthTargetAttributeID>
		   implements IsBuilder {

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class BuilderStart {
		private final A _target;

		public TargetBuilderAttributesStep ofId(final ID id){
			_target.setId(id);
			return new TargetBuilderAttributesStep(_target);
		}
		public TargetBuilderAttributesStep withoutId(){
			return new TargetBuilderAttributesStep(_target);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class TargetBuilderAttributesStep {
		private final A  _target;

		public TargetBuilderBuildStep withAttributes(final Collection<C> attrs){
			_target.setAttributes(attrs);
			return new TargetBuilderBuildStep(_target);
		}
		@SuppressWarnings("unchecked")
		public  TargetBuilderBuildStep withAttributes(final C ... attrs ){
			_target.setAttributes( Arrays.asList(attrs));
			return new TargetBuilderBuildStep(_target);
		}
		public TargetBuilderBuildStep withoutAttributes(){
			return new TargetBuilderBuildStep(_target);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class TargetBuilderBuildStep {
		private final A _target;
		public A build() {
			return _target;
		}
	}
}
