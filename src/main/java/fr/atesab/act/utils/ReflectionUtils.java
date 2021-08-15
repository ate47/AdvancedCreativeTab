package fr.atesab.act.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of tools to help with reflection
 * 
 * @author ATE47
 * @since 2.4.0
 */
public class ReflectionUtils {

    /**
     * fetch all the superclasses without Object.class
     * 
     * @param current the current class
     * @return the list of the superclasses
     */
    public static List<Class<?>> superClassTo(Class<?> current) {
        return superClassTo(current, Object.class);
    }

    /**
     * fetch all the superclasses and stop at the end or Object.class if current
     * isn't a parent of end without end or Object.class
     * 
     * @param current the current class
     * @param end     the end class
     * @return the list of the superclasses
     */
    public static List<Class<?>> superClassTo(Class<?> current, Class<?> end) {
        var stack = new ArrayList<Class<?>>();

        var c = current;

        do {
            stack.add(current);
            c = c.getSuperclass();
        } while (c != Object.class || c != end);

        return stack;
    }

    private ReflectionUtils() {
    }
}
