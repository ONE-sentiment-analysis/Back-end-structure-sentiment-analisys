package br.com.one.sentiment_analysis.service;

import br.com.one.sentiment_analysis.config.UserAuthenticated;
import br.com.one.sentiment_analysis.exception.UserNotFoundException;
import br.com.one.sentiment_analysis.model.user.User;
import br.com.one.sentiment_analysis.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuário não encontrado"));

        UserAuthenticated userAuthenticated = new UserAuthenticated(
                user.getId(),
                user.getEmail(),
                user.getSenha(),
                user.getRole().name()
        );

        return userAuthenticated;
    }
}
