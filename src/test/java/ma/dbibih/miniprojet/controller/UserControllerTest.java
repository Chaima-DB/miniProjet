package ma.dbibih.miniprojet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import ma.dbibih.miniprojet.entity.LoginDTO;
import ma.dbibih.miniprojet.entity.User;
import ma.dbibih.miniprojet.serviceImpl.UserDetailsServiceImpl;
import ma.dbibih.miniprojet.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    User userOne;
    User userTwo;
    LoginDTO loginUser;

    UserDetails userDetails;
    final List<GrantedAuthority> authoritiesUser = new ArrayList<>();
    List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userOne = User.builder().id(1L).userName("test.test").email("test@test.com").password("test")
                .firstName("test").lastName("test").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
        userTwo = User.builder().id(2L).userName("test.test").email("test@test.com").password("test")
                .firstName("test").lastName("test").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
        loginUser = LoginDTO.builder().username("username").password("password").build();

        authoritiesUser.add(new SimpleGrantedAuthority("USER"));
        userDetails = new org.springframework.security.core.userdetails.User(loginUser.getUsername(), loginUser.getPassword(), authoritiesUser);
        users.add(userOne);
        users.add(userTwo);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void UserController_GetUsers_Unauthorized() throws Exception {
        when(userService.getUsers()).thenReturn(users);
        this.mockMvc.perform(get("/api/users/")).andDo(print()).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER", "ADMIN"})
    void UserController_GetUsers_Authorized() throws Exception {
        // Map Users List to Json
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String usersJson = ow.writeValueAsString(users);

        // Return Users When Calling userService.getUsers
        when(userService.getUsers()).thenReturn(users);

        // Assert status 200 && userJson == response
        this.mockMvc.perform(get("/api/users/")).andDo(print()).andExpect(status().isOk()).andExpect(content().contentType("application/json")).andExpect(content().json(usersJson));
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void UserController_GetUserProfile_Admin() throws Exception {
        when(userService.getUserProfile(Mockito.anyString())).thenReturn(userOne);
        this.mockMvc.perform(get("/api/users/admin")).andDo(print()).andExpect(status().isOk());
    }


}