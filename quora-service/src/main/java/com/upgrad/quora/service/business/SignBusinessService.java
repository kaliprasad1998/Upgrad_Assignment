package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class SignBusinessService {

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(final UserEntity userEntity) throws SignUpRestrictedException
    {

       if(userDao.getUserbyUsername(userEntity.getUsername())!=null){
           throw new SignUpRestrictedException( "SGR-001", "Try any other Username, this Username has already been taken");

       }
       if(userDao.getUserbyEmail(userEntity.getEmail())!=null){
           throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
       }
       // Assign encrypted password and salt to the user that is being created.
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signin(final String username, final String password) throws AuthenticationFailedException
    {
        UserEntity userEntity=userDao.getUserbyUsername(username);

        if(userEntity==null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        String encryptedPassword= PasswordCryptographyProvider.encrypt(password,userEntity.getSalt());

        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider=new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity=new UserAuthEntity();
            userAuthEntity.setUuid(UUID.randomUUID().toString());
            userAuthEntity.setUser(userEntity);
            final ZonedDateTime now=ZonedDateTime.now();
            final ZonedDateTime expiresAt=now.plusHours(8);
            userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now, expiresAt));

            userAuthEntity.setLoginAt(now);
            userAuthEntity.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthEntity);

            userDao.updateuser(userEntity);

            return userAuthEntity;
        }
        else{
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String authorizationtoken) throws SignOutRestrictedException
    {
        UserAuthEntity userAuthEntity=userDao.getUserAuthByToken(authorizationtoken);

        if(userAuthEntity==null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        userAuthEntity.setLogoutAt(ZonedDateTime.now());
        userDao.updateUserAuth(userAuthEntity);
        UserEntity userEntity=userAuthEntity.getUser();
        return userEntity;
    }

}
