package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.GET,path = "/userProfile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId") final String uuid,
                                                           @RequestHeader("authorization") final String authorization) throws UserNotFoundException, AuthorizationFailedException {
        String [] bearerToken = authorization.split("Bearer ");
        final UserEntity userEntity = userBusinessService.getUserDetails(uuid,bearerToken[1]);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().userName(userEntity.getUsername()).aboutMe(userEntity.getAboutMe())
                .contactNumber(userEntity.getContactNumber()).country(userEntity.getCountry()).emailAddress(userEntity.getEmail())
                .dob(userEntity.getDob()).firstName(userEntity.getFirstName()).lastName(userEntity.getLastName());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

}
