package GymApp.service.membership;

import GymApp.dao.MembershipRepository;
import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.dto.membership.GetMembershipsResponse;
import GymApp.entity.Account;
import GymApp.entity.Branch;
import GymApp.entity.Membership;
import GymApp.enums.MembershipType;
import GymApp.exception.NotFoundException;
import GymApp.service.AccountService;
import GymApp.service.BranchService;
import GymApp.service.CurrentUserService;
import GymApp.service.MembershipServiceImpl;
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
        Account nonClientAccount = getSampleNonClientAccount();

        // Prepare Client Account
        Account client = getSampleClient();

        // Prepare Branch object
        Branch branch = getSampleBranch();

        // Prepare the ( CreateMembershipRequest Object )
        CreateMembershipRequest request = getSampleMembershipRequest(client, branch);

        // Prepare the ( CreateMembershipResponse Object )
        CreateMembershipResponse ExpectedResponse = getSampleMembershipResponse(
                client,
                branch,
                nonClientAccount
        );

        // Prepare the ( Membership Object )
        Membership membership = getSampleMembership(client, nonClientAccount, branch);

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
    public void createMembershipReturns_ThrowsAccountNotFoundException() {
        // Get Sample Objects
        Account client = getSampleClient();
        Account nonClientAccount = getSampleNonClientAccount();
        Branch branch = getSampleBranch();

        // Prepare ( CreateMembershipRequest )
        CreateMembershipRequest request = getSampleMembershipRequest(client, branch);

        // Prepare the ( Membership Object )
        Membership membership = getSampleMembership(client, nonClientAccount, branch);

        // Mock the ( AccountService )
        when(accountService.findById(client.getId())).thenThrow(new NotFoundException(
                "Account with id 1 not found"));


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
        List<Membership> descendingMemberships = getMemberships()
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
                descendingMemberships.stream().map(Util::convertMembershipToMembershipDto).toList(),
                0,
                10,
                2L,
                1
        )).usingRecursiveComparison().isEqualTo(descendingResponse);

        GetMembershipsResponse ascendingResponse = service.getMemberships(1L, 0, 10, "startDate");

        assertThat(new GetMembershipsResponse(
                descendingMemberships.stream().map(Util::convertMembershipToMembershipDto).toList(),
                0,
                10,
                2L,
                1
        )).usingRecursiveComparison().isEqualTo(ascendingResponse);
    }

    /**
     * Provides a sample object of type ( CreateMembershipResponse )
     *
     */
    private CreateMembershipResponse getSampleMembershipResponse(
            Account client,
            Branch branch,
            Account nonClientAccount
    ) {
        return new CreateMembershipResponse(
                1L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                client.getId(),
                branch.getId(),
                nonClientAccount.getId()

        );
    }

    /**
     * Provides a sample object of type ( CreateMembershipRequest )
     *
     */
    private CreateMembershipRequest getSampleMembershipRequest(Account client, Branch branch) {
        return new CreateMembershipRequest(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                client.getId(),
                branch.getId()
        );
    }

    /**
     * Provides a sample object of type ( Membership )
     *
     */
    private Membership getSampleMembership(Account client, Account createdBy, Branch branch) {
        return new Membership(
                1L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                client,
                branch,
                createdBy,
                LocalDateTime.now()
        );

    }

    /**
     * Provides a sample object of type ( Account )
     *
     */
    private Account getSampleClient() {
        Account client = new Account();
        client.setId(2);
        return client;
    }

    /**
     * Provides a sample object of type ( Account )
     *
     */
    private Account getSampleNonClientAccount() {
        Account account = new Account();
        account.setId(1);
        return account;
    }

    /**
     * Provides a sample object of type ( Branch )
     *
     */
    private Branch getSampleBranch() {
        Branch branch = new Branch();
        branch.setId(1);
        return branch;
    }

    private List<Membership> getMemberships() {
        return List.of(
                new Membership.Builder()
                        .id(1L)
                        .startDate(LocalDate.of(2026, 1, 1))
                        .endDate(LocalDate.of(2027, 1, 1))
                        .isActive(true)
                        .membershipType(MembershipType.YEAR)
                        .createdAt(LocalDateTime.now())
                        .branch(new Branch.Builder().id(1L).name("branch 1").build())
                        .createdBy(new Account.Builder().id(1).build())
                        .client(new Account.Builder().id(2).build())
                        .build(),
                new Membership.Builder()
                        .id(2L)
                        .startDate(LocalDate.of(2026, 1, 1))
                        .endDate(LocalDate.of(2027, 1, 1))
                        .isActive(true)
                        .membershipType(MembershipType.YEAR)
                        .createdAt(LocalDateTime.now())
                        .branch(new Branch.Builder().id(2L).name("branch 2").build())
                        .createdBy(new Account.Builder().id(1).build())
                        .client(new Account.Builder().id(3).build())
                        .build()
        );

    }

}
