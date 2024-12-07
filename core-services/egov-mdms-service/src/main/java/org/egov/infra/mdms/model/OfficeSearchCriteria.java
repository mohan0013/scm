package org.egov.infra.mdms.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeSearchCriteria {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private Long id;
	private Long organizationId;
	private List<String> codes;
	private String HeadOfficeCode;
	private String status;
	
	@Default
	private boolean headOffice=true;
}
