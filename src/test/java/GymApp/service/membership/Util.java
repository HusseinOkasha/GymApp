package GymApp.service.membership;

import GymApp.dto.membership.MembershipDto;
import GymApp.entity.Membership;

public class Util {
    public static MembershipDto convertMembershipToMembershipDto(Membership membership) {
        return new MembershipDto(
                membership.getId(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.isActive(),
                membership.getType(),
                membership.getClient().getId(),
                membership.getBranch().getId(),
                membership.getCreatedBy().getId()
        );
    }
}
