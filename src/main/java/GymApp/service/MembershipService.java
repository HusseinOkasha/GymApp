package GymApp.service;

import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.entity.Membership;

public interface MembershipService {
    CreateMembershipResponse createMembership(CreateMembershipRequest dto);
//    UpdateMembershipResponse updateMembership(UpdateMembershipRequest dto);
    void getMembershipById(Long membershipId);
    void getMembershipByAccountId(Long AccountId);
    void deleteMembershipById(Long membershipId);
}
