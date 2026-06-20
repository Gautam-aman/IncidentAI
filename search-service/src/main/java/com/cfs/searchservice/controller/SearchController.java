package com.cfs.searchservice.controller;

import java.util.List;

import com.cfs.searchservice.document.IncidentDocument;
import com.cfs.searchservice.service.SearchService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@GetMapping
	public List<IncidentDocument> search(
			@RequestParam String query
	) {

		return searchService.search(query);
	}
}
