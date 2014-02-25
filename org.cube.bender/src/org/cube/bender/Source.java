package org.cube.bender;

import java.lang.annotation.Annotation;

import org.cube.bender.Binding.ParamBinding;


public abstract class Source<A extends Annotation> {
	
	private Context<A> context;
	
	final void accept(Context<A> context) throws BenderExeption {
		this.context = context;
		accept(context.getVisitor());
	}

	protected abstract void accept(Visitor<A> visitor) throws BenderExeption;

	protected A getAnnotation(String key) {
		return context.getBinding(key);
	}
	
	protected Visitor<A> visit(String key, String[] params, Object[] values) {
		Binding<A> binding = context.getBinding(key);
		if (binding == null) {
			return null; // TODO RESTRICTED VALIDATION
		}
		for (ParamBinding<A> paramBinding : binding.getParamBindings()) {
			if (paramBinding.getAnnotation().v)
		}
		
		return null;
	}
}
