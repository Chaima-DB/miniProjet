package ma.dbibih.miniprojet.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.dbibih.miniprojet.entity.LoginDTO;
import ma.dbibih.miniprojet.entity.User;
import ma.dbibih.miniprojet.entity.UserDTO;
import ma.dbibih.miniprojet.service.UserService;
import ma.dbibih.miniprojet.serviceImpl.UserDetailsServiceImpl;
import ma.dbibih.miniprojet.serviceImpl.UserServiceImpl;
import ma.dbibih.miniprojet.utilities.AppUserDetails;
import ma.dbibih.miniprojet.utilities.BatchResponse;
import ma.dbibih.miniprojet.utilities.JwtResponse;
import ma.dbibih.miniprojet.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("api/users")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create a new User ! only users with role admin can access it")
    @PostMapping("/")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User Created successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "409", description = "User with same email or user name exists already !"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })

    public ResponseEntity<User> createUser(@RequestBody User user) {
        User response= userService.createUser(user);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @Operation(summary = "Return the list of users ! only users with role admin can access it!")
    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    public ResponseEntity<List<User>> getUsers() {
        List<User> response= userService.getUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get a number of recent users! Only users with the admin role can access it.")
    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    public ResponseEntity<List<User>> getRecentUsers(@RequestParam int count) {
        List<User> response= userService.getRecentUsers(count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Generate a json file named users.json that contain a given number of users.")
    @GetMapping("/generate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File Generated successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    public ResponseEntity<byte[]> generateUsers(@RequestParam int count) throws Exception {
        List<UserDTO> users = userService.generateRandomUsers(count);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(users);
        byte[] isr = json.getBytes();
        String fileName = "users.json";
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentLength(isr.length);
        respHeaders.setContentType(new MediaType("application", "json"));
        respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
    }

    @Operation(summary = "Batch users from json file to database ")
    @PostMapping(value = "/batch",consumes = {
            "multipart/form-data"
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users saved in database successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request ! only json files are accepted "),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    public ResponseEntity<BatchResponse> uploadUsers(@RequestParam("file") MultipartFile file) {
        BatchResponse response;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {
            };
            InputStream inputStream = file.getInputStream();
            List<User> users = mapper.readValue(inputStream, typeReference);
            response = userService.createUsers(users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            System.out.println("Unable to save users: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "Get the user Profile ! Only users with role admin can access it!  ")
    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Profile returned successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request ! Username entered may not be present in the database"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        try {
            User user = userService.getUserProfile(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UsernameNotFoundException ue) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get the authenticated user Profile!")
    @GetMapping("/me")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile returned successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    public ResponseEntity<User> getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            User user = userService.getUserProfile(auth.getName());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UsernameNotFoundException ue) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
