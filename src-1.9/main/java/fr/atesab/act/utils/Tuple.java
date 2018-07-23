package fr.atesab.act.utils;

/**
 * Tuple to store data
 * 
 * @author ATE47
 *
 * @param <A>
 *            First type
 * @param <B>
 *            Second type
 * @since 2.0
 * @see QuadTuple
 */
public class Tuple<A, B> {
	/**
	 * Quad Tuple to store data
	 * 
	 * @author ATE47
	 *
	 * @param <A>
	 *            First type
	 * @param <B>
	 *            Second type
	 * @param <C>
	 *            Third type
	 * @param <D>
	 *            Fourth type
	 * @since 2.0
	 * @see Tuple
	 */
	public static class QuadTuple<A, B, C, D> extends Tuple<A, B> {
		public C c;
		public D d;

		/**
		 * Create a quad tuple with four data
		 * 
		 * @param a
		 * @param b
		 * @param c
		 * @param d
		 * @since 2.0
		 */
		public QuadTuple(A a, B b, C c, D d) {
			super(a, b);
			this.c = c;
			this.d = d;
		}

		/**
		 * Create a new quad tuple with same data
		 * 
		 * @param quad
		 * @since 2.1
		 */
		public QuadTuple(QuadTuple<A, B, C, D> quad) {
			this(quad.a, quad.b, quad.c, quad.d);
		}
	}

	public A a;

	public B b;

	/**
	 * Create a simple tuple with two data
	 * 
	 * @param a
	 * @param b
	 * @since 2.0
	 */
	public Tuple(A a, B b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Create a new tuple with same data
	 * 
	 * @param tuple
	 * @since 2.0
	 */
	public Tuple(Tuple<A, B> tuple) {
		this(tuple.a, tuple.b);
	}
}
