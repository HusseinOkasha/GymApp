package GymApp.service.membership;

import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.dto.membership.MembershipDto;
import GymApp.entity.Account;
import GymApp.entity.Branch;
import GymApp.entity.Membership;
import GymApp.enums.MembershipType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Util {
    public static Membership getSampleMembership() {
        Account client = getSampleClient();
        Account createdBy = getSampleNonClientAccount();
        Branch branch = getSampleBranch();
        return new Membership(
                1L,
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                true,
                MembershipType.YEAR,
                client,
                branch,
                createdBy,
                LocalDateTime.now()
        );
    }

    public static Branch getSampleBranch() {
        return new Branch.Builder().id(1L).build();
    }

    public static Account getSampleClient() {
        return new Account.Builder().id(2).build();
    }

    public static Account getSampleNonClientAccount() {
        return new Account.Builder().id(1).build();
    }

    /**
     * Provides a sample object of type ( CreateMembershipResponse )
     *
     */
    public static CreateMembershipResponse getSampleMembershipResponse() {
        Account client = getSampleClient();
        Branch branch = getSampleBranch();
        Account createdBy = getSampleNonClientAccount();
        return new CreateMembershipResponse(
                1L,
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                true,
                MembershipType.YEAR,
                client.getId(),
                branch.getId(),
                createdBy.getId()
        );
    }

    /**
     * Provides a sample object of type ( CreateMembershipRequest )
     *
     */
    public static CreateMembershipRequest getSampleMembershipRequest() {
        Account client = getSampleClient();
        Branch branch = getSampleBranch();
        return new CreateMembershipRequest(
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                true,
                MembershipType.YEAR,
                client.getId(),
                branch.getId()
        );
    }

    public static List<Membership> getMemberships() {
        return List.of(
                new Membership.Builder()
                        .id(1L)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusYears(1))
                        .isActive(true)
                        .membershipType(MembershipType.YEAR)
                        .createdAt(LocalDateTime.now())
                        .branch(new Branch.Builder().id(1L).name("branch 1").build())
                        .createdBy(new Account.Builder().id(1).build())
                        .client(new Account.Builder().id(2).build())
                        .build(),
                new Membership.Builder()
                        .id(2L)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusYears(1))
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
