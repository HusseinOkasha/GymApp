package GymApp.service;

import GymApp.dao.MembershipRepository;
import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.entity.Account;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {
    @Mock
    MembershipRepository membershipRepo;

    @Mock
    AccountService accountService;
    @InjectMocks
    MembershipServiceImpl service;

    public MembershipServiceTest() {
    }

    @Test
    public void createMembershipReturns_CreatedMembership() {
        // Prepare the ( CreateMembershipRequest Object )
        CreateMembershipRequest request = new CreateMembershipRequest(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                (long) 1
        );
        // Prepare the ( CreateMembershipResponse Object )
        CreateMembershipResponse ExpectedResponse = new CreateMembershipResponse(
                (long) 0,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                (long) 1
        );

        // Prepare the ( Membership Object )
        Account client = new Account();
        client.setId(1);
        Membership membership = new Membership(
                null,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                client
        );

        // Mock the ( Membership Repository )
        when(membershipRepo.save(membership)).thenReturn(new Membership(
                (long)0,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                client

        ));

        // Mock the ( AccountService )
        when(accountService.findById(1)).thenReturn(client);

        // Act
        CreateMembershipResponse response = service.createMembership(request);

        // Assert
        Assertions.assertEquals(ExpectedResponse, response);
    }

    @Test
    public void createMembershipReturns_ThrowsAccountNotFoundException() {
        // Prepare the ( CreateMembershipRequest Object )
        CreateMembershipRequest request = new CreateMembershipRequest(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                (long) 1
        );

        // Prepare the ( Membership Object )
        Account client = new Account();
        client.setId(1);
        Membership membership = new Membership(
                null,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                true,
                MembershipType.YEAR,
                client
        );

        // Mock the ( AccountService )
        when(accountService.findById(1)).thenThrow(new AccountNotFoundException("Account with id 1 not found"));


        // Act & Assert
        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> service.createMembership(request)
        );

        // Verify
        Assertions.assertEquals("Account with id 1 not found", exception.getMessage());
        // Verify that the findById method of the account service is called once.
        verify(accountService).findById(1L);
        // Verify that the save method of the membership repository is never called.
        verify(membershipRepo, never()).save(any());

    }


}
