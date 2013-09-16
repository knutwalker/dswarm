package de.avgl.dmp.converter.util;

import com.google.common.collect.AbstractIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

/**
 * Implement the Iterable/Iterator Interface for a {@link NodeList} (which
 * might as well be a Collection type with _the_ worst API, ever)
 */
public class NodeListIterable implements Iterable<Node> {
	private final NodeList ns;

	private class NodeListIterator extends AbstractIterator<Node> {
		private int i = 0;
		private final int l = ns.getLength();

		@Override
		protected Node computeNext() {
			Node n;
			while (i < l) {
				if ((n = ns.item(i++)).getNodeType() == Node.ELEMENT_NODE) {
					return n;
				}
			}
			return endOfData();
		}
	}

	public NodeListIterable(NodeList ns) {
		this.ns = ns;
	}

	@Override
	public Iterator<Node> iterator() {
		return new NodeListIterator();
	}
}