package GymApp.dto.membership;

import GymApp.enums.MembershipType;

import java.time.LocalDate;

public record CreateMembershipResponse(
        Long id, LocalDate startDate, LocalDate endDate, Boolean isActive, MembershipType type,
        Long clientId
) {
}
