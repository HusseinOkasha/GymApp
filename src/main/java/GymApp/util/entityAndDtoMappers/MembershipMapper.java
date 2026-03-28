package GymApp.util.entityAndDtoMappers;

import GymApp.dto.membership.MembershipDto;
import GymApp.entity.Membership;

public class MembershipMapper {
    public static MembershipDto toMembershipDto(Membership membership) {
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
