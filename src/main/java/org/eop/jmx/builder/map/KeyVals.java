package org.eop.jmx.builder.map;

import java.util.Arrays;
/**
 * lixinjie 2016-12-26
 */
public class KeyVals extends MNode {

	Object[] vals;
	
	public KeyVals(ICNode parent, String key, Object... vals) {
		super(parent, key);
		this.vals = vals;
	}

	@Override
	public void toMap(Context context) {
		context.add(key, Arrays.asList(vals));
	}
}
