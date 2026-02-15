package GymApp.service;

import GymApp.dao.MembershipRepository;
import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.entity.Account;
import GymApp.entity.Branch;
import GymApp.entity.Membership;
import GymApp.enums.MembershipType;
import GymApp.exception.AccountNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        when(accountService.findById(client.getId())).thenThrow(new AccountNotFoundException(
                "Account with id 1 not found"));


        // Act & Assert
        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> service.createMembership(request)
        );

        // Verify
        Assertions.assertEquals("Account with id 1 not found", exception.getMessage());
        // Verify that the findById method of the account service is called once.
        verify(accountService).findById(client.getId());
        // Verify that the save method of the membership repository is never called.
        verify(membershipRepo, never()).save(any());

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
                createdBy
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


}
