package org.vaadin.teemusa.demo;

public class MyBean {

	private String foo = "";
	private String bar = "";

	public MyBean() {
	}

	public MyBean(String foo, String bar) {
		this();
		setFoo(foo);
		setBar(bar);
	}

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	public String getBar() {
		return bar;
	}

	public void setBar(String bar) {
		this.bar = bar;
	}

	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
}
