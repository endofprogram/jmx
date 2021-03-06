package org.eop.jmx.builder.map;

/**
 * lixinjie 2016-12-26
 */
public class ItemMap extends CNode {

	public ItemMap(ICNode parent) {
		super(parent, "");
	}

	@Override
	public void toMap(Context context) {
		Context ncontext = new Context(context.getMapType(), context.getListType());
		for (IMNode child : children) {
			child.toMap(ncontext);
		}
		context.add(ncontext.getMap());
	}
}
