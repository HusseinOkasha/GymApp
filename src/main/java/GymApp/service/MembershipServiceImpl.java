package GymApp.service;

import GymApp.dao.MembershipRepository;
import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.entity.Account;
import GymApp.entity.Membership;
import org.springframework.stereotype.Service;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final AccountService accountService;

    public MembershipServiceImpl(
            MembershipRepository membershipRepository,
            AccountService accountService
    ) {
        this.membershipRepository = membershipRepository;
        this.accountService = accountService;
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
        membership.setClient(account);

        // Persist membership to the database.
        Membership createdMembership = membershipRepository.save(membership);

        // Map "Created Membership" to "CreateMembershipResponse" dto.
        return new CreateMembershipResponse(
                createdMembership.getId(),
                createdMembership.getStartDate(),
                createdMembership.getEndDate(),
                createdMembership.isActive(),
                createdMembership.getType(),
                createdMembership.getClient().getId()
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
