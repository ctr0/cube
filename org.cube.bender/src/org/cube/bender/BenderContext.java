package org.cube.bender;


public class BenderContext extends Context<Bender> {

	protected BenderContext(Session<Bender> session, Visitor<Bender> visitor) {
		super(session, visitor);
	}

	/* (non-Javadoc)
	 * @see org.cube.bender.Session#getBindingKey(java.lang.annotation.Annotation)
	 */
	@Override
	protected String getBindingKey(Bender annotation) {
		return annotation.value();
	}
	
	
}
