package fr.uha.AccountingFlowManager.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
/*
    private final UserDetails userDetails ;

    public SessionService() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
    }

    public String getRole(){
        return userDetails.getAuthorities().iterator().next().getAuthority();
    }
*/
}
