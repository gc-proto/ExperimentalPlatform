package ca.canada.treasury.testbed.web.service;

import java.io.IOException;

import ca.canada.treasury.testbed.web.model.Recall;
import ca.canada.treasury.testbed.web.model.SearchRequest;
import ca.canada.treasury.testbed.web.model.SearchResponse;

/**
 * QuickSearch service.
 */
public interface ISearchService {

    SearchResponse<Recall> searchRecalls(
            SearchRequest searchRequest) throws IOException;

    /**
     * Only exists since we are building a quick and dirty prototype.
     * A real solution would not have this but rather a finalized domain
     * model and processing logic done "within" the services.
     * @param <R> native response type
     * @param <Q> native query type
     * @param query native query
     * @return native response
     * @throws IOException problem executing search
     */
    <R, Q> R badIdeaNativeSearch(Q query) throws IOException;
}
