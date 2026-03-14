package GymApp.service;

import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.dto.membership.GetMembershipsResponse;
import GymApp.entity.Membership;

public interface MembershipService {
    CreateMembershipResponse createMembership(CreateMembershipRequest dto);
    GetMembershipsResponse getMemberships(Long branchId, int page, int size, String sort);
    void getMembershipById(Long membershipId);
    void getMembershipByAccountId(Long AccountId);
    void deleteMembershipById(Long membershipId);
}
