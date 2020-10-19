package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    //create question post method for posting questions on the application

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity,String AccessToken) throws AuthorizationFailedException
    {
        UserAuthEntity userAuthEntity=userDao.getUserAuthByToken(AccessToken);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUserEntity(userAuthEntity.getUser());
        return questionDao.createquestion(questionEntity);
    }

    //display all questions

    public List<QuestionEntity> getAllQuestions(String AccessToken) throws AuthorizationFailedException{

        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(AccessToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }
        List<QuestionEntity> questionEntity = questionDao.getAllQuestions();
        return questionEntity;
    }

    //delete question by the user
    public QuestionEntity deleteQuestion(final String accessToken, final String questionId) throws AuthorizationFailedException, InvalidQuestionException, UserNotFoundException {
        UserAuthEntity userAuthEntity=userDao.getUserAuthByToken(accessToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }
        QuestionEntity questionEntity=questionDao.getQuestionByID(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (!questionEntity.getUserEntity().getUuid().equals(userAuthEntity.getUser().getUuid())
                && !userAuthEntity.getUser().getRole().equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        questionDao.deleteQuestion(questionEntity);
        return questionEntity;
    }

        //get all questions posted by a user
    public List<QuestionEntity> getAllQuestionsByUser(String userId, String accessToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity=userDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
        }
        UserEntity user = userDao.getUserbyId(userId);
        if (user == null) {
            throw new UserNotFoundException(
                    "USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUser(user);
    }

    //Editing a question posted by a user
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(final String accessToken, final String questionId, final String content) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(accessToken);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        QuestionEntity questionEntity = questionDao.getQuestionByID(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (!questionEntity.getUserEntity().getUuid().equals(userAuthEntity.getUser().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        questionEntity.setContent(content);
        questionDao.updateQuestion(questionEntity);
        return questionEntity;
    }




}
