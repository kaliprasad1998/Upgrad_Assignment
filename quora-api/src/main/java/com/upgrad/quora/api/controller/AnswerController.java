package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    //Create Answer method
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/question/{questionId}/answer/create",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String accessToken,
                                                       @PathVariable("questionId") final String questionId,
                                                       AnswerRequest answerRequest)
            throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setContent(answerRequest.getAnswer());
        final AnswerEntity answer = answerService.createAnswer(answerEntity, questionId, accessToken);
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setId(answer.getUuid());
        answerResponse.setStatus("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    //edit Answer method
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/answer/edit/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(AnswerEditRequest answerEditRequest,
                                                                @PathVariable("answerId") final String answerId,
                                                                @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setContent(answerEditRequest.getContent());
        answerEntity.setUuid(answerId);
        AnswerEntity updatedAnswerEntity = answerService.editAnswerByContent(accessToken, answerId, answerEditRequest.getContent());

        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        answerEditResponse.setId(updatedAnswerEntity.getUuid());
        answerEditResponse.setStatus("ANSWER EDITED");

        return new ResponseEntity<>(answerEditResponse, HttpStatus.OK);
    }

    //delete Answer method
    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("answerId") final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerService.deleteAnswer(accessToken, answerId);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse();
        answerDeleteResponse.setId(answerEntity.getUuid());
        answerDeleteResponse.setStatus("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    //get all answers for Question.
    @RequestMapping(
            method = RequestMethod.GET,
            path = "answer/all/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("questionId") String questionId)
            throws AuthorizationFailedException {

        List<AnswerEntity> answers = answerService.getAllAnswersToQuestion(questionId, accessToken);
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
        for (AnswerEntity answerEntity : answers) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setId(answerEntity.getUuid());
            answerDetailsResponse.setAnswerContent(answerEntity.getContent());
            answerDetailsResponses.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(
                answerDetailsResponses, HttpStatus.OK);
    }

}
