package c3c.cube;

import java.util.Iterator;


public class List<T> implements Iterable<T> {
	
	/*internal*/ Wrapper<T> first;
	
	/*internal*/ Wrapper<T> last;
	
	public interface Filter<T> {
		
		public boolean matches(T node);
	}
	
	public void clean() {
		first = last = null;
	}
	
	public T add(T node) {
		if (node != null) {
			if (first == null) {
				first = last = new Wrapper<T>(node);
			} else {
				Wrapper<T> wrapper = new Wrapper<T>(node);
				last.next = wrapper;
				wrapper.previous = last;
				last = wrapper;
			}
		}
		return node;
	}
	
	public T last() {
		return (last != null) ? last.node : null;
	}

	public Iterator<T> iterator() {
		return new NodeIterator(first);
	}
	
	static class Wrapper<T> {
		
		T node;
		Wrapper<T> next;
		/*weak*/ Wrapper<T> previous;
		
		public Wrapper(T decl) {
			this.node = decl;
		}
	}
	
	public T get(String name) {
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			T n = iterator.next();
			if (n.equals(name)) {
				return n;
			}
		}
		return null;
	}
	
	public T get(T node) {
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			T n = iterator.next();
			if (n == node) {
				return n;
			}
		}
		return null;
	}
	
	public T remove(String name) {
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			T n = iterator.next();
			if (n.equals(name)) {
				iterator.remove();
				return n;
			}
		}
		return null;
	}
	
	public T remove(T node) {
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			T n = iterator.next();
			if (n == node) {
				iterator.remove();
				return n;
			}
		}
		return null;
	}
	
	public void remove(Filter<T> filter) {
		Iterator<T> iterator = iterator();
		while (iterator.hasNext()) {
			T n = iterator.next();
			if (filter.matches(n)) {
				iterator.remove();
			}
		}
	}
	
	private void __remove(Wrapper<T> item) {
		if (item.next != null)
			item.next.previous = item.previous;
		if (item.previous != null) {
			item.previous.next = item.next;
			item = item.previous;
		} else {
			first = last = null;
		}
	}
	
	public class NodeIterator implements Iterator<T> {
		
		private Wrapper<T> next;

		private NodeIterator(Wrapper<T> first) {
			next = first;
		}

		public boolean hasNext() {
			return next != null;
		}

		public T next() {
			T node = next.node;
			next = next.next;
			return node;
		}

		public void remove() {
			if (next == null) {
				if (last != null) __remove(last);
			} else {
				__remove(next.previous);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		Iterator<T> iterator = iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			if (first) first = false;
			else builder.append(", ");
			builder.append(iterator.next().toString());
		}
		builder.append(']');
		return builder.toString();
	}
	
}
