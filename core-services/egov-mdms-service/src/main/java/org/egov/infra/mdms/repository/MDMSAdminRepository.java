package org.egov.infra.mdms.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.egov.infra.mdms.dto.Office;
import org.egov.infra.mdms.dto.Organization;
import org.egov.infra.mdms.model.EntityRequest;
import org.egov.infra.mdms.model.OfficeSearchCriteria;
import org.egov.infra.mdms.model.OrganizationSearchCriteria;
import org.egov.infra.mdms.repository.rowMapper.OfficeResultExtractor;
import org.egov.infra.mdms.repository.rowMapper.OrganizationResultExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MDMSAdminRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private OrganizationResultExtractor organizationResultExtractor;

	@Autowired
	private OfficeResultExtractor officeResultExtractor;

	public List<Organization> fetchOrganization(OrganizationSearchCriteria criteria) {
		StringBuilder searchQuery = new StringBuilder("select id, code, name, description, hod, email_id, telephone_number, address, district, sub_district, state, pin, status from eg_organizations");
		List<Object> paramList = new ArrayList<>();
		if(criteria!= null && !StringUtils.isEmpty(criteria.getCodes())) {
			searchQuery.append(addWhereOrAndClause(paramList));
			searchQuery.append(" code in ( ").append(getQueryForCollection(criteria.getCodes(), paramList)).append(" )");
		}
		
		if(criteria!= null && !ObjectUtils.isEmpty(criteria.getId())) {
			searchQuery.append(addWhereOrAndClause(paramList));
			searchQuery.append(" id = ?");
			paramList.add(criteria.getId());
		}
		
		if(criteria!= null && !StringUtils.isEmpty(criteria.getStatus())) {
			searchQuery.append(addWhereOrAndClause(paramList));
			searchQuery.append(" status = ?");
			paramList.add(criteria.getStatus());
		}
		
		List<Organization> organizations = jdbcTemplate.query(searchQuery.toString(), paramList.toArray(), organizationResultExtractor);
		
		return organizations;
	}

	public List<Office> fetchOffice(OfficeSearchCriteria criteria) {
		StringBuilder searchQuery = new StringBuilder("select id, code, organization_id, name, description, email_id, telephone_number, office_address, district, sub_district, state, pin, status, Head_office_code, Head_office from eg_offices");
		List<Object> paramList = new ArrayList<>();
		if(criteria!= null && !CollectionUtils.isEmpty(criteria.getCodes())) {
			searchQuery.append(addWhereOrAndClause(paramList));
			searchQuery.append(" code in (").append(getQueryForCollection(criteria.getCodes(), paramList)).append(" )");;
		}
		
		if(criteria!= null && !ObjectUtils.isEmpty(criteria.getId())) {
			searchQuery.append(addWhereOrAndClause(paramList));
			searchQuery.append(" id = ?");
			paramList.add(criteria.getId());
		}
		
		if(criteria!= null && !StringUtils.isEmpty(criteria.getStatus())) {
			searchQuery.append(addWhereOrAndClause(paramList));
			searchQuery.append(" status = ?");
			paramList.add(criteria.getStatus());
		}
		
		if(criteria!= null && !StringUtils.isEmpty(criteria.getHeadOfficeCode())) {
			searchQuery.append(addWhereOrAndClause(paramList));
			searchQuery.append(" head_office_code = ?");
			paramList.add(criteria.getHeadOfficeCode());
		}
		
		if(criteria!= null && !ObjectUtils.isEmpty(criteria.getOrganizationId())) {
			searchQuery.append(addWhereOrAndClause(paramList));
			searchQuery.append(" organization_id = ?");
			paramList.add(criteria.getOrganizationId());
		}
		
		searchQuery.append(addWhereOrAndClause(paramList));
		searchQuery.append(" head_office = ?");
		paramList.add(criteria.isHeadOffice());
		
		List<Office> offices = jdbcTemplate.query(searchQuery.toString(), paramList.toArray(), officeResultExtractor);
		
		return offices;
	}

	public void createOffice(EntityRequest request) {
		String query = "INSERT INTO public.eg_offices"
				+ " (organization_id, code, name, description, email_id, telephone_number, head_office_code, office_address, district, sub_district, state, pin, status, head_office)"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Office office = request.getOffices().get(i);
				ps.setLong(1, office.getOrganizationId());
				ps.setString(2, office.getCode());
				ps.setString(3, office.getName());
				ps.setString(4, office.getDescription());
				ps.setString(5, office.getEmailId());
				ps.setString(6, office.getTelephoneNumber());
				ps.setString(7, office.getHeadOfficeCode());
				ps.setString(8, office.getOfficeAddress());
				ps.setString(9, office.getDistrict());
				ps.setString(10, office.getSubDistrict());
				ps.setString(11, office.getState());
				ps.setString(12, office.getPin());
				ps.setString(13, office.getStatus());
				ps.setBoolean(14, office.isHeadOffice());
			}

			@Override
			public int getBatchSize() {
				return request.getOffices().size();
			}
		});
	}

	public void createOrganization(EntityRequest request) {
		String query = "INSERT INTO public.eg_organizations"
				+ " (code, name, description, hod, email_id, telephone_number, address, district, sub_district, state, pin, status)"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Organization org = request.getOrganizations().get(i);

				ps.setString(1, org.getCode());
				ps.setString(2, org.getName());
				ps.setString(3, org.getDescription());
				ps.setString(4, org.getHod());
				ps.setString(5, org.getEmailId());
				ps.setString(6, org.getTelephoneNumber());
				ps.setString(7, org.getAddress());
				ps.setString(8, org.getDistrict());
				ps.setString(9, org.getSubDistrict());
				ps.setString(10, org.getState());
				ps.setString(11, org.getPin());
				ps.setString(12, org.getStatus());
			}

			@Override
			public int getBatchSize() {
				return request.getOrganizations().size();
			}
		});
	}

	public void updateOrganization(EntityRequest request) {
		String query = "UPDATE public.eg_organizations"
				+ " SET code=?, name=?, description=?, hod=?, email_id=?, telephone_number=?, address=?, district=?, sub_district=?, state=?, pin=?, status=?"
				+ " WHERE id=?";

		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Organization org = request.getOrganizations().get(i);

				ps.setString(1, org.getCode());
				ps.setString(2, org.getName());
				ps.setString(3, org.getDescription());
				ps.setString(4, org.getHod());
				ps.setString(5, org.getEmailId());
				ps.setString(6, org.getTelephoneNumber());
				ps.setString(7, org.getAddress());
				ps.setString(8, org.getDistrict());
				ps.setString(9, org.getSubDistrict());
				ps.setString(10, org.getState());
				ps.setString(11, org.getPin());
				ps.setString(12, org.getStatus());
				ps.setLong(13, org.getId());
			}

			@Override
			public int getBatchSize() {
				return request.getOrganizations().size();
			}
		});

	}

	public void updateOffice(EntityRequest request) {
		String query = "UPDATE public.eg_offices"
				+ " SET organization_id=?, code=?, name=?, description=?, email_id=?, telephone_number=?, head_office_code=?, office_address=?, district=?, sub_district=?, state=?, pin=?, status=?, head_office=?"
				+ " WHERE id=?";

		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Office office = request.getOffices().get(i);
				ps.setLong(1, office.getOrganizationId());
				ps.setString(2, office.getCode());
				ps.setString(3, office.getName());
				ps.setString(4, office.getDescription());
				ps.setString(5, office.getEmailId());
				ps.setString(6, office.getTelephoneNumber());
				ps.setString(7, office.getHeadOfficeCode());
				ps.setString(8, office.getOfficeAddress());
				ps.setString(9, office.getDistrict());
				ps.setString(10, office.getSubDistrict());
				ps.setString(11, office.getState());
				ps.setString(12, office.getPin());
				ps.setString(13, office.getStatus());
				ps.setBoolean(14, office.isHeadOffice());
				ps.setLong(15, office.getId());
			}

			@Override
			public int getBatchSize() {
				return request.getOffices().size();
			}
		});

	}

	public String addWhereOrAndClause(List<Object> paramList) {
		if(paramList.isEmpty()) {
			return " where ";
		} else {
			return " and ";
		}
	}
	
	private String getQueryForCollection(List<?> ids, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = ids.iterator();
        while (iterator.hasNext()) {
            builder.append(" ?");
            preparedStmtList.add(iterator.next());

            if (iterator.hasNext())
                builder.append(",");
        }
        return builder.toString();
    }

}
