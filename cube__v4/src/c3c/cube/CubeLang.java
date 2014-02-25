package c3c.cube;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public interface CubeLang {
	
	@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR})
	public static @interface internal {}
	
	@Target(ElementType.METHOD)
	public static @interface inline {}
	
	@Target(ElementType.METHOD)
	public static @interface virtual {}
	
	@Target(ElementType.METHOD)
	public static @interface override {}
	
	public static @interface reinterpret {}

}
