package com.cfs.searchservice.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "incidents")
public class IncidentDocument {

	@Id
	private String id;

	private String title;

	private String description;

	private String priority;

	private String severity;

	private String status;

}
