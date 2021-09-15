package org.egov.egovsurveyservices.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.egovsurveyservices.producer.Producer;
import org.egov.egovsurveyservices.repository.SurveyRepository;
import org.egov.egovsurveyservices.validators.SurveyValidator;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.egov.egovsurveyservices.utils.SurveyServiceConstants.*;

@Slf4j
@Service
public class SurveyService {

    @Autowired
    private SurveyValidator surveyValidator;

    @Autowired
    private Producer producer;

    @Autowired
    private EnrichmentService enrichmentService;

    @Autowired
    private SurveyRepository surveyRepository;

    public SurveyEntity createSurvey(SurveyRequest surveyRequest) {

        SurveyEntity surveyEntity = surveyRequest.getSurveyEntity();

        // Validate whether usertype employee is trying to create survey.
        surveyValidator.validateUserType(surveyRequest.getRequestInfo());
        // Validate question types.
        surveyValidator.validateQuestions(surveyEntity);
        // Validate survey uniqueness.
        surveyValidator.validateSurveyUniqueness(surveyEntity);

        // Enrich survey entity
        enrichmentService.enrichSurveyEntity(surveyRequest);

        // Persist survey if it passes all validations
        List<String> listOfTenantIds = new ArrayList<>(surveyEntity.getTenantIds());
        listOfTenantIds.forEach(tenantId ->{
            surveyEntity.setTenantId(tenantId);
            producer.push("save-ss-survey", surveyRequest);
        });

        return surveyEntity;
    }

    public List<SurveyEntity> searchSurveys(SurveySearchCriteria criteria) {
        List<SurveyEntity> surveyEntities = surveyRepository.fetchSurveys(criteria);

        if(CollectionUtils.isEmpty(surveyEntities))
            return new ArrayList<>();

        return surveyEntities;
    }

    public void submitResponse(AnswerRequest answerRequest) {
        RequestInfo requestInfo = answerRequest.getRequestInfo();
        AnswerEntity answerEntity = answerRequest.getAnswerEntity();

        // Validations

        // 1. Validate whether userType is citizen or not
        surveyValidator.validateUserTypeForAnsweringSurvey(requestInfo);
        // 2. Validate if citizen has already responded or not
        surveyValidator.validateWhetherCitizenAlreadyResponded(answerEntity, requestInfo.getUserInfo().getUuid());
        // 3. Validate answers
        surveyValidator.validateAnswers(answerEntity);
        
        Boolean isAnonymousSurvey = fetchSurveyAnonymitySetting(answerEntity.getSurveyId());

        // Enrich answer request
        enrichmentService.enrichAnswerEntity(answerRequest, isAnonymousSurvey);

        // Persist response if it passes all validations
        producer.push("save-ss-answer", answerRequest);
    }

    private Boolean fetchSurveyAnonymitySetting(String surveyId) {
        if(ObjectUtils.isEmpty(surveyId))
            throw new CustomException("EG_SY_ANONYMITY_SETTING_FETCH_ERR", "Cannot fetch anonymity setting if surveyId is empty or null");
        return surveyRepository.fetchAnonymitySetting(surveyId);
    }

    public List<Question> fetchQuestionListBasedOnSurveyId(String surveyId) {
        List<Question> questionList = surveyRepository.fetchQuestionsList(surveyId);
        if(CollectionUtils.isEmpty(questionList))
            return new ArrayList<>();
        return questionList;
    }

    public boolean hasCitizenAlreadyResponded(AnswerEntity answerEntity, String citizenId) {
        if(ObjectUtils.isEmpty(answerEntity.getSurveyId()))
            throw new CustomException("EG_SY_FETCH_CITIZEN_RESP_ERR", "Cannot fetch citizen's response without surveyId");
        return surveyRepository.fetchWhetherCitizenAlreadyResponded(answerEntity.getSurveyId(), citizenId);
    }

    public SurveyEntity updateSurvey(SurveyRequest surveyRequest) {
        SurveyEntity surveyEntity = surveyRequest.getSurveyEntity();
        RequestInfo requestInfo = surveyRequest.getRequestInfo();
        // Validate survey existence
        surveyValidator.validateSurveyExistence(surveyEntity);
        // Validate whether usertype employee is trying to update survey.
        surveyValidator.validateUserType(surveyRequest.getRequestInfo());
        // Validate question types.
        surveyValidator.validateQuestions(surveyEntity);
        // Enrich update request
        surveyEntity.setPostedBy(requestInfo.getUserInfo().getName());
        surveyEntity.getAuditDetails().setLastModifiedBy(requestInfo.getUserInfo().getUuid());
        surveyEntity.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
        surveyEntity.getQuestions().forEach(question -> {
            question.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
            question.getAuditDetails().setLastModifiedBy(requestInfo.getUserInfo().getUuid());
        });

        // Update survey if it passes all validations
        producer.push("update-ss-survey", surveyRequest);

        return surveyEntity;
    }

    public void deleteSurvey(SurveyRequest surveyRequest) {
        SurveyEntity surveyEntity = surveyRequest.getSurveyEntity();

        // Validate survey existence
        surveyValidator.validateSurveyExistence(surveyEntity);
        // Validate whether usertype employee is trying to delete survey.
        surveyValidator.validateUserType(surveyRequest.getRequestInfo());

        producer.push("delete-ss-survey", surveyRequest);

    }

    public AnswerResponse fetchSurveyResults(RequestInfo requestInfo, SurveyResultsSearchCriteria criteria) {

        // Validate whether employee is trying to fetch survey results
        surveyValidator.validateUserType(requestInfo);

        // Validate survey exists
        List<SurveyEntity> surveyEntities = surveyRepository.fetchSurveys(SurveySearchCriteria.builder().uuid(criteria.getSurveyId()).build());

        if(CollectionUtils.isEmpty(surveyEntities))
            throw new CustomException("EG_SY_DOES_NOT_EXIST_ERR", "The provided survey does not exist");


        // Fetch citizens who responded
        List<String> listOfCitizensWhoResponded = surveyRepository.fetchCitizensUuid(criteria);
        log.info(listOfCitizensWhoResponded.toString());

        // Fetch answers given by the fetched citizens for the requested survey
        List<Answer> answers = surveyRepository.fetchSurveyResults(SurveyResultsSearchCriteria.builder().citizenUuids(listOfCitizensWhoResponded).surveyId(criteria.getSurveyId()).build());

        AnswerResponse response = AnswerResponse.builder()
                                                .answers(answers)
                                                .surveyId(surveyEntities.get(0).getUuid())
                                                .title(surveyEntities.get(0).getTitle())
                                                .tenantId(surveyEntities.get(0).getTenantId())
                                                .description(surveyEntities.get(0).getDescription())
                                                .build();
        return response;
    }
}
