package org.egov.infra.mdms.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

import org.egov.infra.mdms.dto.Office;
import org.egov.infra.mdms.dto.Organization;
import org.egov.infra.mdms.model.EntityRequest;
import org.egov.infra.mdms.model.EntityResponse;
import org.egov.infra.mdms.model.OfficeSearchCriteria;
import org.egov.infra.mdms.model.OrganizationSearchCriteria;
import org.egov.infra.mdms.repository.MDMSAdminRepository;
import org.egov.infra.mdms.utils.MDMSConstants;
import org.egov.infra.mdms.utils.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class MDMSAdminService {
	
	@Autowired
	private MDMSAdminRepository adminRepository;
	
	@Autowired
	private ResponseInfoFactory factory;

	public JSONArray getOrganization() {
		List<Organization> organizations = adminRepository.fetchOrganization(null);
		return JsonPath.read(JSONArray.toJSONString(organizations),"$");
	}

	public JSONArray getOffice() {
		List<Office> offices = adminRepository.fetchOffice(null);
		return JsonPath.read(JSONArray.toJSONString(offices),"$");
	}

	public EntityResponse create(String entity, EntityRequest request) {
		EntityResponse response;
		if(entity.equalsIgnoreCase(MDMSConstants.ORGANIZATION)) {
			validateOrganization(request);
			adminRepository.createOrganization(request);
			response = EntityResponse.builder().responseInfo(factory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
					.organizations(request.getOrganizations()).build();
		} else if(entity.equalsIgnoreCase(MDMSConstants.OFFICE)) {
			validateOffice(request);
			adminRepository.createOffice(request);
			response = EntityResponse.builder().responseInfo(factory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
					.offices(request.getOffices()).build();
		} else {
			throw new CustomException("INVALID_REQUEST", "Not Supported");
		}
		return response;
	}
	
	public EntityResponse update(String entity, EntityRequest request) {
		EntityResponse response;
		if(entity.equalsIgnoreCase(MDMSConstants.ORGANIZATION)) {
			adminRepository.updateOrganization(request);
			response = EntityResponse.builder().responseInfo(factory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
					.organizations(request.getOrganizations()).build();
		} else if(entity.equalsIgnoreCase(MDMSConstants.OFFICE)) {
			adminRepository.updateOffice(request);
			response = EntityResponse.builder().responseInfo(factory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
					.offices(request.getOffices()).build();
		} else {
			throw new CustomException("INVALID_REQUEST", "Not Supported");
		}
		return response;
		
	}

	private void validateOffice(EntityRequest request) {
		if(request.getOffices().isEmpty()) {
			throw new CustomException("INVALID_REQUEST", "No office details found in the request.");
		}
		
		OfficeSearchCriteria criteria = OfficeSearchCriteria.builder().requestInfo(request.getRequestInfo())
				.codes(request.getOffices().stream().map(Office::getCode).collect(Collectors.toList())).build();
		List<Office> offices = adminRepository.fetchOffice(criteria);
		if(offices.size()>0) {
			throw new CustomException("INVALID_REQUEST", "Same office already exists.");
		}
	}

	private void validateOrganization(EntityRequest request) {
		if(request.getOrganizations().isEmpty()) {
			throw new CustomException("INVALID_REQUEST", "No organization details found in the request.");
		}
		
		OrganizationSearchCriteria criteria = OrganizationSearchCriteria.builder().requestInfo(request.getRequestInfo())
				.codes(request.getOrganizations().stream().map(Organization::getCode).collect(Collectors.toList())).build();
		List<Organization> organizations = adminRepository.fetchOrganization(criteria);
		if(organizations.size()>0) {
			throw new CustomException("INVALID_REQUEST", "Same organization already exists.");
		}
	}

	public EntityResponse searchOrganization(OrganizationSearchCriteria criteria) {
		List<Organization> organizations = adminRepository.fetchOrganization(criteria);
		return EntityResponse.builder().responseInfo(factory.createResponseInfoFromRequestInfo(criteria.getRequestInfo(), true))
				.organizations(organizations).build();
	}
	
	public EntityResponse searchOffice(OfficeSearchCriteria criteria) {
		List<Office> offices = adminRepository.fetchOffice(criteria);
		return EntityResponse.builder().responseInfo(factory.createResponseInfoFromRequestInfo(criteria.getRequestInfo(), true))
				.offices(offices).build();
	}

}