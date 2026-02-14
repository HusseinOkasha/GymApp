package GymApp.service;

import GymApp.dao.MembershipRepository;
import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.entity.Account;
import GymApp.entity.Branch;
import GymApp.entity.Membership;
import GymApp.security.userDetails.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final AccountService accountService;
    private final CurrentUserService currentUserService;
    private final BranchService branchService;

    public MembershipServiceImpl(
            MembershipRepository membershipRepository,
            AccountService accountService, CurrentUserService currentUserService, BranchService branchService
    ) {
        this.membershipRepository = membershipRepository;
        this.accountService = accountService;
        this.currentUserService = currentUserService;
        this.branchService = branchService;
    }

    @Override
    public CreateMembershipResponse createMembership(CreateMembershipRequest dto) {

        // Map "CreateMembershipRequest" to "Membership" entity.
        Membership membership = new Membership();
        membership.setStartDate(dto.startDate());
        membership.setEndDate(dto.endDate());
        membership.setActive(dto.isActive());
        membership.setType(dto.type());


        // Set client for membership.
        Account account = accountService.findById(dto.clientId());
        membership.setClient(account);

        // Set Creator for the membership.
        Account creator = currentUserService.getCurrentUser();
        membership.setCreatedBy(creator);

        // Set branch for membership
        Branch branch = branchService.findBranchById(dto.branchId());
        membership.setBranch(branch);

        // Persist membership to the database.
        Membership createdMembership = membershipRepository.save(membership);

        // Map "Created Membership" to "CreateMembershipResponse" dto.
        return new CreateMembershipResponse(
                createdMembership.getId(),
                createdMembership.getStartDate(),
                createdMembership.getEndDate(),
                createdMembership.isActive(),
                createdMembership.getType(),
                createdMembership.getClient().getId(),
                createdMembership.getBranch().getId(),
                createdMembership.getCreatedBy().getId()
        );
    }

//    @Override
//    public void updateMembership(MembershipDto dto) {
//
//    }

    @Override
    public void getMembershipById(Long membershipId) {

    }

    @Override
    public void getMembershipByAccountId(Long AccountId) {

    }

    @Override
    public void deleteMembershipById(Long membershipId) {

    }
}
