package info.jtrac.mylar;

import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;

public class JtracAttributeFactory extends AbstractAttributeFactory {

	@Override
	public boolean getIsHidden(String key) {
		return false;
	}

	@Override
	public String getName(String key) {
		return key;
	}

	@Override
	public boolean isReadOnly(String key) {
		return true;
	}

	@Override
	public String mapCommonAttributeKey(String key) {
		return key;
	}

}
