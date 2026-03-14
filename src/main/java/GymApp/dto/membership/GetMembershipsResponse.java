package GymApp.dto.membership;

import java.util.List;

public record GetMembershipsResponse(
        List<MembershipDto> data, int page, int size, Long total, int totalPages
) {
}
