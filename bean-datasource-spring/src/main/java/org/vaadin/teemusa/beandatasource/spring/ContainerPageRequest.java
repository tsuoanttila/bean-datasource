package org.vaadin.teemusa.beandatasource.spring;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class ContainerPageRequest extends PageRequest {

	private int sublistStart;
	private int sublistEnd;

	public ContainerPageRequest(int page, int size) {
		super(page, size);
	}

	public <T> Iterable<T> getSubList(Page<T> page) {
		return page.getContent().subList(sublistStart, sublistEnd);
	}

	public static ContainerPageRequest getPageRequest(int start, int end) {
		ContainerPageRequest request;

		if (start < end - start) {
			request = new ContainerPageRequest(0, end);
			request.sublistStart = start;
			request.sublistEnd = end;
		} else {
			int size = end - start;
			while (start / size != (end - 1) / size) {
				++size;
			}
			request = new ContainerPageRequest(start / size, size);
			request.sublistStart = start % size;
			request.sublistEnd = (end % size == 0 ? size : end % size);
		}

		return request;
	}
}
