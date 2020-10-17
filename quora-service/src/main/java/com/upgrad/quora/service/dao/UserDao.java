package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserbyId(final String userId) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public boolean deleteUser(final String uuid){
        try {
            entityManager.createNamedQuery("deleteUserByUuid",UserEntity.class).setParameter("uuid", uuid).executeUpdate();
            entityManager.flush();
            entityManager.refresh(getUserbyId(uuid));

            return true;

        } catch (Exception e){
            return false;
        }
    }

    public UserEntity getUserbyUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserbyEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email",email).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }
    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }
    public void updateuser(final UserEntity updateduserEntity) {
        entityManager.merge(updateduserEntity);
    }

    public UserAuthEntity getUserAuthByToken(final String accessToken){
        try {
            return entityManager.createNamedQuery("UserAuthByAccessToken", UserAuthEntity.class).setParameter("accessToken",accessToken).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateUserAuth(final UserAuthEntity userAuthEntity){
        entityManager.merge(userAuthEntity);
    }
}
