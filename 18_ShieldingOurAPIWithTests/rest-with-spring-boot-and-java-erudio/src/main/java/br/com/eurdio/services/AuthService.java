package br.com.eurdio.services;

import br.com.eurdio.data.vO.v1.security.AccountCredentialsVO;
import br.com.eurdio.data.vO.v1.security.TokenVO;
import br.com.eurdio.repository.UserRepository;
import br.com.eurdio.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    public ResponseEntity signIn(AccountCredentialsVO data){
        try{
            var username = data.getUsername();
            var password = data.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            var user = repository.findByUsername(username);

            var tokenResponse = new TokenVO();
            
            if (user != null){
                tokenResponse = tokenProvider.createAcessToken(username, user.getRoles());
            }else {
                throw new UsernameNotFoundException("Username " + username + " not Found!");
            }

            return ResponseEntity.ok(tokenResponse);
        }catch (Exception e){
            throw new BadCredentialsException("Ivalid username/password supplied");
        }
    }

}
