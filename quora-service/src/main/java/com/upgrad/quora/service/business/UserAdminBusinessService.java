package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(final String userId,final String authorizationToken) throws UserNotFoundException,AuthorizationFailedException{

        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
        UserEntity adminUser =  userAuthEntity.getUser();
        String role = adminUser.getRole();
        UserEntity userEntity = userDao.getUserbyId(userId);

        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(adminUser == null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }else if(role.equalsIgnoreCase("admin") == false ) {
            throw new AuthorizationFailedException ("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }else if(userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }else{
            userDao.deleteUser(userId);
        }

    }

}
