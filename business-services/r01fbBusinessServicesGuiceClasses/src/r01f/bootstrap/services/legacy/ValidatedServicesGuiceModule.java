package r01f.bootstrap.services.legacy;

import com.google.inject.Module;

@Deprecated
interface ValidatedServicesGuiceModule
  extends Module {
	void validate();
}
