package com.supportportal.supportportal.resource;

import com.supportportal.supportportal.exception.ExceptionHandling;
import com.supportportal.supportportal.exception.domain.EmailExistException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling {

    @GetMapping("/home")
    public String showUser() throws UsernameNotFoundException{
//        return "Application works";
        throw new UsernameNotFoundException("The user was not found.");
    }

}
