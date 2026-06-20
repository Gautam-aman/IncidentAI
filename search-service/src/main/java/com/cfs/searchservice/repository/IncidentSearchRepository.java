package com.cfs.searchservice.repository;

import java.util.List;

import com.cfs.searchservice.document.IncidentDocument;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IncidentSearchRepository extends ElasticsearchRepository<IncidentDocument , String > {
	List<IncidentDocument>
	findByTitleContainingOrDescriptionContaining(
			String title,
			String description
	);
}
