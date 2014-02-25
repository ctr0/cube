package org.cube.bender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.cube.bender.Binding.ParamBinding;

public abstract class Context<A extends Annotation> {
	
	private Map<String, Binding<A>> bindings = new HashMap<String, Binding<A>>();
	
	private Session<A> session;
	private Visitor<A> visitor;
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected Context(Session<A> session, Visitor<A> visitor) throws BenderExeption {
		this.session = session;
		this.visitor = visitor;
		Class<? extends Visitor> c = visitor.getClass();
		ParameterizedType type = (ParameterizedType) c.getGenericSuperclass();
		Class<A> a = (Class<A>) type.getActualTypeArguments()[0];
		putBindings(c, a);
	}

	protected Session<A> getSession() {
		return session;
	}

	protected Visitor<A> getVisitor() {
		return visitor;
	}
	
	public void visit(Source<A> source) throws BenderExeption {
		source.accept(this);
	}
	
	protected abstract String getBindingKey(A annotation);
	
	Binding<A> getBinding(String key) {
		return bindings.get(key);
	}
	
	private void putBindings(Class<?> c, Class<A> a) throws BenderExeption {
		
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			A annotation = method.getAnnotation(a);
			if (annotation != null) {
				putBinding(method, annotation);
				continue;
			}
		}
		for (Class<?> superInterface : c.getInterfaces()) {
			putSuperBindings(superInterface, a);
		}
		Class<?> superClass = c.getSuperclass();
		if (superClass != Object.class) {
			putSuperBindings(superClass, a);
		}
	}

	private void putSuperBindings(Class<?> c, Class<A> a) throws BenderExeption {
		
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			A annotation = method.getAnnotation(a);
			if (annotation != null) {
				if (!isNewBinding(method)) {
					putBinding(method, annotation);
					continue;
				}
			}
		}
		for (Class<?> superInterface : c.getInterfaces()) {
			putSuperBindings(superInterface, a);
		}
		Class<?> superClass = c.getSuperclass();
		if (superClass != Object.class) {
			putSuperBindings(superClass, a);
		}
	}

	private boolean isNewBinding(Method method) {
		String methodName = method.getName().intern();
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (Binding<A> binding : bindings.values()) {
			Method m = binding.getMethod();
			if (methodName == m.getName() && arrayContentsEq(parameterTypes, m.getParameterTypes())) {
				return false;
			}
		}
		return true;
	}

	private void putBinding(Method method, A annotation) throws BenderExeption {
		Class<?>[] paramTypes = method.getParameterTypes();
		ParamBinding<A>[] paramBindings = null;
		if (paramTypes.length > 0) {
			paramBindings = new ParamBinding[paramTypes.length];
			Annotation[][] annos = method.getParameterAnnotations();
			if (annos.length != paramTypes.length) {
				throw new BenderExeption("Missing annotations in method parameters");
			}
			for (int i = 0; i < paramTypes.length; i++) {
				Annotation paramAnno = null;
				for (Annotation anno : annos[i]) {
					if (anno.getClass() == annotation.getClass()) {
						if (paramAnno == null) {
							paramAnno = anno;
						} else {
							throw new BenderExeption("Duplicated annotation in method parameter");
						}
					}
				}
				if (paramAnno == null) {
					throw new BenderExeption("Missing annotations in method parameters");
				}
				paramBindings[i] = new ParamBinding<A>(paramTypes[i], (A) paramAnno);
			}
		}
		String bindingKey = getBindingKey(annotation);
		bindings.put(bindingKey, new Binding<A>(method, annotation, paramBindings));
	}
	
	private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
		if (a1 == null) {
			return a2 == null || a2.length == 0;
		}
		if (a2 == null) {
			return a1.length == 0;
		}
		if (a1.length != a2.length) {
			return false;
		}
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

}
