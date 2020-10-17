package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserDetails(final String userId,final String authorizationToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
        String role = userAuthEntity.getUser().getRole();

        if (role != null) {
            UserEntity userEntity = userDao.getUserbyId(userId);
            if (userEntity == null) {
                throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
            }
            return userEntity;
        }
        throw new AuthorizationFailedException("ATHR-001","User has not signed in");
    }

}
