package GymApp.controller;

import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.dto.membership.GetMembershipsResponse;
import GymApp.dto.membership.MembershipDto;
import GymApp.entity.Account;
import GymApp.entity.Branch;
import GymApp.entity.Membership;
import GymApp.enums.MembershipType;
import GymApp.exception.NotFoundException;
import GymApp.security.SecurityConfig;
import GymApp.security.authenticationProvider.AccountAuthenticationProviderService;
import GymApp.service.AccountService;
import GymApp.service.CurrentUserService;
import GymApp.service.MembershipService;

import GymApp.service.membership.Util;
import GymApp.util.entityAndDtoMappers.MembershipMapper;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static GymApp.service.membership.Util.getSampleMembership;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private AccountService accountService;

    @MockBean
    private CurrentUserService currentUserService;


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
        CreateMembershipRequest request = Util.getSampleMembershipRequest();

        // Prepare response body
        CreateMembershipResponse response = Util.getSampleMembershipResponse();
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
        CreateMembershipRequest request = Util.getSampleMembershipRequest();
        // Prepare response body
        CreateMembershipResponse response = Util.getSampleMembershipResponse();

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

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "SCOPE_ADMIN", "SCOPE_EMPLOYEE"
            }
    )
    void getMemberships_WithValidAuthority_Returns200Ok(String authority) throws Exception {
        // Mock membership service
        when(membershipService.getMemberships(
                any(),
                anyInt(),
                anyInt(),
                any()
        )).thenReturn(new GetMembershipsResponse(
                List.of(),
                10,
                10,
                10L,
                1
        ));

        // Mock AccountService
        when(accountService.hasAccessOnBranch(any(), any())).thenReturn(true);

        // Mock CurrentUserService
        when(currentUserService.getCurrentUser()).thenReturn(new Account.Builder().id(1).build());


        // Act & Assert
        mockMvc
                .perform(get("/api/membership/branch/1")
                                 .queryParam("page", "0")
                                 .queryParam("size", "10")
                                 .queryParam("sort", "startDate")
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority))))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "SCOPE_ADMIN", "SCOPE_EMPLOYEE"
            }
    )
    void getMemberships_ForUnAccessibleBranch_Returns404NotFound(String authority)
            throws Exception {
        // Mock membership service ( getMemberships method )
        when(membershipService.getMemberships(
                any(),
                anyInt(),
                anyInt(),
                any()
        )).thenReturn(new GetMembershipsResponse(
                List.of(),
                10,
                10,
                10L,
                1
        ));

        // Mock AccountService ( hasAccessOnBranch method )
        when(accountService.hasAccessOnBranch(any(), any())).thenThrow(new NotFoundException(""));

        // Mock CurrentUserService ( getCurrentUser method )
        when(currentUserService.getCurrentUser()).thenReturn(new Account.Builder().id(1).build());

        // Act & Assert
        mockMvc
                .perform(get("/api/membership/branch/1")
                                 .queryParam("page", "0")
                                 .queryParam("size", "10")
                                 .queryParam("sort", "startDate")
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority))))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource(
            {
                    "SCOPE_ADMIN, 0, , startDate",
                    "SCOPE_ADMIN, , 10, startDate",
                    "SCOPE_EMPLOYEE, 0, , startDate",
                    "SCOPE_EMPLOYEE, , 10, startDate",
            }
    )
    void getMemberships_WithInValidQueryParams_Returns404NotFound(
            String authority,
            String page,
            String size,
            String sort
    ) throws Exception {
        // Mock membership service ( getMemberships method )
        when(membershipService.getMemberships(
                any(),
                anyInt(),
                anyInt(),
                any()
        )).thenReturn(new GetMembershipsResponse(
                List.of(),
                10,
                10,
                10L,
                1
        ));

        // Mock AccountService ( hasAccessOnBranch method )
        when(accountService.hasAccessOnBranch(any(), any())).thenThrow(new NotFoundException(""));

        // Mock CurrentUserService ( getCurrentUser method )
        when(currentUserService.getCurrentUser()).thenReturn(new Account.Builder().id(1).build());


        // Act & Assert
        mockMvc
                .perform(get("/api/membership/branch/1")
                                 .queryParam("page", page)
                                 .queryParam("size", size)
                                 .queryParam("sort", sort)
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority))))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource(
            {
                    "SCOPE_CLIENT, 0, 10, startDate",
            }
    )
    void getMemberships_WithInValidAuthority_Returns403FORBIDDEN(
            String authority,
            String page,
            String size,
            String sort
    ) throws Exception {
        // Mock membership service ( getMemberships method )
        when(membershipService.getMemberships(
                any(),
                anyInt(),
                anyInt(),
                any()
        )).thenReturn(new GetMembershipsResponse(
                List.of(),
                10,
                10,
                10L,
                1
        ));

        // Mock AccountService ( hasAccessOnBranch method )
        when(accountService.hasAccessOnBranch(any(), any())).thenThrow(new NotFoundException(""));

        // Mock CurrentUserService ( getCurrentUser method )
        when(currentUserService.getCurrentUser()).thenReturn(new Account.Builder().id(1).build());


        // Act & Assert
        mockMvc
                .perform(get("/api/membership/branch/1")
                                 .queryParam("page", page)
                                 .queryParam("size", size)
                                 .queryParam("sort", sort)
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority))))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "SCOPE_ADMIN", "SCOPE_EMPLOYEE"
            }
    )
    void getMembershipById_WithValidAuthority_Returns200OK(String authority) throws Exception {
        // Prepare
        Membership membership = getSampleMembership();
        MembershipDto membershipDto = MembershipMapper.toMembershipDto(membership);

        // Mock membership service ( getMembershipById method )
        when(membershipService.getMembershipById(1L)).thenReturn(membershipDto);

        // Mock AccountService ( hasAccessOnBranch method )
        when(accountService.hasAccessOnBranch(
                membership.getCreatedBy().getId(),
                membership.getBranch().getId()
        )).thenReturn(true);

        // Mock CurrentUserService ( getCurrentUser method )
        when(currentUserService.getCurrentUser()).thenReturn(membership.getCreatedBy());


        // Act & Assert
        mockMvc
                .perform(get("/api/membership/" + membership.getId() + "/branch/1")
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority))))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(membershipDto)));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "SCOPE_CLIENT",
            }
    )
    void getMembershipById_WithInvalidAuthority_Returns403FORBIDDEN(String authority) throws Exception {
        // Prepare
        Membership membership = getSampleMembership();

        // Act & Assert
        mockMvc
                .perform(get("/api/membership/" + membership.getId() + "/branch/1")
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority))))
                .andExpect(status().isForbidden());
    }


    @ParameterizedTest
    @ValueSource(
            strings = {
                    "SCOPE_ADMIN", "SCOPE_EMPLOYEE"
            }
    )
    void getMembershipById_WithValidAuthority_Returns404NOTFOUND(String authority)
            throws Exception {
        // Prepare
        Membership membership = getSampleMembership();

        // Mock membership service ( getMembershipById method )
        when(membershipService.getMembershipById(1L)).thenThrow(new NotFoundException(
                "Couldn't find membership with Id: " + membership.getId()));

        // Mock AccountService ( hasAccessOnBranch method )
        when(accountService.hasAccessOnBranch(
                membership.getCreatedBy().getId(),
                membership.getBranch().getId()
        )).thenReturn(true);

        // Mock CurrentUserService ( getCurrentUser method )
        when(currentUserService.getCurrentUser()).thenReturn(membership.getCreatedBy());


        // Act & Assert
        mockMvc
                .perform(get("/api/membership/" + membership.getId() + "/branch/1")
                                 .with(csrf())
                                 .with(jwt().authorities(new SimpleGrantedAuthority(authority))))
                .andExpect(status().isNotFound());
    }
}
