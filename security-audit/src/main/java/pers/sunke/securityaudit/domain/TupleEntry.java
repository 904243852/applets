package pers.sunke.securityaudit.domain;

import java.util.Map.Entry;

public class TupleEntry<A, B> implements Entry<A, B> {

	private final A a;

	private B b;

	public TupleEntry(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public String toString() {
		return String.format("{ %s: %s }", a, b);
	}

	public A getKey() {
		return a;
	}

	public B getValue() {
		return b;
	}

	public B setValue(B b) {
		this.b = b;
		return this.b;
	}
}
