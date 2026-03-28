package GymApp.service.membership;

import GymApp.dao.MembershipRepository;
import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.dto.membership.GetMembershipsResponse;
import GymApp.dto.membership.MembershipDto;
import GymApp.entity.Account;
import GymApp.entity.Branch;
import GymApp.entity.Membership;
import GymApp.enums.MembershipType;
import GymApp.exception.NotFoundException;
import GymApp.service.AccountService;
import GymApp.service.BranchService;
import GymApp.service.CurrentUserService;
import GymApp.service.MembershipServiceImpl;
import GymApp.util.entityAndDtoMappers.MembershipMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {
    @Mock
    MembershipRepository membershipRepo;

    @Mock
    AccountService accountService;

    @Mock
    CurrentUserService currentUserService;

    @Mock
    BranchService branchService;


    @InjectMocks
    MembershipServiceImpl service;

    public MembershipServiceTest() {
    }

    @Test
    public void createMembershipReturns_CreatedMembership() {

        // Prepare ( Employee / Admin ) account
        Account nonClientAccount = Util.getSampleNonClientAccount();

        // Prepare Client Account
        Account client = Util.getSampleClient();

        // Prepare Branch object
        Branch branch = Util.getSampleBranch();

        // Prepare the ( CreateMembershipRequest Object )
        CreateMembershipRequest request = Util.getSampleMembershipRequest();

        // Prepare the ( CreateMembershipResponse Object )
        CreateMembershipResponse ExpectedResponse = Util.getSampleMembershipResponse();

        // Prepare the ( Membership Object )
        Membership membership = Util.getSampleMembership();

        // Mock the ( Membership Repository )
        when(membershipRepo.save(any())).thenReturn(membership);

        // Mock ( Current User Service )
        when(currentUserService.getCurrentUser()).thenReturn(nonClientAccount);

        // Mock the ( AccountService )
        when(accountService.findById(client.getId())).thenReturn(client);

        // Mock the ( BranchService )
        when(branchService.findBranchById(any())).thenReturn(branch);


        // Act
        CreateMembershipResponse response = service.createMembership(request);

        // Assert
        Assertions.assertEquals(ExpectedResponse, response);
    }

    @Test
    public void createMembershipThrows_AccountNotFoundException() {
        // Get Sample Objects
        Account client = Util.getSampleClient();
        Account nonClientAccount = Util.getSampleNonClientAccount();
        Branch branch = Util.getSampleBranch();

        // Prepare ( CreateMembershipRequest )
        CreateMembershipRequest request = Util.getSampleMembershipRequest();

        // Prepare the ( Membership Object )
        Membership membership = Util.getSampleMembership();

        // Mock the ( AccountService )
        when(accountService.findById(client.getId())).thenThrow(new NotFoundException(
                "Account with id 1 not found"));

        // Mock the ( CurrentUserService )
        when(currentUserService.getCurrentUser()).thenReturn(new Account.Builder().id(1).build());


        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.createMembership(request)
        );

        // Verify
        Assertions.assertEquals("Account with id 1 not found", exception.getMessage());
        // Verify that the findById method of the account service is called once.
        verify(accountService).findById(client.getId());
        // Verify that the save method of the membership repository is never called.
        verify(membershipRepo, never()).save(any());

    }

    @Test
    public void getMembershipsReturns_DescendingMemberships() {
        // Prepare
        Pageable pageable = PageRequest.of(0, 10, Sort.by("firstName"));
        List<Membership> descendingMemberships = Util.getMemberships()
                .stream()
                .sorted(Comparator.comparing(Membership::getStartDate).reversed())
                .toList();

        Page<Membership> descendingPage = new PageImpl<>(
                descendingMemberships,
                pageable,
                descendingMemberships.size()
        );

        // Mock membership repository
        when(membershipRepo.findAllByBranch_Id(
                any(),
                eq(PageRequest.of(
                        0,
                        10,
                        Sort.by("startDate").descending()
                ))
        )).thenReturn(descendingPage);

        GetMembershipsResponse descendingResponse = service.getMemberships(1L, 0, 10, "startDate");

        assertThat(new GetMembershipsResponse(
                descendingMemberships.stream().map(MembershipMapper::toMembershipDto).toList(),
                0,
                10,
                2L,
                1
        )).usingRecursiveComparison().isEqualTo(descendingResponse);

        GetMembershipsResponse ascendingResponse = service.getMemberships(1L, 0, 10, "startDate");

        assertThat(new GetMembershipsResponse(
                descendingMemberships.stream().map(MembershipMapper::toMembershipDto).toList(),
                0,
                10,
                2L,
                1
        )).usingRecursiveComparison().isEqualTo(ascendingResponse);
    }

    @Test
    public void getMembershipById_ReturnsCorrectMembership() {
        // Prepare
        Membership membership = Util.getSampleMembership();
        MembershipDto expectedDto = MembershipMapper.toMembershipDto(membership);

        // Mock membership repo (findById method)
        when(membershipRepo.findById(membership.getId())).thenReturn(Optional.of(membership));

        // Act
        MembershipDto actualDto = service.getMembershipById(membership.getId());

        // Assert
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);

    }

    @Test
    public void getMembershipById_WithNonExistingId_ThrowsNotFoundException() {
        // Prepare
        Membership membership = Util.getSampleMembership();

        // Mock membership repo (findById method)
        when(membershipRepo.findById(membership.getId())).thenThrow(new NotFoundException(
                "Couldn't find membership with Id: " + membership.getId()));

        // Act
        assertThrows(NotFoundException.class, () -> service.getMembershipById(membership.getId()));
    }

}
