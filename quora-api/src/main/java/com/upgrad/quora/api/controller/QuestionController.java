package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    //Create Question Functionality

    @RequestMapping(method = RequestMethod.POST,path = "/question/create", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createquestion(@RequestHeader("authorization") final String authorization, QuestionRequest questionRequest) throws AuthorizationFailedException
    {
        String[] bearer=authorization.split("Bearer ");
        QuestionEntity questionEntity=new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        QuestionEntity question=questionService.createQuestion(questionEntity,bearer[1]);
        QuestionResponse questionResponse=new QuestionResponse().id(question.getUuid()).status("Question Created");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    //Get All Questions

    @RequestMapping(method = RequestMethod.GET,path="/question/all",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String[] bearer = authorization.split("Bearer ");
        List<QuestionEntity> questionEntities=questionService.getAllQuestions(bearer[1]);
        List<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<>();
        for(QuestionEntity ques:questionEntities){
            QuestionDetailsResponse detailsResponse=new QuestionDetailsResponse();
            detailsResponse.id(ques.getUuid()).content(ques.getContent());
            questionDetailsResponse.add(detailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponse,HttpStatus.OK);
    }

    //Delete Question
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException, UserNotFoundException {
        String[] bearer = authorization.split("Bearer ");

        QuestionEntity questionEntity = questionService.deleteQuestion(bearer[1], questionId);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        questionDeleteResponse.setId(questionEntity.getUuid());
        questionDeleteResponse.setStatus("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    //Get Question from User ID

    @RequestMapping(method = RequestMethod.GET,path = "/question/all/{userId}")
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestionByUserId(@RequestHeader("authorization") final String authorization, @PathVariable("userId") String userId)
            throws AuthorizationFailedException, UserNotFoundException
    {
        String[] bearer = authorization.split("Bearer ");
        List<QuestionEntity> questions = questionService.getAllQuestionsByUser(userId, bearer[1]);
        List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();
        for (QuestionEntity questionEntity : questions) {
            QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse();
            questionDetailResponse.setId(questionEntity.getUuid());
            questionDetailResponse.setContent(questionEntity.getContent());
            questionDetailResponses.add(questionDetailResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailResponses, HttpStatus.OK);
    }

    //Edit Question

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionId,QuestionEditRequest questionEditRequest)
            throws AuthorizationFailedException, InvalidQuestionException {
        String[] bearer = authorization.split("Bearer ");
        QuestionEntity questionEntity = questionService.editQuestion(bearer[1], questionId, questionEditRequest.getContent());
        QuestionEditResponse questionEditResponse = new QuestionEditResponse();
        questionEditResponse.setId(questionEntity.getUuid());
        questionEditResponse.setStatus("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}
