package GymApp.controller;

import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.dto.membership.GetMembershipsResponse;
import GymApp.dto.membership.MembershipDto;
import GymApp.service.AccountService;
import GymApp.service.CurrentUserService;
import GymApp.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    private final MembershipService membershipService;
    private final AccountService accountService;
    private final CurrentUserService currentUserService;

    public MembershipController(MembershipService membershipService, AccountService accountService,
                                CurrentUserService currentUserService
    ) {
        this.membershipService = membershipService;
        this.accountService = accountService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<CreateMembershipResponse> createMembership(
            @Valid @RequestBody CreateMembershipRequest req
    ) {
        CreateMembershipResponse response = membershipService.createMembership(req);
        return ResponseEntity.created(URI.create("api/membership/" + response.id())).body(response);
    }

    @GetMapping("/branch/{branchId}")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<GetMembershipsResponse> getMemberships(
            @PathVariable long branchId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String sort

    ) {
        // Check that the current user has access on the branch, will throw exception if not.
        accountService.hasAccessOnBranch(currentUserService.getCurrentUser().getId(), branchId);
        return ResponseEntity.ok(membershipService.getMemberships(branchId, page, size, sort));
    }

    @GetMapping("{membershipId}/branch/{branchId}")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<MembershipDto> getMembershipById(
            @PathVariable long branchId,
            @PathVariable long membershipId
    ) {
        // Check that the current user has access on the branch, will throw exception if not.
        accountService.hasAccessOnBranch(currentUserService.getCurrentUser().getId(), branchId);
        return ResponseEntity.ok(membershipService.getMembershipById(membershipId));
    }

}
