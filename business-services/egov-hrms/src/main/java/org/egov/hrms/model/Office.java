package org.egov.hrms.model;

import java.util.List;

import org.egov.hrms.web.contract.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
@Builder
public class Office {
	
	private Long id;
	
	private Long userId;
	
	private String officeId;
	
	private Long naaUserId;
	
	private Boolean active;
	
	private AuditDetails auditDetails;

}
