package GymApp.dto.membership;

import GymApp.enums.MembershipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateMembershipRequest(
        @NotNull LocalDate startDate, @NotNull LocalDate endDate, @NotNull Boolean isActive,
        @NotNull MembershipType type, @NotNull Long clientId, @NotNull Long branchId
) {
}
