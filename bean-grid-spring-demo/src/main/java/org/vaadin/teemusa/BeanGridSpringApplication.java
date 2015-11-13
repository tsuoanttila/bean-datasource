package org.vaadin.teemusa;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.vaadin.teemusa.beandatasource.interfaces.CollectionContainer.HasPaging;
import org.vaadin.teemusa.beandatasource.spring.ContainerPageRequest;
import org.vaadin.teemusa.beandatasource.spring.SpringRepositoryContainer;
import org.vaadin.teemusa.beangrid.BeanGrid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

// Spring boot init

@SpringBootApplication
public class BeanGridSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeanGridSpringApplication.class, args);
	}
}

// Test data generation on start of spring boot

@Component
class ReservationGenerator implements CommandLineRunner {

	@Autowired
	private ReservationRepository repo;

	@Override
	public void run(String... arg0) throws Exception {
		Stream.of("Josh", "Sami", "Teemu").forEach(e -> repo.save(new Reservation(e)));
		IntStream.range(0, 1000).forEach(e -> repo.save(new Reservation("" + e)));
	}
}

// Vaadin UI

@SpringUI
@Title("BeanGrid with Spring Data")
@Widgetset("org.vaadin.teemusa.BeanGridSpringWidgetSet")
@Theme("valo")
class MyUI extends UI {

	@Autowired
	private SpringRepositoryContainer<Reservation, Long, ReservationRepository> container;

	@Override
	protected void init(VaadinRequest request) {
		container.setIdResolver(Reservation::getId);
		BeanGrid<Reservation> grid = new BeanGrid<>(container);
		grid.addColumn("Reservation Name", Reservation::getReservationName);
		grid.addComponentColumn("Reset name", bean -> new Button("Rename", click -> {
			bean.setReservationName("Foo!");
			grid.getBeanContainer().refresh(bean);
		}));
		grid.addComponentColumn("Remove", bean -> {
			Button button = new Button("DELETE", click -> container.remove(bean));
			button.addStyleName(ValoTheme.BUTTON_DANGER);
			return button;
		});
		grid.setSizeFull();

		TextField textField = new TextField("Reservation Name");
		Button addReservation = new Button("Add", click -> {
			container.add(new Reservation(textField.getValue()));
			textField.clear();
		});
		addReservation.setStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout addingLayout = new HorizontalLayout(textField, addReservation);
		addingLayout.setComponentAlignment(addReservation, Alignment.BOTTOM_RIGHT);
		addingLayout.setWidth("100%");

		Shortcuts.add(addReservation, KeyCode.ENTER, s -> addReservation.click());

		VerticalLayout content = new VerticalLayout(grid, addingLayout);
		content.setExpandRatio(grid, 1.0f);
		content.setSpacing(true);
		content.setMargin(true);
		content.setSizeFull();

		setContent(content);
	}

	// Helper to make my life easier.
	static interface Shortcuts {

		static class ShortcutEvent {
		}

		void onShortcut(ShortcutEvent e);

		static void add(AbstractComponent component, int keyCode, Shortcuts listener) {
			component.addShortcutListener(new com.vaadin.event.ShortcutListener("", keyCode, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					listener.onShortcut(new ShortcutEvent());
				}

			});

		}
	}

}

// Reservation bean

@Entity
class Reservation {

	@Id
	@GeneratedValue
	private Long id;

	private String reservationName;

	public Reservation() { // Why JPA?
	}

	public Reservation(String reservationName) {
		this.reservationName = reservationName;
	}

	public Long getId() {
		return id;
	}

	public String getReservationName() {
		return reservationName;
	}

	public void setReservationName(String reservationName) {
		this.reservationName = reservationName;
	}

	@Override
	public String toString() {
		return "Reservation{id=" + getId() + ",reservationName=" + getReservationName() + "}";
	}
}

/**
 * Thanks to Spring Boot, this is all we need. No extra annotations
 */
interface ReservationRepository extends JpaRepository<Reservation, Long> {
	Page<Reservation> findAll(Pageable p);
}

@Component
class ReservationContainer extends SpringRepositoryContainer<Reservation, Long, ReservationRepository>
		implements HasPaging<Reservation> {

	@Override
	public Iterable<Reservation> getPage(int start, int end) {
		ContainerPageRequest pageRequest = ContainerPageRequest.getPageRequest(start, end);
		return pageRequest.getSubList(repo.findAll(pageRequest));
	}
}