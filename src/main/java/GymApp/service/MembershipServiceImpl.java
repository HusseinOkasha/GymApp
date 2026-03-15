package GymApp.service;

import GymApp.dao.MembershipRepository;
import GymApp.dto.membership.CreateMembershipRequest;
import GymApp.dto.membership.CreateMembershipResponse;
import GymApp.dto.membership.GetMembershipsResponse;
import GymApp.dto.membership.MembershipDto;
import GymApp.entity.Account;
import GymApp.entity.Branch;
import GymApp.entity.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final AccountService accountService;
    private final CurrentUserService currentUserService;
    private final BranchService branchService;

    public MembershipServiceImpl(
            MembershipRepository membershipRepository,
            AccountService accountService,
            CurrentUserService currentUserService,
            BranchService branchService
    ) {
        this.membershipRepository = membershipRepository;
        this.accountService = accountService;
        this.currentUserService = currentUserService;
        this.branchService = branchService;
    }

    @Override
    public CreateMembershipResponse createMembership(CreateMembershipRequest dto) {
        // Check that the current user (EMPLOYEE / Admin) has access on the branch
        accountService.hasAccessOnBranch(
                currentUserService.getCurrentUser().getId(),
                dto.branchId()
        );

        // Map "CreateMembershipRequest" to "Membership" entity.
        Membership membership = new Membership();
        membership.setStartDate(dto.startDate());
        membership.setEndDate(dto.endDate());
        membership.setActive(dto.isActive());
        membership.setType(dto.type());


        // Set client for membership.
        Account account = accountService.findById(dto.clientId());
        membership.setClient(account);

        // Set Creator for the membership.
        Account creator = currentUserService.getCurrentUser();
        membership.setCreatedBy(creator);

        // Set branch for membership
        Branch branch = branchService.findBranchById(dto.branchId());
        membership.setBranch(branch);

        // Persist membership to the database.
        Membership createdMembership = membershipRepository.save(membership);

        // Map "Created Membership" to "CreateMembershipResponse" dto.
        return new CreateMembershipResponse(
                createdMembership.getId(),
                createdMembership.getStartDate(),
                createdMembership.getEndDate(),
                createdMembership.isActive(),
                createdMembership.getType(),
                createdMembership.getClient().getId(),
                createdMembership.getBranch().getId(),
                createdMembership.getCreatedBy().getId()
        );
    }

    @Override
    public GetMembershipsResponse getMemberships(Long branchId, int page, int size, String sort) {

        // Set Default Sort
        sort = sort==null || sort.isBlank()  ? "startDate" : sort ;

        // Get memberships of certain branch
        Page<Membership> membershipsPage = this.membershipRepository.findAllByBranch_Id(
                branchId,
                PageRequest.of(
                        page,
                        size,
                        Sort
                                .by(sort)
                                .descending()
                )
        );
        return new GetMembershipsResponse(
                // Convert memberships to membershipDto
                membershipsPage.get().map(membership -> new MembershipDto(
                        membership.getId(),
                        membership.getStartDate(),
                        membership.getEndDate(),
                        membership.isActive(),
                        membership.getType(),
                        membership.getClient().getId(),
                        membership.getClient().getId(),
                        membership.getCreatedBy().getId()
                )).toList(), membershipsPage.getNumber(), // set the page number
                membershipsPage.getSize(), // set the page size
                membershipsPage.getTotalElements(), // set total number of memberships
                membershipsPage.getTotalPages() // set the total number of pages
        );

    }

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
