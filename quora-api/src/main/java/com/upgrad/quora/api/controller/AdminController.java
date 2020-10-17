package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.upgrad.quora.api.model.UserDetailsResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @RequestMapping(method = RequestMethod.DELETE,path = "/admin/user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String uuid,
                                                         @RequestHeader("authorization") final String authorization) throws UserNotFoundException, AuthorizationFailedException{

        String [] bearerToken = authorization.split("Bearer ");
        userAdminBusinessService.deleteUser(uuid,bearerToken[1]);

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse();
        userDeleteResponse.setId(uuid);
        userDeleteResponse.setStatus("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);
    }
}
