package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public List<AnswerEntity> getAllAnswers() {
        return entityManager.createNamedQuery("getAllAnswers", AnswerEntity.class).getResultList();
    }

    public AnswerEntity getAnswerById(final String questionId) {
        try {
            return entityManager
                    .createNamedQuery("getAnswerById", AnswerEntity.class)
                    .setParameter("uuid", questionId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
    }

    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    public List<AnswerEntity> getAllAnswersForQuestion(final String questionId){
        return entityManager
                .createNamedQuery("getAllAnswersPerQuestion", AnswerEntity.class)
                .setParameter("questionId", questionId)
                .getResultList();
    }
}
