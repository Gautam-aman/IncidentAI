package com.cfs.searchservice.service;


import java.util.List;

import com.cfs.searchservice.document.IncidentDocument;
import com.cfs.searchservice.repository.IncidentSearchRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

		private final IncidentSearchRepository incidentSearchRepository;

		public void index(IncidentDocument incidentDocument) {
			incidentSearchRepository.save(incidentDocument);
		}

		public List<IncidentDocument> search(String query) {
			return incidentSearchRepository.findByTitleContainingOrDescriptionContaining(query, query);
		}

}
