package org.cube.bender;

public class BenderSession extends Session<Bender> {

	public BenderSession() {
		super(BenderContext.class);
	}

	public BenderContext getContext(BenderVisitor visitor) throws BenderExeption {
		return (BenderContext) super.getContext(visitor);
	}
	
}
