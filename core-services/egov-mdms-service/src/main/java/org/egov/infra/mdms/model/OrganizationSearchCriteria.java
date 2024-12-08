package org.egov.infra.mdms.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.infra.mdms.dto.Organization;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationSearchCriteria {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private String tenantId;
	
	private Long id;
	
	private List<String> codes;
	
	private String status;
}
