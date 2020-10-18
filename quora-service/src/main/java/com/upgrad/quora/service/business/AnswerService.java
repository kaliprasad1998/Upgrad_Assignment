package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerService {
    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity, String questionId, final String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionById = questionDao.getQuestionById(questionId);
        if(questionById==null){
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to post a question");
        }
        answerEntity.setQuestionEntity(questionById);
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUuid(UUID.randomUUID().toString());
        //answerEntity.setUserEntity(userAuthEntity.getUserEntity());
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerByContent(final String accessToken, final String answerId, final String content)
            throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        if (!answerEntity
                .getUserEntity()
                .getUuid()
                .equals(userAuthEntity.getUserEntity().getUuid())) {
            throw new AuthorizationFailedException(
                    "ATHR-003", "Only the answer owner can edit the answer");
        }
        answerEntity.setContent(content);
        answerDao.updateAnswer(answerEntity);
        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String accessToken, final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out. Sign in first to delete an answer");
        }
        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if(answerEntity==null)
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist'");

        if (!answerEntity.getUserEntity().getUuid().equals(userAuthEntity.getUserEntity().getUuid())
                && !userAuthEntity.getUserEntity().getRole().equals("admin")) {
            throw new AuthorizationFailedException(
                    "ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        answerDao.deleteAnswer(answerEntity);
        return answerEntity;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswersToQuestion(String questionId, String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out. Sign in first to delete an answer");
        }
        return answerDao.getAllAnswers();
    }
}
