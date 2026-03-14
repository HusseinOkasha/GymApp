package GymApp.controller;

import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.dto.membership.GetMembershipsResponse;
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

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
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
        return ResponseEntity.ok(membershipService.getMemberships(branchId, page, size, sort));
    }
}
