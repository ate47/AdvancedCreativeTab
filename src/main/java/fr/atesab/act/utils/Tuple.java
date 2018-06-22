package fr.atesab.act.utils;

public class Tuple<A, B> {
	public static class QuadTuple<A, B, C, D> {
		public A a;
		public B b;
		public C c;
		public D d;

		public QuadTuple(A a, B b, C c, D d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}
	}

	public A a;

	public B b;

	public Tuple(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public Tuple(Tuple<A, B> node) {
		this(node.a, node.b);
	}
}
