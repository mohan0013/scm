package org.egov.infra.mdms.controller;

import org.egov.infra.mdms.model.EntityRequest;
import org.egov.infra.mdms.model.EntityResponse;
import org.egov.infra.mdms.model.OfficeSearchCriteria;
import org.egov.infra.mdms.model.OrganizationSearchCriteria;
import org.egov.infra.mdms.service.MDMSAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(value = "/v1")
public class MDMSAdminController {
	
	@Autowired
	private MDMSAdminService adminService;
	
	@PostMapping("{entity}/_create")
	private ResponseEntity<EntityResponse> create(@PathVariable("entity") String entity, @RequestBody EntityRequest request) {
		EntityResponse response = adminService.create(entity, request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("{entity}/_update")
	private ResponseEntity<EntityResponse> update(@PathVariable("entity") String entity, @RequestBody EntityRequest request) {
		EntityResponse response = adminService.update(entity, request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("org/_search")
	private ResponseEntity<EntityResponse> searchOrganization(@RequestBody OrganizationSearchCriteria criteria) {
		EntityResponse response = adminService.searchOrganization(criteria);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("ofc/_search")
	private ResponseEntity<EntityResponse> searchOffice(@RequestBody OfficeSearchCriteria criteria) {
		EntityResponse response = adminService.searchOffice(criteria);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
