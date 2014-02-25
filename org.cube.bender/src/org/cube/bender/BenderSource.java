package org.cube.bender;

import java.util.Iterator;
import java.util.Stack;


public abstract class BenderSource extends Source<Bender> {
	
	private Stack<String> path = new Stack<String>();
	
	/* (non-Javadoc)
	 * @see org.cube.bender.Source#accept(org.cube.bender.Visitor)
	 */
	@Override
	protected final void accept(Visitor<Bender> visitor) throws BenderExeption {
		accept((BenderVisitor) visitor);
	}
	
	protected abstract void accept(BenderVisitor visitor) throws BenderExeption;
	
	protected String getAbsolutePath() {
		StringBuilder builder = new StringBuilder(path.size() * 8);
		Iterator<String> it = path.iterator();
		while (it.hasNext()) {
			builder.append(it.next());
			builder.append('/');
		}
		return builder.toString();
	}

	protected void pushPath(String path) {
		this.path.push(path);
	}
	
	protected void popPath() {
		this.path.pop();
	}
}
