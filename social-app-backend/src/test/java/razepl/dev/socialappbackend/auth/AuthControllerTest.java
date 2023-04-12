package razepl.dev.socialappbackend.auth;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import razepl.dev.socialappbackend.user.User;
import razepl.dev.socialappbackend.user.interfaces.ServiceUser;
import razepl.dev.socialappbackend.user.interfaces.UserRepository;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    final void init() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("username", "password", authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    final void test_registerUser_successful_register() throws Exception {
        // given
        String password = "Abc1!l1.DKk";
        String name = "Adam";
        String surname = "Kowalski";
        String email = "andrzej@gmail.com";
        LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);
        boolean expected = false;

        ServiceUser user = new User(dateOfBirth, name, surname, email, password);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .content(AuthTestUtil.asJsonString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        boolean result = userRepository.findByName(name).isEmpty();

        // then
        Assertions.assertEquals(expected, result, "Registering user has failed!");
    }

    @ParameterizedTest
    @CsvSource({
            "Abcdla.dkk",
            "Abc1al1dDKk",
            "Abca!ln.DKk",
            "a",
            "ABCDEFGHIJK",
            "abcdefghijk"
    })
    final void test_registerUser_parametrized(String password) throws Exception {
        // given
        String expected = null;
        String name = "Adam";
        String surname = "Kowalski";
        String email = "andrzej@gmail.com";
        LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);

        ServiceUser user = new User(dateOfBirth, name, surname, email, password);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .content(AuthTestUtil.asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        String result = userRepository.findByName(name);

        // then
        Assertions.assertEquals(expected, result, "Registering user has failed!");
    }
}
