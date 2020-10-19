package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createquestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(){
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestionByID(String questionId) {
        try {
            return entityManager.createNamedQuery("getQuestionByID", QuestionEntity.class).setParameter("uuid",questionId).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    public List<QuestionEntity> getAllQuestionsByUser(UserEntity user) {
        return entityManager.createNamedQuery("getQuestionByUser", QuestionEntity.class).setParameter("user", user).getResultList();
    }

    public void updateQuestion(QuestionEntity questionEntity) {
            entityManager.merge(questionEntity);
    }
}
