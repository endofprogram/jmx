package org.eop.jmx.builder.map;

/**
 * lixinjie 2016-12-26
 */
public class RootMap extends CNode {

	public RootMap(ICNode parent) {
		super(parent, "");
	}
	
	@Override
	public void toMap(Context context) {
		for (IMNode child : children) {
			child.toMap(context);
		}
	}

}
