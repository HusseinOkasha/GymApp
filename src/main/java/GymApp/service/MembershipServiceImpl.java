package GymApp.service;

import GymApp.dao.MembershipRepository;
import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.entity.Account;
import GymApp.entity.Branch;
import GymApp.entity.Membership;
import org.springframework.stereotype.Service;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final AccountService accountService;
    private final BranchService branchService;

    public MembershipServiceImpl(
            MembershipRepository membershipRepository,
            AccountService accountService, BranchService branchService
    ) {
        this.membershipRepository = membershipRepository;
        this.accountService = accountService;
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


        // Get the Account
        Account account = accountService.findById(dto.clientId());
        // Set client for membership.
        membership.setClient(account);

        // Get the branch
        Branch branch = branchService.findBranchById(dto.branchId());
        // Set branch for membership
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
                createdMembership.getBranch().getId()
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
