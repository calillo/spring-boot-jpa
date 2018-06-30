package com.rest.api.web.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Event that is fired when a paginated search is performed.
 * <p/>
 * This event object contains all the information needed to create the URL for the paginated results
 *
 * @param <T>
 *            Type of the result that is being handled (commonly Entities).
 */
public final class PaginatedResultsRetrievedEvent<T> extends ApplicationEvent {
    private final UriComponentsBuilder uriBuilder;
    private final HttpServletResponse response;
    private final long totalCount;
    private final int page;
    private final int totalPages;
    private final int pageSize;

    public PaginatedResultsRetrievedEvent(Object source, final UriComponentsBuilder uriBuilderToSet, final HttpServletResponse responseToSet, final long totalCountToSet, final int pageToSet, final int totalPagesToSet, final int pageSizeToSet) {
    	super(source);

        uriBuilder = uriBuilderToSet;
        response = responseToSet;
        totalCount = totalCountToSet;
        page = pageToSet;
        totalPages = totalPagesToSet;
        pageSize = pageSizeToSet;
    }

    public final UriComponentsBuilder getUriBuilder() {
        return uriBuilder;
    }

    public final HttpServletResponse getResponse() {
        return response;
    }

    public long getTotalCount() {
		return totalCount;
	}

	public final int getPage() {
        return page;
    }

    public final int getTotalPages() {
        return totalPages;
    }

    public final int getPageSize() {
        return pageSize;
    }

}
