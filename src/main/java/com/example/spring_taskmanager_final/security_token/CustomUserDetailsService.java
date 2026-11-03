package com.example.spring_taskmanager_final.security_token;

import com.example.spring_taskmanager_final.model.User;
import com.example.spring_taskmanager_final.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       //Шукаємо юзера в БД через репозиторій
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("User Not Found: " + username);
        }

        // 2. Перетворюємо сутність User в стандартний Spring Security UserDetails
        // Метод .roles() автоматично  добавить префікс "ROLE_" (наприклад, "ROLE_USER" или "ROLE_ADMIN")
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // тут має бути захешироований пароль из БД!
                .roles(user.getRole())  // Твоя роль з базы (наприклад "ADMIN")
                .build();
    }
}
