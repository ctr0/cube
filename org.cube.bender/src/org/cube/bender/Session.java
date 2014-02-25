package org.cube.bender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public abstract class Session<A extends Annotation> {

	private Map<Visitor<A>, Context<A>> contexts = new HashMap<Visitor<A>, Context<A>>();
	
	private Class<? extends Context<A>> factory;
	private Constructor<? extends Context<A>> constructor;
	
	public Session(Class<? extends Context<A>> factory) {
		this.factory = factory;
	}

	public Context<A> getContext(Visitor<A> visitor) throws BenderExeption {
		Context<A> context = contexts.get(visitor);
		if (context == null) {
			try {
				context = getContextConstructor().newInstance((Object[]) null);
			} catch (Exception e) {
				throw new BenderExeption("Cannot instantiate context " + factory.getCanonicalName(), e);
			}
			contexts.put(visitor, context);
		}
		return context;
	}
	
	private Constructor<? extends Context<A>> getContextConstructor() 
			throws NoSuchMethodException, SecurityException {
		if (constructor == null) {
			constructor = factory.getConstructor((Class<?>[]) null);
		}
		return constructor;
	}

}
