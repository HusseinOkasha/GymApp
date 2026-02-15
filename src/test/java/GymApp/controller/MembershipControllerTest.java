package GymApp.controller;

import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.enums.MembershipType;
import GymApp.security.SecurityConfig;
import GymApp.security.authenticationProvider.AccountAuthenticationProviderService;
import GymApp.service.MembershipService;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MembershipController.class)
@Import(SecurityConfig.class)
public class MembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private MembershipService membershipService;

    @MockBean
    private AccountAuthenticationProviderService accountAuthenticationProviderService;


    @ParameterizedTest
    @ValueSource(
            strings = {
                    "SCOPE_ADMIN", "SCOPE_EMPLOYEE"
            }
    )
    void createMembership_withInvalidDate_Return400BadRequest(String authority) throws Exception {
        mockMvc
                .perform(post("/api/membership")
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority)))
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content("""
                                                      {
                                                        "startDate": "2026-01-01",
                                                        "endDate": "2027-01-01",
                                                        "isActive": true,
                                                        "type":"YEAR"
                                                      }
                                                  """))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "SCOPE_ADMIN", "SCOPE_EMPLOYEE"
            }
    )
    void createMembership_WithValidAuthority_Return201Created(String authority) throws Exception {

        // Prepare Request body
        CreateMembershipRequest request = getSampleMembershipRequest();

        // Prepare response body
        CreateMembershipResponse response = getSampleMembershipResponse();
        // Mock membership service
        when(membershipService.createMembership(any())).thenReturn(response);

        // Act & Assert
        mockMvc
                .perform(post("/api/membership")
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority)))
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "SCOPE_CLIENT",
            }
    )
    void createMembership_WithInValidAuthority_Return403Unauthorized(String authority)
            throws Exception {

        // Prepare Request body
        CreateMembershipRequest request = getSampleMembershipRequest();
        // Prepare response body
        CreateMembershipResponse response = getSampleMembershipResponse();

        // Mock membership service
        when(membershipService.createMembership(any())).thenReturn(response);

        // Act & Assert
        mockMvc
                .perform(post("/api/membership")
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority)))
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    /**
     * Provides a sample object of type ( CreateMembershipResponse )
     *
     */
    private CreateMembershipResponse getSampleMembershipResponse(){
        return new CreateMembershipResponse(
                1L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                (long) 1,
                1L,
                1L

        );
    }

    /**
     * Provides a sample object of type ( CreateMembershipRequest )
     *
     */
    private CreateMembershipRequest getSampleMembershipRequest(){
        return new CreateMembershipRequest(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                (long) 1,
                1L
        );
    }
}
