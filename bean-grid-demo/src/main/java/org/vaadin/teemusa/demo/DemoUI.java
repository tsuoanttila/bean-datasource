package org.vaadin.teemusa.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.vaadin.teemusa.beangrid.BeanGrid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("BeanGrid Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.teemusa.demo.DemoWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {

		// Initialize our new UI component
		final BeanGrid<MyBean> component = new BeanGrid<>(createBeans(1000));
		component.setColumnReorderingAllowed(true).setFrozenColumnCount(1);

		component.addColumn("foo", MyBean::getFoo).setHidable(false);
		component.addColumn("bar", MyBean::getBar);
		component.addColumn("toString", bean -> bean.getFoo() + ", " + bean.getBar());
		component.addComponentColumn("Layout With Stuff", bean -> {
			HorizontalLayout layout = new HorizontalLayout();
			Label label = new Label(bean.getFoo());
			layout.addComponent(label);
			Button button = new Button("Click Me!", e -> Notification.show("Clicked!", Type.TRAY_NOTIFICATION));
			layout.addComponent(button);
			layout.setSizeFull();
			layout.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
			layout.addLayoutClickListener(e -> Notification.show("LayoutClick on " + bean.getBar()));
			return layout;
		});

		component.setSizeFull();

		final VerticalLayout layout = new VerticalLayout();
		layout.setStyleName("demoContentLayout");
		layout.setSizeFull();
		layout.addComponent(component);
		setContent(layout);
	}

	private Collection<MyBean> createBeans(int count) {
		List<MyBean> list = new ArrayList<MyBean>();
		for (int i = 0; i < count; ++i) {
			list.add(new MyBean("foo", "Bar " + i));
		}

		return list;
	}

}
