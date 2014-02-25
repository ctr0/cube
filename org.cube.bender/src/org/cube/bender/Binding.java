package org.cube.bender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class Binding<A extends Annotation> {
	
	private final Method method;

	private final A annotation;
	
	private final ParamBinding<A>[] paramBindings;

	Binding(Method method, A annotation, ParamBinding<A>[] paramBindings) {
		this.method = method;
		this.annotation = annotation;
		this.paramBindings = paramBindings;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @return the annotation
	 */
	public A getAnnotation() {
		return annotation;
	}	
	
	/**
	 * @return the paramBindings
	 */
	public ParamBinding<A>[] getParamBindings() {
		return paramBindings;
	}

	public static class ParamBinding<A extends Annotation> {

		private final Class<?> type;
		
		private final A annotation;
		
		ParamBinding(Class<?> type, A annotation) {
			this.type = type;
			this.annotation = annotation;
		}

		/**
		 * @return the type
		 */
		public Class<?> getType() {
			return type;
		}

		/**
		 * @return the annotation
		 */
		public Annotation getAnnotation() {
			return annotation;
		}
		
	}
}
